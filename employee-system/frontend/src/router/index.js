import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '../store/auth'

const routes = [
  {
    path: '/login',
    name: 'login',
    component: () => import('../views/Login.vue'),
    meta: { title: '登录', requiresAuth: false }
  },
  {
    path: '/',
    component: () => import('../views/Layout.vue'),
    redirect: '/dashboard',
    meta: { requiresAuth: true },
    children: [
      {
        path: '/dashboard',
        name: 'dashboard',
        component: () => import('../views/Dashboard.vue'),
        meta: { title: '首页' }
      },
      {
        path: '/employees',
        name: 'employees',
        component: () => import('../views/employees/EmployeeList.vue'),
        meta: { title: '员工列表' }
      },
      {
        path: '/employees/add',
        name: 'employee-add',
        component: () => import('../views/employees/EmployeeForm.vue'),
        meta: { title: '添加员工' }
      },
      {
        path: '/employees/edit/:id',
        name: 'employee-edit',
        component: () => import('../views/employees/EmployeeForm.vue'),
        meta: { title: '编辑员工' }
      },
      {
        path: '/departments',
        name: 'departments',
        component: () => import('../views/departments/DepartmentList.vue'),
        meta: { title: '部门管理' }
      },
      {
        path: '/attendance',
        name: 'attendance',
        component: () => import('../views/attendance/AttendanceList.vue'),
        meta: { title: '考勤管理' }
      },
      {
        path: '/announcements',
        name: 'announcements',
        component: () => import('../views/announcements/AnnouncementList.vue'),
        meta: { title: '公告通知' }
      },
      {
        path: '/leaves',
        name: 'leaves',
        component: () => import('../views/leaves/LeaveList.vue'),
        meta: { title: '请假管理' }
      }
    ]
  },
  {
    path: '/:pathMatch(.*)*',
    name: 'not-found',
    component: () => import('../views/NotFound.vue')
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const authStore = useAuthStore()
  
  if (to.meta.requiresAuth !== false && !authStore.isLoggedIn) {
    next('/login')
  } else if (to.path === '/login' && authStore.isLoggedIn) {
    next('/dashboard')
  } else {
    next()
  }
})

export default router