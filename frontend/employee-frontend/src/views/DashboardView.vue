<script setup>
import { onMounted, ref, computed } from 'vue'
import { getDepartments, getEmployees } from '../api/api'

const departmentCount = ref(0)
const employeeCount = ref(0)
const departments = ref([])
const employees = ref([])
const error = ref('')
const loading = ref(true)

const currentDate = computed(() => {
  return new Intl.DateTimeFormat('en-US', {
    weekday: 'long',
    year: 'numeric',
    month: 'long',
    day: 'numeric'
  }).format(new Date())
})

async function loadDashboard() {
  loading.value = true
  error.value = ''

  try {
    // Dashboard needs all employees for department counts.
    // Employee page itself uses server-side pagination.
    const [departmentResponse, employeeResponse] = await Promise.all([
      getDepartments('', 0, 1000),
      getEmployees('', 0, 1000)
    ])

    departments.value = departmentResponse.data.content ?? departmentResponse.data
    employees.value = employeeResponse.data.content ?? employeeResponse.data

    departmentCount.value = departmentResponse.data.totalElements ?? departments.value.length
    employeeCount.value = employeeResponse.data.totalElements ?? employees.value.length
  } catch (err) {
    console.error(err)
    error.value = 'Could not load dashboard data. Please check your server.'
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadDashboard()
})
</script>

