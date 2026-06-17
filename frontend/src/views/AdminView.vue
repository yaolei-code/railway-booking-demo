<script setup>
import { onMounted } from 'vue'
import {
  CalendarPlus,
  PackagePlus,
  Pencil,
  Plus,
  RefreshCcw,
  Route,
  Save,
  Trash2
} from 'lucide-vue-next'
import { useRailwayStore } from '../services/railwayStore'

const store = useRailwayStore()

onMounted(async () => {
  if (store.stations.value.length === 0) await store.loadStations()
  if (store.trains.value.length === 0) await store.loadTrains()
  if (store.selectedTrainId.value) await store.loadTrainStations()
})
</script>

<template>
  <section class="admin-section">
    <div class="section-heading">
      <h2>管理台</h2>
      <div class="heading-actions">
        <el-button :icon="RefreshCcw" :loading="store.loading.stations" @click="store.loadStations">车站</el-button>
        <el-button :icon="RefreshCcw" :loading="store.loading.trains" @click="store.loadTrains">车次</el-button>
      </div>
    </div>

    <div class="work-grid">
      <div class="panel">
        <div class="panel-title">
          <div>
            <h2>车站维护</h2>
            <p>维护站名、城市和站点编码。</p>
          </div>
          <el-button :icon="Plus" @click="store.resetStationForm">新建</el-button>
        </div>

        <el-form label-position="top" class="compact-form">
          <el-form-item label="站名">
            <el-input v-model="store.stationForm.name" placeholder="北京南" />
          </el-form-item>
          <el-form-item label="城市">
            <el-input v-model="store.stationForm.city" placeholder="北京" />
          </el-form-item>
          <el-form-item label="编码">
            <el-input v-model="store.stationForm.code" placeholder="BJN" />
          </el-form-item>
        </el-form>
        <el-button type="primary" :icon="Save" :loading="store.loading.stations" @click="store.saveStation">
          {{ store.stationForm.id ? '保存车站' : '创建车站' }}
        </el-button>

        <el-table :data="store.stations.value" v-loading="store.loading.stations" row-key="id" class="admin-table">
          <el-table-column prop="name" label="站名" min-width="110" />
          <el-table-column prop="city" label="城市" min-width="90" />
          <el-table-column prop="code" label="编码" min-width="90" />
          <el-table-column label="操作" width="160" fixed="right">
            <template #default="{ row }">
              <el-button :icon="Pencil" @click="store.editStation(row)" />
              <el-button type="danger" :icon="Trash2" @click="store.deleteStation(row)" />
            </template>
          </el-table-column>
        </el-table>
      </div>

      <div class="panel">
        <div class="panel-title">
          <div>
            <h2>车次维护</h2>
            <p>维护车次号、类型和运营状态。</p>
          </div>
          <el-button :icon="Plus" @click="store.resetTrainForm">新建</el-button>
        </div>

        <el-form label-position="top" class="compact-form">
          <el-form-item label="车次号">
            <el-input v-model="store.trainForm.trainNo" placeholder="G101" />
          </el-form-item>
          <el-form-item label="类型">
            <el-select v-model="store.trainForm.trainType">
              <el-option label="高铁 G" value="G" />
              <el-option label="动车 D" value="D" />
              <el-option label="普通 K" value="K" />
            </el-select>
          </el-form-item>
          <el-form-item label="状态">
            <el-select v-model="store.trainForm.status">
              <el-option label="ACTIVE" value="ACTIVE" />
              <el-option label="INACTIVE" value="INACTIVE" />
            </el-select>
          </el-form-item>
        </el-form>
        <el-button type="primary" :icon="Save" :loading="store.loading.trains" @click="store.saveTrain">
          {{ store.trainForm.id ? '保存车次' : '创建车次' }}
        </el-button>

        <el-table :data="store.trains.value" v-loading="store.loading.trains" row-key="id" class="admin-table">
          <el-table-column prop="trainNo" label="车次" min-width="90" />
          <el-table-column prop="trainType" label="类型" min-width="80" />
          <el-table-column prop="status" label="状态" min-width="110">
            <template #default="{ row }">
              <el-tag :type="row.status === 'ACTIVE' ? 'success' : 'info'">{{ row.status }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="160" fixed="right">
            <template #default="{ row }">
              <el-button :icon="Pencil" @click="store.editTrain(row)" />
              <el-button type="danger" :icon="Trash2" @click="store.deleteTrain(row)" />
            </template>
          </el-table-column>
        </el-table>
      </div>
    </div>

    <section class="panel">
      <div class="panel-title">
        <div>
          <h2>经停车站</h2>
          <p>按站序一次保存整条路线。</p>
        </div>
        <div class="heading-actions">
          <el-select
            v-model="store.selectedTrainId.value"
            placeholder="选择车次"
            class="train-select"
            @change="store.loadTrainStations"
          >
            <el-option
              v-for="train in store.trains.value"
              :key="train.id"
              :label="train.trainNo"
              :value="train.id"
            />
          </el-select>
          <el-button :icon="Plus" @click="store.addRouteRow">加站</el-button>
          <el-button type="primary" :icon="Route" :loading="store.loading.route" @click="store.saveTrainStations">
            保存路线
          </el-button>
        </div>
      </div>

      <el-table :data="store.routeRows.value" v-loading="store.loading.route" row-key="stopOrder">
        <el-table-column label="站序" width="90">
          <template #default="{ row }">
            <el-input-number v-model="row.stopOrder" :min="1" controls-position="right" />
          </template>
        </el-table-column>
        <el-table-column label="车站" min-width="180">
          <template #default="{ row }">
            <el-select v-model="row.stationId" placeholder="选择车站">
              <el-option
                v-for="station in store.stations.value"
                :key="station.id"
                :label="`${station.name} (${station.city})`"
                :value="station.id"
              />
            </el-select>
          </template>
        </el-table-column>
        <el-table-column label="到达时间" min-width="150">
          <template #default="{ row }">
            <el-time-picker v-model="row.arrivalTime" value-format="HH:mm:ss" placeholder="到达" />
          </template>
        </el-table-column>
        <el-table-column label="出发时间" min-width="150">
          <template #default="{ row }">
            <el-time-picker v-model="row.departureTime" value-format="HH:mm:ss" placeholder="出发" />
          </template>
        </el-table-column>
        <el-table-column label="操作" width="90" fixed="right">
          <template #default="{ $index }">
            <el-button type="danger" :icon="Trash2" @click="store.removeRouteRow($index)" />
          </template>
        </el-table-column>
      </el-table>
    </section>

    <div class="work-grid">
      <div class="panel">
        <div class="panel-title">
          <div>
            <h2>每日开行</h2>
            <p>创建某天某趟车的开行记录。</p>
          </div>
        </div>
        <el-form label-position="top" class="compact-form">
          <el-form-item label="车次">
            <el-select v-model="store.scheduleForm.trainId" placeholder="选择车次">
              <el-option
                v-for="train in store.trains.value"
                :key="train.id"
                :label="train.trainNo"
                :value="train.id"
              />
            </el-select>
          </el-form-item>
          <el-form-item label="日期">
            <el-date-picker
              v-model="store.scheduleForm.travelDate"
              type="date"
              value-format="YYYY-MM-DD"
              placeholder="选择日期"
            />
          </el-form-item>
          <el-form-item label="状态">
            <el-select v-model="store.scheduleForm.status">
              <el-option label="OPEN" value="OPEN" />
              <el-option label="CLOSED" value="CLOSED" />
            </el-select>
          </el-form-item>
        </el-form>
        <el-button type="primary" :icon="CalendarPlus" :loading="store.loading.schedule" @click="store.createSchedule">
          创建开行
        </el-button>

        <el-table :data="store.createdSchedules.value" row-key="id" class="admin-table">
          <el-table-column prop="id" label="ID" width="80" />
          <el-table-column label="车次" min-width="90">
            <template #default="{ row }">{{ store.trainNoById.value.get(row.trainId) || row.trainId }}</template>
          </el-table-column>
          <el-table-column prop="travelDate" label="日期" min-width="120" />
          <el-table-column prop="status" label="状态" min-width="100" />
        </el-table>
      </div>

      <div class="panel">
        <div class="panel-title">
          <div>
            <h2>库存创建</h2>
            <p>为开行记录配置区间、座位、票数和价格。</p>
          </div>
        </div>
        <el-form label-position="top" class="inventory-form">
          <el-form-item label="Schedule ID">
            <el-input-number v-model="store.inventoryForm.scheduleId" :min="1" controls-position="right" />
          </el-form-item>
          <el-form-item label="出发站">
            <el-select v-model="store.inventoryForm.departureStationId" placeholder="选择出发站">
              <el-option
                v-for="station in store.stations.value"
                :key="station.id"
                :label="`${station.name} (${station.city})`"
                :value="station.id"
              />
            </el-select>
          </el-form-item>
          <el-form-item label="到达站">
            <el-select v-model="store.inventoryForm.arrivalStationId" placeholder="选择到达站">
              <el-option
                v-for="station in store.stations.value"
                :key="station.id"
                :label="`${station.name} (${station.city})`"
                :value="station.id"
              />
            </el-select>
          </el-form-item>
          <el-form-item label="座位">
            <el-select v-model="store.inventoryForm.seatType">
              <el-option label="二等座" value="SECOND_CLASS" />
              <el-option label="一等座" value="FIRST_CLASS" />
              <el-option label="商务座" value="BUSINESS_CLASS" />
            </el-select>
          </el-form-item>
          <el-form-item label="总票数">
            <el-input-number v-model="store.inventoryForm.totalCount" :min="0" controls-position="right" />
          </el-form-item>
          <el-form-item label="可售">
            <el-input-number v-model="store.inventoryForm.availableCount" :min="0" controls-position="right" />
          </el-form-item>
          <el-form-item label="锁定">
            <el-input-number v-model="store.inventoryForm.lockedCount" :min="0" controls-position="right" />
          </el-form-item>
          <el-form-item label="票价">
            <el-input-number v-model="store.inventoryForm.price" :min="0" :precision="2" controls-position="right" />
          </el-form-item>
        </el-form>
        <el-button type="primary" :icon="PackagePlus" :loading="store.loading.inventory" @click="store.createInventory">
          创建库存
        </el-button>
      </div>
    </div>
  </section>
</template>
