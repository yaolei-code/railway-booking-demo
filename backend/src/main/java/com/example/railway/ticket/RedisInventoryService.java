package com.example.railway.ticket;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

/**
 * Redis 库存缓存服务。
 * <p>
 * 设计思路：
 * 1. Redis 作为"快车道"——查票和锁票优先走 Redis，比数据库快两个数量级。
 * 2. Lua 脚本保证 Redis 内的 check-and-deduct 是原子操作，不会超卖。
 * 3. 数据库仍是"事实来源"——Redis 扣减成功后再走数据库锁，如果数据库失败则回滚 Redis。
 * 4. Redis 不可用时自动降级到纯数据库模式，不影响业务可用性。
 * <p>
 * Redis 数据结构：Hash {@code inventory:{id}}
 * 字段：availableCount, lockedCount, totalCount, price
 */
@Service
public class RedisInventoryService {

    private static final Logger log = LoggerFactory.getLogger(RedisInventoryService.class);

    private static final String KEY_PREFIX = "inventory:";

    // ---------- Lua 脚本（保证 Redis 内的 check-and-deduct 原子性）----------

    /** 原子锁票：检查可用票数 > 0 后扣减。返回 1=成功, 0=售罄。 */
    private static final String LOCK_LUA = """
            local key = KEYS[1]
            local available = redis.call('HGET', key, 'availableCount')
            if not available or tonumber(available) <= 0 then
                return 0
            end
            redis.call('HINCRBY', key, 'availableCount', -1)
            redis.call('HINCRBY', key, 'lockedCount', 1)
            return 1
            """;

    /** 原子确认（支付后）：lockedCount - 1。 */
    private static final String CONFIRM_LUA = """
            local key = KEYS[1]
            local locked = redis.call('HGET', key, 'lockedCount')
            if not locked or tonumber(locked) <= 0 then
                return 0
            end
            redis.call('HINCRBY', key, 'lockedCount', -1)
            return 1
            """;

    /** 原子释放（取消/超时）：availableCount + 1, lockedCount - 1。 */
    private static final String RELEASE_LUA = """
            local key = KEYS[1]
            local locked = redis.call('HGET', key, 'lockedCount')
            if not locked or tonumber(locked) <= 0 then
                return 0
            end
            redis.call('HINCRBY', key, 'availableCount', 1)
            redis.call('HINCRBY', key, 'lockedCount', -1)
            return 1
            """;

    private final StringRedisTemplate redis;
    private final TicketInventoryMapper inventoryMapper;
    private final DefaultRedisScript<Long> lockScript;
    private final DefaultRedisScript<Long> confirmScript;
    private final DefaultRedisScript<Long> releaseScript;

    public RedisInventoryService(StringRedisTemplate redis, TicketInventoryMapper inventoryMapper) {
        this.redis = redis;
        this.inventoryMapper = inventoryMapper;
        this.lockScript = new DefaultRedisScript<>(LOCK_LUA, Long.class);
        this.confirmScript = new DefaultRedisScript<>(CONFIRM_LUA, Long.class);
        this.releaseScript = new DefaultRedisScript<>(RELEASE_LUA, Long.class);
    }

    // ---------- 启动预热 ----------

    /** 启动时尝试把数据库中的所有库存加载到 Redis。Redis 没就绪则静默跳过。 */
    @PostConstruct
    void warmUp() {
        try {
            List<TicketInventory> all = inventoryMapper.selectList(null);
            if (!all.isEmpty()) {
                loadAll(all);
            }
        } catch (Exception e) {
            log.info("Redis warm-up skipped (Redis not ready): {}", e.getMessage());
        }
    }

    // ---------- 写入 ----------

    /** 将一条库存记录写入 Redis Hash。 */
    public void saveInventory(TicketInventory inventory) {
        try {
            String key = inventoryKey(inventory.getId());
            redis.opsForHash().put(key, "availableCount", String.valueOf(inventory.getAvailableCount()));
            redis.opsForHash().put(key, "lockedCount", String.valueOf(inventory.getLockedCount()));
            redis.opsForHash().put(key, "totalCount", String.valueOf(inventory.getTotalCount()));
            redis.opsForHash().put(key, "price", inventory.getPrice().toString());
        } catch (Exception e) {
            log.warn("Redis save failed (ignored): inventoryId={}", inventory.getId(), e);
        }
    }

    /** 从数据库批量加载库存到 Redis。 */
    public void loadAll(List<TicketInventory> inventories) {
        for (TicketInventory inv : inventories) {
            saveInventory(inv);
        }
        log.info("Redis inventory loaded: {} records", inventories.size());
    }

    // ---------- 原子操作 ----------

    /**
     * Redis 原子锁 1 张票。
     * 
     * @return true=锁成功, false=Redis 判定售罄
     * @throws RuntimeException Redis 连接失败（调用方应降级到纯数据库）
     */
    public boolean lockOne(Long inventoryId) {
        Long result = redis.execute(lockScript, Collections.singletonList(inventoryKey(inventoryId)));
        return Long.valueOf(1).equals(result);
    }

    /** Redis 原子确认（支付后）。即使 Redis 失败也不影响业务，静默跳过。 */
    public void confirmOne(Long inventoryId) {
        try {
            redis.execute(confirmScript, Collections.singletonList(inventoryKey(inventoryId)));
        } catch (Exception e) {
            log.warn("Redis confirm failed (ignored): inventoryId={}", inventoryId, e);
        }
    }

    /** Redis 原子释放（取消/超时）。即使 Redis 失败也不影响业务，静默跳过。 */
    public void releaseOne(Long inventoryId) {
        try {
            redis.execute(releaseScript, Collections.singletonList(inventoryKey(inventoryId)));
        } catch (Exception e) {
            log.warn("Redis release failed (ignored): inventoryId={}", inventoryId, e);
        }
    }

    // ---------- 查询 ----------

    /**
     * 读取 Redis 中的可用票数。
     * 
     * @return 可用票数，key 不存在返回 null
     * @throws RuntimeException Redis 连接失败（调用方应降级到 DB）
     */
    public Integer getAvailableCount(Long inventoryId) {
        Object val = redis.opsForHash().get(inventoryKey(inventoryId), "availableCount");
        if (val == null) {
            return null;
        }
        return Integer.parseInt(val.toString());
    }

    // ---------- 内部 ----------

    private static String inventoryKey(Long inventoryId) {
        return KEY_PREFIX + inventoryId;
    }
}
