<script setup>
import { onMounted } from 'vue'
import { RefreshCcw, Search, Ticket } from 'lucide-vue-next'
import { useRailwayStore } from '../services/railwayStore'

const store = useRailwayStore()

onMounted(async () => {
  if (store.stations.value.length === 0) {
    await store.loadStations()
  }
  if (store.searchForm.departureStationId && store.searchForm.arrivalStationId && store.tickets.value.length === 0) {
    await store.searchTickets()
  }
})
</script>

<template>
  <section class="work-grid">
    <div class="panel">
      <div class="panel-title">
        <div>
          <h2>查票</h2>
          <p>使用后端演示数据可以直接查北京南到上海虹桥。</p>
        </div>
        <el-button :icon="RefreshCcw" :loading="store.loading.stations" @click="store.loadStations">刷新车站</el-button>
      </div>

      <el-form label-position="top" class="search-form">
        <el-form-item label="出发站">
          <el-select v-model="store.searchForm.departureStationId" placeholder="选择出发站">
            <el-option
              v-for="station in store.stations.value"
              :key="station.id"
              :label="`${station.name} (${station.city})`"
              :value="station.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="到达站">
          <el-select v-model="store.searchForm.arrivalStationId" placeholder="选择到达站">
            <el-option
              v-for="station in store.stations.value"
              :key="station.id"
              :label="`${station.name} (${station.city})`"
              :value="station.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="日期">
          <el-date-picker
            v-model="store.searchForm.travelDate"
            type="date"
            value-format="YYYY-MM-DD"
            placeholder="选择日期"
          />
        </el-form-item>
        <el-form-item label="座位类型">
          <el-select v-model="store.searchForm.seatType" clearable placeholder="不限">
            <el-option label="二等座" value="SECOND_CLASS" />
            <el-option label="一等座" value="FIRST_CLASS" />
            <el-option label="商务座" value="BUSINESS_CLASS" />
          </el-select>
        </el-form-item>
      </el-form>

      <el-button type="primary" :icon="Search" :loading="store.loading.tickets" @click="store.searchTickets">
        查询
      </el-button>
    </div>

    <div class="panel">
      <div class="panel-title">
        <div>
          <h2>乘车人</h2>
          <p>下单时会把这里的信息写入订单明细。</p>
        </div>
      </div>
      <el-form label-position="top">
        <el-form-item label="姓名">
          <el-input v-model="store.passengerForm.passengerName" />
        </el-form-item>
        <el-form-item label="证件号">
          <el-input v-model="store.passengerForm.passengerIdNo" />
        </el-form-item>
      </el-form>
    </div>
  </section>

  <section class="panel">
    <div class="panel-title">
      <div>
        <h2>车票结果</h2>
        <p>查到票后可以直接创建待支付订单。</p>
      </div>
    </div>
    <el-table :data="store.tickets.value" v-loading="store.loading.tickets" row-key="inventoryId">
      <el-table-column prop="trainNo" label="车次" min-width="90" />
      <el-table-column prop="departureStationName" label="出发站" min-width="110" />
      <el-table-column prop="arrivalStationName" label="到达站" min-width="110" />
      <el-table-column prop="departureTime" label="出发" min-width="90" />
      <el-table-column prop="arrivalTime" label="到达" min-width="90" />
      <el-table-column prop="durationMinutes" label="历时(分钟)" min-width="110" />
      <el-table-column prop="seatType" label="座位" min-width="120" />
      <el-table-column prop="availableCount" label="余票" min-width="90" />
      <el-table-column prop="price" label="价格" min-width="90" />
      <el-table-column label="操作" width="130" fixed="right">
        <template #default="{ row }">
          <el-button type="primary" :icon="Ticket" @click="store.createOrder(row)">下单</el-button>
        </template>
      </el-table-column>
    </el-table>
  </section>
</template>
