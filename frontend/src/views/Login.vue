<template>
  <div class="login-container">
    <div class="login-box">
      <h2 class="title">智能健康管理平台</h2>
      <el-form :model="loginForm" :rules="rules" ref="loginFormRef" class="login-form">
        <el-form-item prop="username">
          <el-input
            v-model="loginForm.username"
            placeholder="请输入用户名"
            prefix-icon="User"
            size="large"
          />
        </el-form-item>
        
        <el-form-item prop="password">
          <el-input
            v-model="loginForm.password"
            type="password"
            placeholder="请输入密码"
            prefix-icon="Lock"
            size="large"
            show-password
            @keyup.enter="handleLogin"
          />
        </el-form-item>
        
        <el-form-item>
          <el-button
            type="primary"
            size="large"
            :loading="loading"
            @click="handleLogin"
            class="login-button"
          >
            登录
          </el-button>
        </el-form-item>
        
        <el-form-item>
          <el-button
            size="large"
            @click="$router.push('/register')"
            class="register-button"
          >
            注册新账号
          </el-button>
        </el-form-item>
      </el-form>
      
      <div class="demo-account">
        <p>测试账号：</p>
        <p>患者 - patient1 / password123</p>
        <p>医生 - doctor1 / password123</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { login } from '@/api/auth'

const router = useRouter()
const loginFormRef = ref(null)
const loading = ref(false)

const loginForm = reactive({
  username: '',
  password: ''
})

const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' }
  ]
}

const handleLogin = async () => {
  await loginFormRef.value.validate(async (valid) => {
    if (!valid) return
    
    try {
      loading.value = true
      const res = await login(loginForm)
      
      // 保存token和用户信息
      localStorage.setItem('token', res.data.token)
      localStorage.setItem('role', res.data.user.role)
      localStorage.setItem('userId', res.data.user.id)
      localStorage.setItem('username', res.data.user.username)
      
      ElMessage.success('登录成功')
      
      // 根据角色跳转
      if (res.data.user.role === 'PATIENT') {
        router.push('/patient/dashboard')
      } else if (res.data.user.role === 'DOCTOR') {
        router.push('/doctor/dashboard')
      }
    } catch (error) {
      console.error('登录失败:', error)
    } finally {
      loading.value = false
    }
  })
}
</script>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  width: 100%;
  height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.login-box {
  width: 400px;
  padding: 40px;
  background: white;
  border-radius: 10px;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.2);
}

.title {
  text-align: center;
  margin-bottom: 30px;
  font-size: 24px;
  color: #333;
}

.login-form {
  margin-top: 20px;
}

.login-button,
.register-button {
  width: 100%;
}

.demo-account {
  margin-top: 20px;
  padding: 15px;
  background: #f5f7fa;
  border-radius: 5px;
  font-size: 12px;
  color: #666;
  text-align: center;
}

.demo-account p {
  margin: 5px 0;
}
</style>
