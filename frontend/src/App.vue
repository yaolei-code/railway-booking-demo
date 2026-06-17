<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import {
  CalendarPlus,
  CircleDollarSign,
  LogIn,
  LogOut,
  PackagePlus,
  Pencil,
  Plus,
  RefreshCcw,
  Route,
  Save,
  Search,
  Ticket,
  Trash2,
  TrainFront,
  UserPlus,
  XCircle
} from 'lucide-vue-next'
import { ElMessage, ElMessageBox } from 'element-plus'

const token = ref(localStorage.getItem('railway_token') || '')
const currentUser = ref(null)
const stations = ref([])
const trains = ref([])
const tickets = ref([])
const orders = ref([])
const routeRows = ref([])
const createdSchedules = ref([])
const selectedTrainId = ref('')
const loading = reactive({
  auth: false,
  stations: false,
  tickets: false,
  orders: false,
  trains: false,
  route: false,
  schedule: false,
  inventory: false
})

const loginForm = reactive({
  username: 'testuser',
  password: '123456'
})

const registerForm = reactive({
  username: 'testuser',
  password: '123456',
  phone: '13800000000',
  email: 'test@example.com'
})

const searchForm = reactive({
  departureStationId: '',
  arrivalStationId: '',
  travelDate: '2026-06-20',
  seatType: 'SECOND_CLASS'
})

const passengerForm = reactive({
  passengerName: '张三',
  passengerIdNo: '110101199001011234'
})

const stationForm = reactive({
  id: null,
  name: '',
  city: '',
  code: ''
})

const trainForm = reactive({
  id: null,
  trainNo: '',
  trainType: 'G',
  status: 'ACTIVE'
})

const scheduleForm = reactive({
  trainId: '',
  travelDate: '2026-06-20',
  status: 'OPEN'
})

const inventoryForm = reactive({
  scheduleId: '',
  departureStationId: '',
  arrivalStationId: '',
  seatType: 'SECOND_CLASS',
  totalCount: 50,
  availableCount: 50,
  lockedCount: 0,
  price: 100
})

const authHeader = computed(() => token.value ? { Authorization: `Bearer ${token.value}` } : {})
const isLoggedIn = computed(() => Boolean(token.value && currentUser.value))
const isAdmin = computed(() => currentUser.value?.role === 'ADMIN')
const stationNameById = computed(() => {
  const map = new Map()
  stations.value.forEach((station) => map.set(station.id, station.name))
  return map
})
const trainNoById = computed(() => {
  const map = new Map()
  trains.value.forEach((train) => map.set(train.id, train.trainNo))
  return map
})

async function request(path, options = {}) {
  const response = await fetch(path, {
    headers: {
      'Content-Type': 'application/json',
      ...authHeader.value,
      ...(options.headers || {})
    },
    ...options
  })
  const body = await response.json()
  if (!response.ok || body.code !== 0) {
    throw new Error(body.message || 'request failed')
  }
  return body.data
}

async function register() {
  loading.auth = true
  try {
    await request('/api/users/register', {
      method: 'POST',
      body: JSON.stringify(registerForm)
    })
    ElMessage.success('注册成功，可以登录')
    loginForm.username = registerForm.username
    loginForm.password = registerForm.password
  } catch (error) {
    ElMessage.error(error.message)
  } finally {
    loading.auth = false
  }
}

async function login() {
  loading.auth = true
  try {
    const data = await request('/api/users/login', {
      method: 'POST',
      body: JSON.stringify(loginForm)
    })
    token.value = data.token
    localStorage.setItem('railway_token', data.token)
    currentUser.value = {
      id: data.id,
      username: data.username,
      role: data.role,
      status: data.status
    }
    ElMessage.success('登录成功')
    await loadOrders()
  } catch (error) {
    ElMessage.error(error.message)
  } finally {
    loading.auth = false
  }
}

function logout() {
  token.value = ''
  currentUser.value = null
  orders.value = []
  localStorage.removeItem('railway_token')
  ElMessage.success('已退出')
}

async function loadMe() {
  if (!token.value) return
  try {
    currentUser.value = await request('/api/users/me', {
      headers: authHeader.value
    })
    await loadOrders()
  } catch {
    logout()
  }
}

