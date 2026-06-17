import { computed, reactive, ref } from 'vue'
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
    return currentUser.value
  } catch (error) {
    ElMessage.error(error.message)
    return null
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
  if (!token.value) return null
  try {
    currentUser.value = await request('/api/users/me')
    await loadOrders()
    return currentUser.value
  } catch {
    logout()
    return null
  }
}

async function ensureCurrentUser() {
  if (currentUser.value) return currentUser.value
  return loadMe()
}

async function loadStations() {
  loading.stations = true
  try {
    stations.value = await request('/api/stations')
    if (stations.value.length >= 2 && !searchForm.departureStationId && !searchForm.arrivalStationId) {
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
    await request(`/api/admin/stations/${station.id}`, { method: 'DELETE' })
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
    await request(`/api/admin/trains/${train.id}`, { method: 'DELETE' })
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
    orders.value = await request('/api/orders')
  } catch (error) {
    ElMessage.error(error.message)
  } finally {
    loading.orders = false
  }
}

async function payOrder(orderId) {
  try {
    await request(`/api/orders/${orderId}/pay`, { method: 'POST' })
    ElMessage.success('支付成功')
    await loadOrders()
  } catch (error) {
    ElMessage.error(error.message)
  }
}

async function cancelOrder(orderId) {
  try {
    await request(`/api/orders/${orderId}/cancel`, { method: 'POST' })
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

export function useRailwayStore() {
  return {
    token,
    currentUser,
    stations,
    trains,
    tickets,
    orders,
    routeRows,
    createdSchedules,
    selectedTrainId,
    loading,
    loginForm,
    registerForm,
    searchForm,
    passengerForm,
    stationForm,
    trainForm,
    scheduleForm,
    inventoryForm,
    isLoggedIn,
    isAdmin,
    trainNoById,
    request,
    register,
    login,
    logout,
    loadMe,
    ensureCurrentUser,
    loadStations,
    resetStationForm,
    editStation,
    saveStation,
    deleteStation,
    loadTrains,
    resetTrainForm,
    editTrain,
    saveTrain,
    deleteTrain,
    loadTrainStations,
    addRouteRow,
    removeRouteRow,
    saveTrainStations,
    createSchedule,
    createInventory,
    searchTickets,
    createOrder,
    loadOrders,
    payOrder,
    cancelOrder,
    statusType
  }
}