<template>
  <section class="dashboard">

    <header class="dashboard-header">
      <div class="header-left">
        <div class="welcome-icon">
          <span>⌂</span>
        </div>

        <div>
          <span class="eyebrow">OVERVIEW</span>
          <h1>Dashboard</h1>
          <p>Welcome to your Employee Management System</p>
        </div>
      </div>

      <div class="header-right">
        <div class="date-box">
          <span class="date-icon">◷</span>

          <div>
            <span class="date-label">Today</span>
            <strong>{{ currentDate }}</strong>
          </div>
        </div>

        <button
          class="refresh-button"
          :class="{ refreshing: loading }"
          @click="loadDashboard"
        >
          <span class="refresh-icon">↻</span>
          Refresh
        </button>
      </div>
    </header>

    <div v-if="error" class="alert alert-error">
      <span class="alert-icon">!</span>

      <div>
        <strong>Unable to load dashboard</strong>
        <p>{{ error }}</p>
      </div>
    </div>

    <div class="statistics-grid">

      <div class="stat-card employee-card">
        <div class="stat-top">
          <div class="stat-icon employee-icon">
            <span>👥</span>
          </div>
          <span class="stat-status">Active</span>
        </div>

        <div class="stat-content">
          <span class="stat-label">Total Employees</span>

          <div class="stat-value">
            <span v-if="loading" class="skeleton-number">&nbsp;</span>
            <span v-else>{{ employeeCount }}</span>
          </div>

          <p class="stat-description">
            Employees registered in the system
          </p>
        </div>

        <div class="stat-footer">
          <RouterLink to="/employees" class="stat-link">
            View Employees
            <span>→</span>
          </RouterLink>
        </div>
      </div>

      <div class="stat-card department-card">
        <div class="stat-top">
          <div class="stat-icon department-icon">
            <span>▦</span>
          </div>
          <span class="stat-status">Active</span>
        </div>

        <div class="stat-content">
          <span class="stat-label">Total Departments</span>

          <div class="stat-value">
            <span v-if="loading" class="skeleton-number">&nbsp;</span>
            <span v-else>{{ departmentCount }}</span>
          </div>

          <p class="stat-description">
            Departments currently configured
          </p>
        </div>

        <div class="stat-footer">
          <RouterLink to="/departments" class="stat-link">
            View Departments
            <span>→</span>
          </RouterLink>
        </div>
      </div>

      <div class="stat-card average-card">
        <div class="stat-top">
          <div class="stat-icon average-icon">
            <span>↗</span>
          </div>
          <span class="stat-status neutral">Overview</span>
        </div>

        <div class="stat-content">
          <span class="stat-label">Employees / Department</span>

          <div class="stat-value">
            <span v-if="loading" class="skeleton-number">&nbsp;</span>
            <span v-else>
              {{
                departmentCount > 0
                  ? (employeeCount / departmentCount).toFixed(1)
                  : '0'
              }}
            </span>
          </div>

          <p class="stat-description">
            Average employees per department
          </p>
        </div>

        <div class="stat-footer">
          <RouterLink to="/departments" class="stat-link">
            Manage Departments
            <span>→</span>
          </RouterLink>
        </div>
      </div>

      <div class="stat-card system-card">
        <div class="stat-top">
          <div class="stat-icon system-icon">
            <span>✓</span>
          </div>
          <span class="stat-status success">Online</span>
        </div>

        <div class="stat-content">
          <span class="stat-label">System Status</span>
          <div class="status-value">Operational</div>

          <p class="stat-description">
            API connection is available
          </p>
        </div>

        <div class="stat-footer">
          <span class="system-info">Spring Boot API</span>
        </div>
      </div>

    </div>

    <div class="content-grid">

      <div class="dashboard-panel employees-panel">
        <div class="panel-header">
          <div>
            <span class="panel-eyebrow">PEOPLE</span>
            <h2>Recent Employees</h2>
            <p>Employees currently registered</p>
          </div>

          <RouterLink to="/employees" class="view-all">
            View all
            <span>→</span>
          </RouterLink>
        </div>

        <div v-if="loading" class="loading-container">
          <div class="loading-spinner"></div>
          <span>Loading employees...</span>
        </div>

        <div v-else-if="employees.length === 0" class="empty-state">
          <div class="empty-icon">👥</div>
          <h3>No employees yet</h3>
          <p>Add your first employee to get started.</p>

          <RouterLink to="/employees" class="primary-button">
            Add Employee
          </RouterLink>
        </div>

        <div v-else class="employee-list">
          <div
            v-for="employee in employees.slice(0, 5)"
            :key="employee.id"
            class="employee-row"
          >
            <div class="employee-avatar">
              {{
                employee.fullName
                  ? employee.fullName.charAt(0).toUpperCase()
                  : '?'
              }}
            </div>

            <div class="employee-info">
              <strong>{{ employee.fullName }}</strong>
              <span>{{ employee.email }}</span>
            </div>

            <div class="employee-position">
              <span class="position">
                {{ employee.position || 'No position' }}
              </span>

              <span class="department">
                {{ employee.department?.name || 'No department' }}
              </span>
            </div>

            <div class="employee-arrow">→</div>
          </div>
        </div>
      </div>

      <div class="dashboard-panel departments-panel">
        <div class="panel-header">
          <div>
            <span class="panel-eyebrow">ORGANIZATION</span>
            <h2>Departments</h2>
            <p>Department overview</p>
          </div>

          <RouterLink to="/departments" class="view-all">
            Manage
            <span>→</span>
          </RouterLink>
        </div>

        <div v-if="loading" class="loading-container">
          <div class="loading-spinner"></div>
          <span>Loading departments...</span>
        </div>

        <div v-else-if="departments.length === 0" class="empty-state">
          <div class="empty-icon">▦</div>
          <h3>No departments</h3>
          <p>Create a department to organize employees.</p>

          <RouterLink to="/departments" class="primary-button">
            Add Department
          </RouterLink>
        </div>

        <div v-else class="department-list">
          <RouterLink
            v-for="(department, index) in departments.slice(0, 6)"
            :key="department.id"
            to="/departments"
            class="department-row"
          >
            <div class="department-number">
              {{ String(index + 1).padStart(2, '0') }}
            </div>

            <div class="department-info">
              <strong>{{ department.name }}</strong>
              <span>Department ID #{{ department.id }}</span>
            </div>

            <div class="department-count">
              <strong>
                {{
                  employees.filter(
                    employee =>
                      employee.department?.id === department.id
                  ).length
                }}
              </strong>
              <span>employees</span>
            </div>
          </RouterLink>
        </div>
      </div>

    </div>

    <div class="quick-actions">
      <div class="quick-header">
        <div>
          <span class="panel-eyebrow">QUICK ACTIONS</span>
          <h2>What would you like to do?</h2>
        </div>
      </div>

      <div class="quick-grid">

        <RouterLink to="/employees" class="quick-card">
          <div class="quick-icon">+</div>

          <div>
            <strong>Add Employee</strong>
            <span>Register a new employee</span>
          </div>

          <span class="quick-arrow">→</span>
        </RouterLink>

        <RouterLink to="/departments" class="quick-card">
          <div class="quick-icon">+</div>

          <div>
            <strong>Add Department</strong>
            <span>Create a new department</span>
          </div>

          <span class="quick-arrow">→</span>
        </RouterLink>

        <RouterLink to="/employees" class="quick-card">
          <div class="quick-icon">👥</div>

          <div>
            <strong>View Employees</strong>
            <span>Browse employee records</span>
          </div>

          <span class="quick-arrow">→</span>
        </RouterLink>

        <RouterLink to="/departments" class="quick-card">
          <div class="quick-icon">▦</div>

          <div>
            <strong>View Departments</strong>
            <span>Manage organization structure</span>
          </div>

          <span class="quick-arrow">→</span>
        </RouterLink>

      </div>
    </div>

    <footer class="dashboard-footer">
      <span>Employee Management System</span>
      <span>Spring Boot + Vue.js + PostgreSQL</span>
    </footer>

  </section>
</template>