async function loadStations() {
  loading.stations = true
  try {
    stations.value = await request('/api/stations')
    if (stations.value.length >= 2) {
      searchForm.departureStationId = stations.value[0].id
      searchForm.arrivalStationId = stations.value[1].id
    }
  } catch (error) {
    ElMessage.error(error.message)
  } finally {
    loading.stations = false
  }
}

function resetStationForm() {
  stationForm.id = null
  stationForm.name = ''
  stationForm.city = ''
  stationForm.code = ''
}

function editStation(station) {
  stationForm.id = station.id
  stationForm.name = station.name
  stationForm.city = station.city
  stationForm.code = station.code
}

async function saveStation() {
  loading.stations = true
  try {
    const method = stationForm.id ? 'PUT' : 'POST'
    const path = stationForm.id ? `/api/admin/stations/${stationForm.id}` : '/api/admin/stations'
    await request(path, {
      method,
      body: JSON.stringify({
        name: stationForm.name,
        city: stationForm.city,
        code: stationForm.code
      })
    })
    ElMessage.success(stationForm.id ? '车站已更新' : '车站已创建')
    resetStationForm()
    await loadStations()
  } catch (error) {
    ElMessage.error(error.message)
  } finally {
    loading.stations = false
  }
}

async function deleteStation(station) {
  try {
    await ElMessageBox.confirm(`确认删除车站 ${station.name}？`, '删除车站', {
      type: 'warning',
      confirmButtonText: '删除',
      cancelButtonText: '取消'
    })
    loading.stations = true
    await request(`/api/admin/stations/${station.id}`, {
      method: 'DELETE'
    })
    ElMessage.success('车站已删除')
    await loadStations()
  } catch (error) {
    if (error !== 'cancel') ElMessage.error(error.message || '操作取消')
  } finally {
    loading.stations = false
  }
}

async function loadTrains() {
  loading.trains = true
  try {
    trains.value = await request('/api/trains')
    if (!selectedTrainId.value && trains.value.length > 0) {
      selectedTrainId.value = trains.value[0].id
      scheduleForm.trainId = trains.value[0].id
    }
  } catch (error) {
    ElMessage.error(error.message)
  } finally {
    loading.trains = false
  }
}

function resetTrainForm() {
  trainForm.id = null
  trainForm.trainNo = ''
  trainForm.trainType = 'G'
  trainForm.status = 'ACTIVE'
}

function editTrain(train) {
  trainForm.id = train.id
  trainForm.trainNo = train.trainNo
  trainForm.trainType = train.trainType
  trainForm.status = train.status
}

async function saveTrain() {
  loading.trains = true
  try {
    const method = trainForm.id ? 'PUT' : 'POST'
    const path = trainForm.id ? `/api/admin/trains/${trainForm.id}` : '/api/admin/trains'
    await request(path, {
      method,
      body: JSON.stringify({
        trainNo: trainForm.trainNo,
        trainType: trainForm.trainType,
        status: trainForm.status
      })
    })
    ElMessage.success(trainForm.id ? '车次已更新' : '车次已创建')
    resetTrainForm()
    await loadTrains()
  } catch (error) {
    ElMessage.error(error.message)
  } finally {
    loading.trains = false
  }
}

async function deleteTrain(train) {
  try {
    await ElMessageBox.confirm(`确认删除车次 ${train.trainNo}？`, '删除车次', {
      type: 'warning',
      confirmButtonText: '删除',
      cancelButtonText: '取消'
    })
    loading.trains = true
    await request(`/api/admin/trains/${train.id}`, {
      method: 'DELETE'
    })
    ElMessage.success('车次已删除')
    await loadTrains()
  } catch (error) {
    if (error !== 'cancel') ElMessage.error(error.message || '操作取消')
  } finally {
    loading.trains = false
  }
}

async function loadTrainStations() {
  if (!selectedTrainId.value) return
  loading.route = true
  try {
    const rows = await request(`/api/trains/${selectedTrainId.value}/stations`)
    routeRows.value = rows.map((row) => ({
      stationId: row.stationId,
      stopOrder: row.stopOrder,
      arrivalTime: row.arrivalTime || '',
      departureTime: row.departureTime || ''
    }))
  } catch (error) {
    ElMessage.error(error.message)
  } finally {
    loading.route = false
  }
}

