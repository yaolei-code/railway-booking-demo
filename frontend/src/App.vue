<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import {
  CircleDollarSign,
  LogIn,
  LogOut,
  RefreshCcw,
  Search,
  Ticket,
  TrainFront,
  UserPlus,
  XCircle
} from 'lucide-vue-next'
import { ElMessage } from 'element-plus'

const token = ref(localStorage.getItem('railway_token') || '')
const currentUser = ref(null)
const stations = ref([])
const tickets = ref([])
const orders = ref([])
const loading = reactive({
  auth: false,
  stations: false,
  tickets: false,
  orders: false
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

const authHeader = computed(() => token.value ? { Authorization: `Bearer ${token.value}` } : {})
const isLoggedIn = computed(() => Boolean(token.value && currentUser.value))

async function request(path, options = {}) {
  const response = await fetch(path, {
    headers: {
      'Content-Type': 'application/json',
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
  await loadMe()
  if (searchForm.departureStationId && searchForm.arrivalStationId) {
    await searchTickets()
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
          <el-tag v-else type="info">未登录</el-tag>
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

.el-select,
.el-date-editor.el-input {
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
    grid-template-columns: repeat(3, minmax(0, 1fr));
  }

  .work-grid,
  .search-form {
    grid-template-columns: 1fr;
  }

  .topbar,
  .panel-title {
    flex-direction: column;
    align-items: stretch;
  }
}
</style>
