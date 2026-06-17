<script setup>
import { onMounted } from 'vue'
import { CircleDollarSign, RefreshCcw, XCircle } from 'lucide-vue-next'
import { useRailwayStore } from '../services/railwayStore'

const store = useRailwayStore()

onMounted(async () => {
  await store.loadOrders()
})
</script>

<template>
  <section class="panel">
    <div class="panel-title">
      <div>
        <h2>我的订单</h2>
        <p>待支付订单可以支付或取消。</p>
      </div>
      <el-button :icon="RefreshCcw" :loading="store.loading.orders" @click="store.loadOrders">刷新订单</el-button>
    </div>
    <el-table :data="store.orders.value" v-loading="store.loading.orders" row-key="id">
      <el-table-column prop="orderNo" label="订单号" min-width="210" />
      <el-table-column prop="status" label="状态" min-width="130">
        <template #default="{ row }">
          <el-tag :type="store.statusType(row.status)">{{ row.status }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="totalAmount" label="金额" min-width="90" />
      <el-table-column prop="createdAt" label="创建时间" min-width="190" />
      <el-table-column label="操作" width="190" fixed="right">
        <template #default="{ row }">
          <el-button
            type="success"
            :icon="CircleDollarSign"
            :disabled="row.status !== 'PENDING_PAYMENT'"
            @click="store.payOrder(row.id)"
          >
            支付
          </el-button>
          <el-button
            type="danger"
            :icon="XCircle"
            :disabled="row.status !== 'PENDING_PAYMENT'"
            @click="store.cancelOrder(row.id)"
          >
            取消
          </el-button>
        </template>
      </el-table-column>
    </el-table>
  </section>
</template>