function addRouteRow() {
  routeRows.value.push({
    stationId: '',
    stopOrder: routeRows.value.length + 1,
    arrivalTime: '',
    departureTime: ''
  })
}

function removeRouteRow(index) {
  routeRows.value.splice(index, 1)
  routeRows.value.forEach((row, rowIndex) => {
    row.stopOrder = rowIndex + 1
  })
}

async function saveTrainStations() {
  if (!selectedTrainId.value) {
    ElMessage.warning('请先选择车次')
    return
  }
  loading.route = true
  try {
    await request(`/api/admin/trains/${selectedTrainId.value}/stations`, {
      method: 'PUT',
      body: JSON.stringify(routeRows.value.map((row) => ({
        stationId: row.stationId,
        stopOrder: row.stopOrder,
        arrivalTime: row.arrivalTime || null,
        departureTime: row.departureTime || null
      })))
    })
    ElMessage.success('经停车站已保存')
    await loadTrainStations()
  } catch (error) {
    ElMessage.error(error.message)
  } finally {
    loading.route = false
  }
}

async function createSchedule() {
  loading.schedule = true
  try {
    const data = await request('/api/admin/schedules', {
      method: 'POST',
      body: JSON.stringify(scheduleForm)
    })
    createdSchedules.value.unshift(data)
    inventoryForm.scheduleId = data.id
    ElMessage.success('每日车次已创建')
  } catch (error) {
    ElMessage.error(error.message)
  } finally {
    loading.schedule = false
  }
}

async function createInventory() {
  loading.inventory = true
  try {
    await request('/api/admin/inventory', {
      method: 'POST',
      body: JSON.stringify(inventoryForm)
    })
    ElMessage.success('库存已创建')
  } catch (error) {
    ElMessage.error(error.message)
  } finally {
    loading.inventory = false
  }
}

async function searchTickets() {
  loading.tickets = true
  try {
    const params = new URLSearchParams({
      departureStationId: searchForm.departureStationId,
      arrivalStationId: searchForm.arrivalStationId,
      travelDate: searchForm.travelDate
    })
    if (searchForm.seatType) params.set('seatType', searchForm.seatType)
    tickets.value = await request(`/api/tickets/search?${params}`)
    if (tickets.value.length === 0) {
      ElMessage.warning('没有查到车票')
    }
  } catch (error) {
    ElMessage.error(error.message)
  } finally {
    loading.tickets = false
  }
}

async function createOrder(ticketItem) {
  if (!isLoggedIn.value) {
    ElMessage.warning('请先登录')
    return
  }
  try {
    await request('/api/orders', {
      method: 'POST',
      headers: authHeader.value,
      body: JSON.stringify({
        inventoryId: ticketItem.inventoryId,
        passengerName: passengerForm.passengerName,
        passengerIdNo: passengerForm.passengerIdNo
      })
    })
    ElMessage.success('下单成功')
    await searchTickets()
    await loadOrders()
  } catch (error) {
    ElMessage.error(error.message)
  }
}

async function loadOrders() {
  if (!token.value) return
  loading.orders = true
  try {
    orders.value = await request('/api/orders', {
      headers: authHeader.value
    })
  } catch (error) {
    ElMessage.error(error.message)
  } finally {
    loading.orders = false
  }
}

async function payOrder(orderId) {
  try {
    await request(`/api/orders/${orderId}/pay`, {
      method: 'POST',
      headers: authHeader.value
    })
    ElMessage.success('支付成功')
    await loadOrders()
  } catch (error) {
    ElMessage.error(error.message)
  }
}

async function cancelOrder(orderId) {
  try {
    await request(`/api/orders/${orderId}/cancel`, {
      method: 'POST',
      headers: authHeader.value
    })
    ElMessage.success('订单已取消')
    await loadOrders()
    await searchTickets()
  } catch (error) {
    ElMessage.error(error.message)
  }
}

function statusType(status) {
  if (status === 'PAID') return 'success'
  if (status === 'CANCELLED') return 'info'
  return 'warning'
}

onMounted(async () => {
  await loadStations()
  await loadTrains()
  await loadMe()
  if (searchForm.departureStationId && searchForm.arrivalStationId) {
    await searchTickets()
  }
  if (selectedTrainId.value) {
    await loadTrainStations()
  }
})
</script>

