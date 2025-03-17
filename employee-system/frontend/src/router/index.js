import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/',
    component: () => import('../views/Layout.vue'),
    redirect: '/employees',
    children: [
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

export default router 