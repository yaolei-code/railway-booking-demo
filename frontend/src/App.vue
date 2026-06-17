<script setup>
import { onMounted } from 'vue'
import { RouterLink, RouterView, useRouter } from 'vue-router'
import { LogOut, TrainFront } from 'lucide-vue-next'
import { useRailwayStore } from './services/railwayStore'

const router = useRouter()
const store = useRailwayStore()

async function logout() {
  store.logout()
  await router.push('/account')
}

onMounted(async () => {
  await store.loadStations()
  await store.loadTrains()
  await store.loadMe()
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
        <RouterLink to="/tickets">查票</RouterLink>
        <RouterLink to="/orders">订单</RouterLink>
        <RouterLink to="/account">账号</RouterLink>
        <RouterLink v-if="store.isAdmin.value" to="/admin">管理</RouterLink>
      </nav>
    </aside>

    <main class="workspace">
      <header class="topbar">
        <div>
          <h1>铁路订票系统</h1>
          <p>用户端和管理端已拆分为独立路由，管理员入口由角色权限控制。</p>
        </div>
        <div class="user-box">
          <el-tag v-if="store.isLoggedIn.value" type="success">{{ store.currentUser.value.username }}</el-tag>
          <el-tag v-if="store.isAdmin.value" type="warning">ADMIN</el-tag>
          <el-tag v-if="!store.isLoggedIn.value" type="info">未登录</el-tag>
          <el-button v-if="store.isLoggedIn.value" :icon="LogOut" @click="logout">退出</el-button>
        </div>
      </header>

      <RouterView />
    </main>
  </div>
</template>