<template>
  <div class="app-shell">
    <aside class="sidebar">
      <div class="brand">
        <TrainFront />
        <div>
          <strong>Railway Booking</strong>
          <span>Demo Console</span>
        </div>
      </div>
      <nav>
        <a href="#tickets">查票</a>
        <a href="#orders">订单</a>
        <a href="#account">账号</a>
        <a v-if="isAdmin" href="#admin">管理</a>
      </nav>
    </aside>

    <main class="workspace">
      <header class="topbar">
        <div>
          <h1>铁路订票后台演示</h1>
          <p>当前第一版已串通登录、查票、下单、支付和取消。</p>
        </div>
        <div class="user-box">
          <el-tag v-if="isLoggedIn" type="success">{{ currentUser.username }}</el-tag>
          <el-tag v-if="isAdmin" type="warning">ADMIN</el-tag>
          <el-tag v-if="!isLoggedIn" type="info">未登录</el-tag>
          <el-button v-if="isLoggedIn" :icon="LogOut" @click="logout">退出</el-button>
        </div>
      </header>

      <section id="tickets" class="work-grid">
        <div class="panel">
          <div class="panel-title">
            <div>
              <h2>查票</h2>
              <p>使用后端演示数据可以直接查北京南到上海虹桥。</p>
            </div>
            <el-button :icon="RefreshCcw" :loading="loading.stations" @click="loadStations">刷新车站</el-button>
          </div>

          <el-form label-position="top" class="search-form">
            <el-form-item label="出发站">
              <el-select v-model="searchForm.departureStationId" placeholder="选择出发站">
                <el-option
                  v-for="station in stations"
                  :key="station.id"
                  :label="`${station.name} (${station.city})`"
                  :value="station.id"
                />
              </el-select>
            </el-form-item>
            <el-form-item label="到达站">
              <el-select v-model="searchForm.arrivalStationId" placeholder="选择到达站">
                <el-option
                  v-for="station in stations"
                  :key="station.id"
                  :label="`${station.name} (${station.city})`"
                  :value="station.id"
                />
              </el-select>
            </el-form-item>
            <el-form-item label="日期">
              <el-date-picker
                v-model="searchForm.travelDate"
                type="date"
                value-format="YYYY-MM-DD"
                placeholder="选择日期"
              />
            </el-form-item>
            <el-form-item label="座位类型">
              <el-select v-model="searchForm.seatType" clearable placeholder="不限">
                <el-option label="二等座" value="SECOND_CLASS" />
                <el-option label="一等座" value="FIRST_CLASS" />
              </el-select>
            </el-form-item>
          </el-form>

          <el-button type="primary" :icon="Search" :loading="loading.tickets" @click="searchTickets">查询</el-button>
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
              <el-input v-model="passengerForm.passengerName" />
            </el-form-item>
            <el-form-item label="证件号">
              <el-input v-model="passengerForm.passengerIdNo" />
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
        <el-table :data="tickets" v-loading="loading.tickets" row-key="scheduleId">
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
              <el-button type="primary" :icon="Ticket" @click="createOrder(row)">下单</el-button>
            </template>
          </el-table-column>
        </el-table>
      </section>

      <section id="orders" class="panel">
        <div class="panel-title">
          <div>
            <h2>我的订单</h2>
            <p>待支付订单可以支付或取消。</p>
          </div>
          <el-button :icon="RefreshCcw" :loading="loading.orders" @click="loadOrders">刷新订单</el-button>
        </div>
        <el-table :data="orders" v-loading="loading.orders" row-key="id">
          <el-table-column prop="orderNo" label="订单号" min-width="210" />
          <el-table-column prop="status" label="状态" min-width="130">
            <template #default="{ row }">
              <el-tag :type="statusType(row.status)">{{ row.status }}</el-tag>
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
                @click="payOrder(row.id)"
              >
                支付
              </el-button>
              <el-button
                type="danger"
                :icon="XCircle"
                :disabled="row.status !== 'PENDING_PAYMENT'"
                @click="cancelOrder(row.id)"
              >
                取消
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </section>

      <section id="account" class="work-grid">
        <div class="panel">
          <div class="panel-title">
            <div>
              <h2>登录</h2>
              <p>登录后前端会保存 token，并带着 token 调用订单接口。</p>
            </div>
          </div>
          <el-form label-position="top">
            <el-form-item label="用户名">
              <el-input v-model="loginForm.username" />
            </el-form-item>
            <el-form-item label="密码">
              <el-input v-model="loginForm.password" type="password" show-password />
            </el-form-item>
          </el-form>
          <el-button type="primary" :icon="LogIn" :loading="loading.auth" @click="login">登录</el-button>
        </div>

        <div class="panel">
          <div class="panel-title">
            <div>
              <h2>注册</h2>
              <p>如果测试用户不存在，可以先注册一个。</p>
            </div>
          </div>
          <el-form label-position="top">
            <el-form-item label="用户名">
              <el-input v-model="registerForm.username" />
            </el-form-item>
            <el-form-item label="密码">
              <el-input v-model="registerForm.password" type="password" show-password />
            </el-form-item>
            <el-form-item label="手机号">
              <el-input v-model="registerForm.phone" />
            </el-form-item>
            <el-form-item label="邮箱">
              <el-input v-model="registerForm.email" />
            </el-form-item>
          </el-form>
          <el-button :icon="UserPlus" :loading="loading.auth" @click="register">注册</el-button>
        </div>
      </section>

      <section v-if="isAdmin" id="admin" class="admin-section">
        <div class="section-heading">
          <h2>管理台</h2>
          <div class="heading-actions">
            <el-button :icon="RefreshCcw" :loading="loading.stations" @click="loadStations">车站</el-button>
            <el-button :icon="RefreshCcw" :loading="loading.trains" @click="loadTrains">车次</el-button>
          </div>
        </div>

        <div class="work-grid">
          <div class="panel">
            <div class="panel-title">
              <div>
                <h2>车站维护</h2>
                <p>维护站名、城市和站点编码。</p>
              </div>
              <el-button :icon="Plus" @click="resetStationForm">新建</el-button>
            </div>

            <el-form label-position="top" class="compact-form">
              <el-form-item label="站名">
                <el-input v-model="stationForm.name" placeholder="北京南" />
              </el-form-item>
              <el-form-item label="城市">
                <el-input v-model="stationForm.city" placeholder="北京" />
              </el-form-item>
              <el-form-item label="编码">
                <el-input v-model="stationForm.code" placeholder="BJN" />
              </el-form-item>
            </el-form>
            <el-button type="primary" :icon="Save" :loading="loading.stations" @click="saveStation">
              {{ stationForm.id ? '保存车站' : '创建车站' }}
            </el-button>

            <el-table :data="stations" v-loading="loading.stations" row-key="id" class="admin-table">
              <el-table-column prop="name" label="站名" min-width="110" />
              <el-table-column prop="city" label="城市" min-width="90" />
              <el-table-column prop="code" label="编码" min-width="90" />
              <el-table-column label="操作" width="160" fixed="right">
                <template #default="{ row }">
                  <el-button :icon="Pencil" @click="editStation(row)" />
                  <el-button type="danger" :icon="Trash2" @click="deleteStation(row)" />
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
              <el-button :icon="Plus" @click="resetTrainForm">新建</el-button>
            </div>

            <el-form label-position="top" class="compact-form">
              <el-form-item label="车次号">
                <el-input v-model="trainForm.trainNo" placeholder="G101" />
              </el-form-item>
              <el-form-item label="类型">
                <el-select v-model="trainForm.trainType">
                  <el-option label="高铁 G" value="G" />
                  <el-option label="动车 D" value="D" />
                  <el-option label="普通 K" value="K" />
                </el-select>
              </el-form-item>
              <el-form-item label="状态">
                <el-select v-model="trainForm.status">
                  <el-option label="ACTIVE" value="ACTIVE" />
                  <el-option label="INACTIVE" value="INACTIVE" />
                </el-select>
              </el-form-item>
            </el-form>
            <el-button type="primary" :icon="Save" :loading="loading.trains" @click="saveTrain">
              {{ trainForm.id ? '保存车次' : '创建车次' }}
            </el-button>

            <el-table :data="trains" v-loading="loading.trains" row-key="id" class="admin-table">
              <el-table-column prop="trainNo" label="车次" min-width="90" />
              <el-table-column prop="trainType" label="类型" min-width="80" />
              <el-table-column prop="status" label="状态" min-width="110">
                <template #default="{ row }">
                  <el-tag :type="row.status === 'ACTIVE' ? 'success' : 'info'">{{ row.status }}</el-tag>
                </template>
              </el-table-column>
              <el-table-column label="操作" width="160" fixed="right">
                <template #default="{ row }">
                  <el-button :icon="Pencil" @click="editTrain(row)" />
                  <el-button type="danger" :icon="Trash2" @click="deleteTrain(row)" />
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
                v-model="selectedTrainId"
                placeholder="选择车次"
                class="train-select"
                @change="loadTrainStations"
              >
                <el-option
                  v-for="train in trains"
                  :key="train.id"
                  :label="train.trainNo"
                  :value="train.id"
                />
              </el-select>
              <el-button :icon="Plus" @click="addRouteRow">加站</el-button>
              <el-button type="primary" :icon="Route" :loading="loading.route" @click="saveTrainStations">
                保存路线
              </el-button>
            </div>
          </div>

          <el-table :data="routeRows" v-loading="loading.route" row-key="stopOrder">
            <el-table-column label="站序" width="90">
              <template #default="{ row }">
                <el-input-number v-model="row.stopOrder" :min="1" controls-position="right" />
              </template>
            </el-table-column>
            <el-table-column label="车站" min-width="180">
              <template #default="{ row }">
                <el-select v-model="row.stationId" placeholder="选择车站">
                  <el-option
                    v-for="station in stations"
                    :key="station.id"
                    :label="`${station.name} (${station.city})`"
                    :value="station.id"
                  />
                </el-select>
              </template>
            </el-table-column>
            <el-table-column label="到达时间" min-width="150">
              <template #default="{ row }">
                <el-time-picker
                  v-model="row.arrivalTime"
                  value-format="HH:mm:ss"
                  placeholder="到达"
                />
              </template>
            </el-table-column>
            <el-table-column label="出发时间" min-width="150">
              <template #default="{ row }">
                <el-time-picker
                  v-model="row.departureTime"
                  value-format="HH:mm:ss"
                  placeholder="出发"
                />
              </template>
            </el-table-column>
            <el-table-column label="操作" width="90" fixed="right">
              <template #default="{ $index }">
                <el-button type="danger" :icon="Trash2" @click="removeRouteRow($index)" />
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
                <el-select v-model="scheduleForm.trainId" placeholder="选择车次">
                  <el-option
                    v-for="train in trains"
                    :key="train.id"
                    :label="train.trainNo"
                    :value="train.id"
                  />
                </el-select>
              </el-form-item>
              <el-form-item label="日期">
                <el-date-picker
                  v-model="scheduleForm.travelDate"
                  type="date"
                  value-format="YYYY-MM-DD"
                  placeholder="选择日期"
                />
              </el-form-item>
              <el-form-item label="状态">
                <el-select v-model="scheduleForm.status">
                  <el-option label="OPEN" value="OPEN" />
                  <el-option label="CLOSED" value="CLOSED" />
                </el-select>
              </el-form-item>
            </el-form>
            <el-button type="primary" :icon="CalendarPlus" :loading="loading.schedule" @click="createSchedule">
              创建开行
            </el-button>

            <el-table :data="createdSchedules" row-key="id" class="admin-table">
              <el-table-column prop="id" label="ID" width="80" />
              <el-table-column label="车次" min-width="90">
                <template #default="{ row }">{{ trainNoById.get(row.trainId) || row.trainId }}</template>
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
                <el-input-number v-model="inventoryForm.scheduleId" :min="1" controls-position="right" />
              </el-form-item>
              <el-form-item label="出发站">
                <el-select v-model="inventoryForm.departureStationId" placeholder="选择出发站">
                  <el-option
                    v-for="station in stations"
                    :key="station.id"
                    :label="`${station.name} (${station.city})`"
                    :value="station.id"
                  />
                </el-select>
              </el-form-item>
              <el-form-item label="到达站">
                <el-select v-model="inventoryForm.arrivalStationId" placeholder="选择到达站">
                  <el-option
                    v-for="station in stations"
                    :key="station.id"
                    :label="`${station.name} (${station.city})`"
                    :value="station.id"
                  />
                </el-select>
              </el-form-item>
              <el-form-item label="座位">
                <el-select v-model="inventoryForm.seatType">
                  <el-option label="二等座" value="SECOND_CLASS" />
                  <el-option label="一等座" value="FIRST_CLASS" />
                </el-select>
              </el-form-item>
              <el-form-item label="总票数">
                <el-input-number v-model="inventoryForm.totalCount" :min="0" controls-position="right" />
              </el-form-item>
              <el-form-item label="可售">
                <el-input-number v-model="inventoryForm.availableCount" :min="0" controls-position="right" />
              </el-form-item>
              <el-form-item label="锁定">
                <el-input-number v-model="inventoryForm.lockedCount" :min="0" controls-position="right" />
              </el-form-item>
              <el-form-item label="票价">
                <el-input-number v-model="inventoryForm.price" :min="0" :precision="2" controls-position="right" />
              </el-form-item>
            </el-form>
            <el-button type="primary" :icon="PackagePlus" :loading="loading.inventory" @click="createInventory">
              创建库存
            </el-button>
          </div>
        </div>
      </section>
    </main>
  </div>
