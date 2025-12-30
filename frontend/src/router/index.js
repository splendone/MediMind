import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/',
    redirect: '/login'
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue')
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/Register.vue')
  },
  {
    path: '/patient',
    name: 'PatientLayout',
    component: () => import('@/views/patient/Layout.vue'),
    meta: { requiresAuth: true, role: 'PATIENT' },
    children: [
      {
        path: 'dashboard',
        name: 'PatientDashboard',
        component: () => import('@/views/patient/Dashboard.vue')
      },
      {
        path: 'health-record',
        name: 'HealthRecord',
        component: () => import('@/views/patient/HealthRecord.vue')
      },
      {
        path: 'health-chart',
        name: 'HealthChart',
        component: () => import('@/views/patient/HealthChart.vue')
      },
      {
        path: 'ai-suggestion',
        name: 'AiSuggestion',
        component: () => import('@/views/patient/AiSuggestion.vue')
      },
      {
        path: 'alert',
        name: 'Alert',
        component: () => import('@/views/patient/Alert.vue')
      },
      {
        path: 'medication',
        name: 'Medication',
        component: () => import('@/views/patient/Medication.vue')
      },
      {
        path: 'chat',
        name: 'Chat',
        component: () => import('@/views/patient/Chat.vue')
      }
    ]
  },
  {
    path: '/doctor',
    name: 'DoctorLayout',
    component: () => import('@/views/doctor/Layout.vue'),
    meta: { requiresAuth: true, role: 'DOCTOR' },
    children: [
      {
        path: 'dashboard',
        name: 'DoctorDashboard',
        component: () => import('@/views/doctor/Dashboard.vue')
      },
      {
        path: 'patients',
        name: 'Patients',
        component: () => import('@/views/doctor/Patients.vue')
      },
      {
        path: 'patient/:id',
        name: 'PatientDetail',
        component: () => import('@/views/doctor/PatientDetail.vue')
      },
      {
        path: 'alerts',
        name: 'DoctorAlerts',
        component: () => import('@/views/doctor/Alerts.vue')
      },
      {
        path: 'chat',
        name: 'DoctorChat',
        component: () => import('@/views/doctor/Chat.vue')
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  const userRole = localStorage.getItem('role')
  
  if (to.meta.requiresAuth && !token) {
    next('/login')
  } else if (to.meta.role && to.meta.role !== userRole) {
    next('/login')
  } else {
    next()
  }
})

export default router
