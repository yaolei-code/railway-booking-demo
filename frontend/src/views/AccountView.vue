<script setup>
import { useRoute, useRouter } from 'vue-router'
import { LogIn, UserPlus } from 'lucide-vue-next'
import { useRailwayStore } from '../services/railwayStore'

const route = useRoute()
const router = useRouter()
const store = useRailwayStore()

async function loginAndRedirect() {
  const user = await store.login()
  if (!user) return
  const redirect = route.query.redirect || (user.role === 'ADMIN' ? '/admin' : '/tickets')
  await router.push(String(redirect))
}
</script>

<template>
  <section class="work-grid">
    <div class="panel">
      <div class="panel-title">
        <div>
          <h2>登录</h2>
          <p>登录后前端会保存 token，并带着 token 调用订单接口。</p>
        </div>
      </div>
      <el-form label-position="top">
        <el-form-item label="用户名">
          <el-input v-model="store.loginForm.username" />
        </el-form-item>
        <el-form-item label="密码">
          <el-input v-model="store.loginForm.password" type="password" show-password />
        </el-form-item>
      </el-form>
      <el-button type="primary" :icon="LogIn" :loading="store.loading.auth" @click="loginAndRedirect">登录</el-button>
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
          <el-input v-model="store.registerForm.username" />
        </el-form-item>
        <el-form-item label="密码">
          <el-input v-model="store.registerForm.password" type="password" show-password />
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="store.registerForm.phone" />
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="store.registerForm.email" />
        </el-form-item>
      </el-form>
      <el-button :icon="UserPlus" :loading="store.loading.auth" @click="store.register">注册</el-button>
    </div>
  </section>
</template>