</template>

<style scoped>
.app-shell {
  min-height: 100vh;
  display: grid;
  grid-template-columns: 248px minmax(0, 1fr);
}

.sidebar {
  background: #17202b;
  color: #f6f8fb;
  padding: 22px 18px;
  position: sticky;
  top: 0;
  height: 100vh;
}

.brand {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 28px;
}

.brand svg {
  width: 30px;
  height: 30px;
  color: #7fd1ae;
}

.brand strong,
.brand span {
  display: block;
}

.brand strong {
  font-size: 16px;
}

.brand span {
  color: #aab6c5;
  font-size: 12px;
  margin-top: 3px;
}

nav {
  display: grid;
  gap: 8px;
}

nav a {
  color: #dbe4ef;
  text-decoration: none;
  padding: 10px 12px;
  border-radius: 8px;
}

nav a:hover {
  background: #263342;
}

.workspace {
  padding: 24px;
  display: grid;
  gap: 18px;
}

.topbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 18px;
  padding-bottom: 4px;
}

h1,
h2,
p {
  margin: 0;
}

h1 {
  font-size: 24px;
  line-height: 1.25;
}

h2 {
  font-size: 17px;
  line-height: 1.35;
}

p {
  color: #637083;
  font-size: 13px;
  line-height: 1.6;
  margin-top: 4px;
}

