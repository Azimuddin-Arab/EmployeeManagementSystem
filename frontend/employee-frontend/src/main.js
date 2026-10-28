import { createApp } from 'vue'
import { createRouter, createWebHistory } from 'vue-router'

import App from './App.vue'

import DashboardView from './views/DashboardView.vue'
import DepartmentsView from './views/DepartmentsView.vue'
import EmployeesView from './views/EmployeesView.vue'

import './style.css'

const router = createRouter({
  history: createWebHistory(),

  routes: [
    {
      path: '/',
      component: DashboardView
    },

    {
      path: '/departments',
      component: DepartmentsView
    },

    {
      path: '/employees',
      component: EmployeesView
    }
  ]
})

createApp(App)
  .use(router)
  .mount('#app')