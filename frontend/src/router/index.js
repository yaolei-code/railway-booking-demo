import { createRouter, createWebHistory } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useRailwayStore } from '../services/railwayStore'
import TicketSearchView from '../views/TicketSearchView.vue'
import OrdersView from '../views/OrdersView.vue'
import AccountView from '../views/AccountView.vue'
import AdminView from '../views/AdminView.vue'

const routes = [
  { path: '/', redirect: '/tickets' },
  { path: '/tickets', component: TicketSearchView },
  { path: '/orders', component: OrdersView, meta: { requiresAuth: true } },
  { path: '/account', component: AccountView },
  { path: '/admin', component: AdminView, meta: { requiresAuth: true, requiresAdmin: true } }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach(async (to) => {
  const store = useRailwayStore()
  if (!to.meta.requiresAuth && !to.meta.requiresAdmin) {
    return true
  }

  const user = await store.ensureCurrentUser()
  if (!user) {
    ElMessage.warning('请先登录')
    return { path: '/account', query: { redirect: to.fullPath } }
  }

  if (to.meta.requiresAdmin && user.role !== 'ADMIN') {
    ElMessage.error('当前账号没有管理员权限')
    return '/tickets'
  }

  return true
})

export default router