.user-box {
  display: flex;
  align-items: center;
  gap: 10px;
  flex-wrap: wrap;
  justify-content: flex-end;
}

.work-grid {
  display: grid;
  grid-template-columns: minmax(0, 1.45fr) minmax(280px, 0.75fr);
  gap: 18px;
}

.admin-section {
  display: grid;
  gap: 18px;
}

.section-heading {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 14px;
}

.heading-actions {
  display: flex;
  align-items: center;
  justify-content: flex-end;
  gap: 10px;
  flex-wrap: wrap;
}

.panel {
  background: #ffffff;
  border: 1px solid #d9e1ea;
  border-radius: 8px;
  padding: 18px;
  box-shadow: 0 1px 2px rgba(25, 34, 45, 0.04);
}

.panel-title {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 14px;
  margin-bottom: 16px;
}

.search-form {
  display: grid;
  grid-template-columns: repeat(4, minmax(150px, 1fr));
  gap: 12px;
}

.compact-form {
  display: grid;
  grid-template-columns: repeat(3, minmax(120px, 1fr));
  gap: 12px;
}

.inventory-form {
  display: grid;
  grid-template-columns: repeat(4, minmax(120px, 1fr));
  gap: 12px;
}

.admin-table {
  margin-top: 16px;
}

.train-select {
  width: 150px;
}

.el-select,
.el-date-editor.el-input,
.el-date-editor.el-input__wrapper,
.el-input-number {
  width: 100%;
}

@media (max-width: 980px) {
  .app-shell {
    grid-template-columns: 1fr;
  }

  .sidebar {
    position: static;
    height: auto;
  }

  nav {
    grid-template-columns: repeat(4, minmax(0, 1fr));
  }

  .work-grid,
  .search-form,
  .compact-form,
  .inventory-form {
    grid-template-columns: 1fr;
  }

  .topbar,
  .panel-title,
  .section-heading {
    flex-direction: column;
    align-items: stretch;
  }

  .heading-actions {
    justify-content: flex-start;
  }

  .train-select {
    width: 100%;
  }
}
</style>
