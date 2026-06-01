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
        meta: { title: '首页', roles: ['admin', 'employee'] }
      },
      {
        path: '/employees',
        name: 'employees',
        component: () => import('../views/employees/EmployeeList.vue'),
        meta: { title: '员工列表', roles: ['admin'] }
      },
      {
        path: '/employees/add',
        name: 'employee-add',
        component: () => import('../views/employees/EmployeeForm.vue'),
        meta: { title: '添加员工', roles: ['admin'] }
      },
      {
        path: '/employees/edit/:id',
        name: 'employee-edit',
        component: () => import('../views/employees/EmployeeForm.vue'),
        meta: { title: '编辑员工', roles: ['admin'] }
      },
      {
        path: '/departments',
        name: 'departments',
        component: () => import('../views/departments/DepartmentList.vue'),
        meta: { title: '部门管理', roles: ['admin'] }
      },
      {
        path: '/attendance',
        name: 'attendance',
        component: () => import('../views/attendance/AttendanceList.vue'),
        meta: { title: '考勤管理', roles: ['admin', 'employee'] }
      },
      {
        path: '/announcements',
        name: 'announcements',
        component: () => import('../views/announcements/AnnouncementList.vue'),
        meta: { title: '公告通知', roles: ['admin', 'employee'] }
      },
      {
        path: '/leaves',
        name: 'leaves',
        component: () => import('../views/leaves/LeaveList.vue'),
        meta: { title: '请假管理', roles: ['admin', 'employee'] }
      },
      {
        path: '/users',
        name: 'users',
        component: () => import('../views/users/UserList.vue'),
        meta: { title: '账号管理', roles: ['admin'] }
      },
      {
        path: '/users/add',
        name: 'user-add',
        component: () => import('../views/users/UserForm.vue'),
        meta: { title: '添加账号', roles: ['admin'] }
      },
      {
        path: '/users/edit/:id',
        name: 'user-edit',
        component: () => import('../views/users/UserForm.vue'),
        meta: { title: '编辑账号', roles: ['admin'] }
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
  } else if (to.meta.roles && !to.meta.roles.includes(authStore.role)) {
    next('/dashboard')
  } else {
    next()
  }
})

export default router