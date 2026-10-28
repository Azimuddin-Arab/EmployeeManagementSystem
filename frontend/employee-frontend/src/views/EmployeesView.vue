
<script setup>
import { onMounted, onBeforeUnmount, ref, computed, watch } from 'vue'

import {
  getEmployees,
  createEmployee,
  updateEmployee,
  deleteEmployee,
  getDepartments
} from '../api/api'


// ========================================
// DATA
// ========================================

const employees = ref([])
const departments = ref([])

const loading = ref(false)
const saving = ref(false)

const error = ref('')
const success = ref('')

// Search and pagination state. The page number is zero-based.
const search = ref('')
const page = ref(0)
const size = ref(10)
const totalElements = ref(0)
const totalPages = ref(0)

const pageNumbers = computed(() =>
  Array.from({ length: totalPages.value }, (_, index) => index)
)

const editingId = ref(null)
let searchTimer = null


// ========================================
// FORM
// ========================================

const employee = ref({
  fullName: '',
  email: '',
  position: '',
  departmentId: ''
})


// ========================================
// LOAD DATA FROM THE BACKEND
// ========================================

async function loadData() {
  loading.value = true
  error.value = ''

  try {
    const [employeesResponse, departmentsResponse] = await Promise.all([
      getEmployees(search.value.trim(), page.value, size.value),
      getDepartments()
    ])

    const result = employeesResponse.data

    // Spring Boot Page response contains content and pagination metadata.
    employees.value = result.content ?? []
    totalElements.value = result.totalElements ?? 0
    totalPages.value = result.totalPages ?? 0
    departments.value = departmentsResponse.data

    // If the current page no longer exists (for example after deleting the
    // last employee on a page), move to the last available page and reload.
    if (totalPages.value > 0 && page.value >= totalPages.value) {
      page.value = totalPages.value - 1
      await loadData()
      return
    }

    if (totalPages.value === 0) {
      page.value = 0
    }
  } catch (err) {
    console.error(err)
    error.value = 'Could not load employee data.'
  } finally {
    loading.value = false
  }
}


// ========================================
// RESET FORM
// ========================================

function resetForm() {

  employee.value = {

    fullName: '',

    email: '',

    position: '',

    departmentId: ''

  }

  editingId.value = null

  error.value = ''
}


// ========================================
// EDIT
// ========================================

function editEmployee(emp) {

  editingId.value = emp.id

  employee.value = {

    fullName: emp.fullName || '',

    email: emp.email || '',

    position: emp.position || '',

    departmentId:
      emp.department?.id || ''

  }

  success.value = ''

  error.value = ''

  window.scrollTo({

    top: 0,

    behavior: 'smooth'

  })

}


// ========================================
// SAVE
// ========================================

async function saveEmployee() {

  error.value = ''
  success.value = ''

  if (!employee.value.fullName.trim()) {

    error.value =
      'Full Name is required.'

    return
  }

  if (!employee.value.email.trim()) {

    error.value =
      'Email is required.'

    return
  }

  if (!employee.value.position.trim()) {

    error.value =
      'Position is required.'

    return
  }

  if (!employee.value.departmentId) {

    error.value =
      'Please select a department.'

    return
  }


  const data = {

    fullName:
      employee.value.fullName.trim(),

    email:
      employee.value.email.trim(),

    position:
      employee.value.position.trim(),

    departmentId:
      Number(employee.value.departmentId)

  }


  saving.value = true

  try {

    if (editingId.value !== null) {

      await updateEmployee(

        editingId.value,

        data

      )

      success.value =
        'Employee updated successfully.'

    } else {

      await createEmployee(data)

      success.value =
        'Employee created successfully.'

    }

    resetForm()

    // After creating/updating, show the first page so the saved employee is easy to find.
    if (page.value !== 0) {
      page.value = 0 // The page watcher will reload the data.
    } else {
      await loadData()
    }

  } catch (err) {

    console.error(err)

    error.value =
      err.response?.data?.message ||
      'Could not save employee.'

  } finally {

    saving.value = false

  }
}


// ========================================
// DELETE
// ========================================

async function removeEmployee(emp) {

  const confirmed = confirm(

    `Are you sure you want to delete "${emp.fullName}"?`

  )

  if (!confirmed) {
    return
  }


  try {

    await deleteEmployee(emp.id)

    success.value =
      'Employee deleted successfully.'

    await loadData()

  } catch (err) {

    console.error(err)

    error.value =
      'Could not delete employee.'

  }
}


// ========================================
// SEARCH, PAGINATION, AND INIT
// ========================================

watch(search, () => {
  clearTimeout(searchTimer)

  // A new search always starts from the first page.
  if (page.value !== 0) {
    page.value = 0
    return // The page watcher will load the new search.
  }

  // Debounce typing so we do not send a request for every keystroke.
  searchTimer = setTimeout(() => {
    loadData()
  }, 300)
})

watch([page, size], () => {
  loadData()
})

onMounted(() => {
  loadData()
})

onBeforeUnmount(() => {
  clearTimeout(searchTimer)
})
</script>


<template>

  <section class="employees-page">

    <!-- ================================= -->
    <!-- PAGE HEADER -->
    <!-- ================================= -->

    <header class="page-header">

      <div class="header-title">

        <div class="page-icon">
          👥
        </div>

        <div>

          <span class="eyebrow">
            PEOPLE MANAGEMENT
          </span>

          <h1>
            Employees
          </h1>

          <p>
            Manage employee records and departments
          </p>

        </div>

      </div>


      <div class="header-actions">

        <button
          class="refresh-button"
          @click="loadData"
        >

          <span>
            ↻
          </span>

          Refresh

        </button>

        <button
          class="add-button"
          @click="resetForm"
        >

          <span>
            +
          </span>

          Add Employee

        </button>

      </div>

    </header>


    <!-- ================================= -->
    <!-- ALERTS -->
    <!-- ================================= -->

    <div
      v-if="success"
      class="alert success-alert"
    >

      <div class="alert-icon">
        ✓
      </div>

      <div>

        <strong>
          Success
        </strong>

        <p>
          {{ success }}
        </p>

      </div>

      <button
        @click="success = ''"
      >
        ×
      </button>

    </div>


    <div
      v-if="error"
      class="alert error-alert"
    >

      <div class="alert-icon">
        !
      </div>

      <div>

        <strong>
          Error
        </strong>

        <p>
          {{ error }}
        </p>

      </div>

      <button
        @click="error = ''"
      >
        ×
      </button>

    </div>


    <!-- ================================= -->
    <!-- FORM -->
    <!-- ================================= -->

    <div class="form-panel">

      <div class="form-header">

        <div>

          <span class="section-label">
            {{ editingId !== null ? 'EDIT RECORD' : 'NEW RECORD' }}
          </span>

          <h2>
            {{ editingId !== null
              ? 'Edit Employee'
              : 'Add New Employee'
            }}
          </h2>

        </div>


        <div
          v-if="editingId !== null"
          class="editing-badge"
        >
          Editing #{{ editingId }}
        </div>

      </div>


      <form
        class="employee-form"
        @submit.prevent="saveEmployee"
      >

        <!-- Full Name -->

        <div class="form-group">

          <label>
            Full Name
            <span>*</span>
          </label>

          <input
            v-model="employee.fullName"
            type="text"
            placeholder="e.g. Ahmad Ahmad"
            autocomplete="name"
          />

        </div>


        <!-- Email -->

        <div class="form-group">

          <label>
            Email Address
            <span>*</span>
          </label>

          <input
            v-model="employee.email"
            type="email"
            placeholder="e.g. ahmad@example.com"
            autocomplete="email"
          />

        </div>


        <!-- Position -->

        <div class="form-group">

          <label>
            Position
            <span>*</span>
          </label>

          <input
            v-model="employee.position"
            type="text"
            placeholder="e.g. Software Developer"
          />

        </div>


        <!-- Department -->

        <div class="form-group">

          <label>
            Department
            <span>*</span>
          </label>

          <select
            v-model="employee.departmentId"
          >

            <option value="">
              Select department
            </option>

            <option
              v-for="department in departments"
              :key="department.id"
              :value="department.id"
            >
              {{ department.name }}
            </option>

          </select>

        </div>


        <!-- Buttons -->

        <div class="form-actions">

          <button
            type="submit"
            class="save-button"
            :disabled="saving"
          >

            <span v-if="saving">
              Saving...
            </span>

            <span v-else>
              {{ editingId !== null
                ? 'Update Employee'
                : 'Save Employee'
              }}
            </span>

          </button>


          <button
            v-if="editingId !== null"
            type="button"
            class="cancel-button"
            @click="resetForm"
          >
            Cancel
          </button>

        </div>

      </form>

    </div>


    <!-- ================================= -->
    <!-- TABLE PANEL -->
    <!-- ================================= -->

    <div class="table-panel">

      <div class="table-header">

        <div>

          <span class="section-label">
            DIRECTORY
          </span>

          <h2>
            Employee List
          </h2>

          <p>
            {{ totalElements }} employees registered
          </p>

        </div>


        <!-- Search -->

        <div class="search-box">

          <span>
            ⌕
          </span>

          <input
            v-model="search"
            type="text"
            placeholder="Search employees..."
          />

          <button
            v-if="search"
            type="button"
            aria-label="Clear search"
            @click="search = ''"
          >
            ×
          </button>

        </div>

      </div>


      <!-- Loading -->

      <div
        v-if="loading"
        class="loading"
      >

        <div class="spinner"></div>

        <span>
          Loading employees...
        </span>

      </div>


      <!-- Table -->

      <div
        v-else-if="employees.length"
        class="table-wrapper"
      >

        <table>

          <thead>

            <tr>

              <th>
                #
              </th>

              <th>
                Employee
              </th>

              <th>
                Email
              </th>

              <th>
                Department
              </th>

              <th>
                Position
              </th>

              <th class="actions-column">
                Actions
              </th>

            </tr>

          </thead>


          <tbody>

            <tr
              v-for="emp in employees"
              :key="emp.id"
            >

              <!-- ID -->

              <td>

                <span class="employee-id">
                  #{{ emp.id }}
                </span>

              </td>


              <!-- Employee -->

              <td>

                <div class="employee-cell">

                  <div class="avatar">

                    {{
                      emp.fullName
                        ? emp.fullName
                            .charAt(0)
                            .toUpperCase()
                        : '?'
                    }}

                  </div>

                  <div>

                    <strong>
                      {{ emp.fullName }}
                    </strong>

                    <span>
                      Employee #{{ emp.id }}
                    </span>

                  </div>

                </div>

              </td>


              <!-- Email -->

              <td>

                <span class="email">
                  {{ emp.email }}
                </span>

              </td>


              <!-- Department -->

              <td>

                <span class="department-badge">

                  {{
                    emp.department?.name ||
                    'Not assigned'
                  }}

                </span>

              </td>


              <!-- Position -->

              <td>

                <span class="position-text">
                  {{ emp.position }}
                </span>

              </td>


              <!-- Actions -->

              <td>

                <div class="action-buttons">

                  <button
                    class="edit-button"
                    title="Edit employee"
                    @click="editEmployee(emp)"
                  >
                    ✎
                  </button>

                  <button
                    class="delete-button"
                    title="Delete employee"
                    @click="removeEmployee(emp)"
                  >
                    🗑
                  </button>

                </div>

              </td>

            </tr>

          </tbody>

        </table>

      </div>


      <!-- Empty -->

      <div
        v-else
        class="empty-state"
      >

        <div class="empty-icon">
          👥
        </div>

        <h3>
          {{ search
            ? 'No employees found'
            : 'No employees yet'
          }}
        </h3>

        <p>
          {{
            search
              ? 'Try a different search term.'
              : 'Add your first employee to get started.'
          }}
        </p>

        <button
          v-if="!search"
          class="add-button"
          @click="resetForm"
        >
          + Add Employee
        </button>

      </div>

      <!-- Pagination -->

      <div
        v-if="!loading && totalElements > 0"
        class="pagination"
      >

        <div class="pagination-info">
          Showing {{ page * size + 1 }} to
          {{ Math.min((page + 1) * size, totalElements) }}
          of {{ totalElements }} employees
        </div>

        <div class="pagination-controls">

          <label for="pageSize">Rows:</label>

          <select id="pageSize" v-model.number="size">
            <option :value="5">5</option>
            <option :value="10">10</option>
            <option :value="20">20</option>
            <option :value="50">50</option>
          </select>

          <button
            type="button"
            :disabled="page === 0"
            @click="page--"
          >
            Previous
          </button>

          <button
            v-for="n in pageNumbers"
            :key="n"
            type="button"
            :class="{ active: page === n }"
            :aria-current="page === n ? 'page' : undefined"
            @click="page = n"
          >
            {{ n + 1 }}
          </button>

          <button
            type="button"
            :disabled="page >= totalPages - 1"
            @click="page++"
          >
            Next
          </button>

        </div>

      </div>

    </div>

  </section>

</template>


<style scoped>

* {
  box-sizing: border-box;
}


/* ================================= */
/* PAGE */
/* ================================= */

.employees-page {

  min-height: 100vh;

  padding: 32px 36px 40px;

  background: #f5f7fb;

}


/* ================================= */
/* HEADER */
/* ================================= */

.page-header {

  display: flex;

  align-items: center;

  justify-content: space-between;

  gap: 25px;

  margin-bottom: 28px;

}


.header-title {

  display: flex;

  align-items: center;

  gap: 16px;

}


.page-icon {

  width: 52px;
  height: 52px;

  display: flex;

  align-items: center;
  justify-content: center;

  border-radius: 14px;

  background: #2563eb;

  color: white;

  font-size: 22px;

  box-shadow:
    0 8px 20px rgba(37,99,235,.18);

}


.eyebrow,
.section-label {

  display: block;

  font-size: 10px;

  font-weight: 750;

  letter-spacing: 1.3px;

  color: #2563eb;

  margin-bottom: 5px;

}


.page-header h1 {

  margin: 0;

  font-size: 29px;

  color: #111827;

}


.page-header p {

  margin: 5px 0 0;

  color: #6b7280;

  font-size: 13px;

}


.header-actions {

  display: flex;

  gap: 10px;

}


/* ================================= */
/* BUTTONS */
/* ================================= */

button {

  border: 0;

  cursor: pointer;

  font-family: inherit;

}


.refresh-button,
.add-button {

  display: flex;

  align-items: center;

  gap: 8px;

  padding: 11px 16px;

  border-radius: 8px;

  font-size: 12px;

  font-weight: 650;

}


.refresh-button {

  background: white;

  color: #374151;

  border: 1px solid #e5e7eb;

}


.add-button {

  background: #2563eb;

  color: white;

  box-shadow:
    0 5px 15px rgba(37,99,235,.18);

}


.add-button:hover {

  background: #1d4ed8;

}


.refresh-button:hover {

  background: #f8fafc;

}


/* ================================= */
/* ALERT */
/* ================================= */

.alert {

  display: flex;

  align-items: center;

  gap: 12px;

  padding: 13px 16px;

  margin-bottom: 18px;

  border-radius: 9px;

}


.alert-icon {

  width: 27px;
  height: 27px;

  flex-shrink: 0;

  display: flex;

  align-items: center;
  justify-content: center;

  border-radius: 50%;

  color: white;

  font-weight: 700;

}


.alert strong {

  font-size: 12px;

}


.alert p {

  margin: 2px 0 0;

  font-size: 11px;

}


.alert button {

  margin-left: auto;

  background: transparent;

  font-size: 19px;

}


.success-alert {

  background: #f0fdf4;

  border: 1px solid #bbf7d0;

  color: #166534;

}


.success-alert .alert-icon {

  background: #16a34a;

}


.error-alert {

  background: #fef2f2;

  border: 1px solid #fecaca;

  color: #991b1b;

}


.error-alert .alert-icon {

  background: #dc2626;

}


/* ================================= */
/* FORM */
/* ================================= */

.form-panel,
.table-panel {

  background: white;

  border: 1px solid #e5e7eb;

  border-radius: 14px;

  margin-bottom: 22px;

}


.form-panel {

  padding: 25px;

}


.form-header {

  display: flex;

  align-items: center;

  justify-content: space-between;

  padding-bottom: 20px;

  border-bottom: 1px solid #f1f5f9;

  margin-bottom: 20px;

}


.form-header h2,
.table-header h2 {

  margin: 0;

  font-size: 18px;

  color: #111827;

}


.editing-badge {

  padding: 6px 10px;

  background: #eff6ff;

  color: #2563eb;

  border-radius: 20px;

  font-size: 10px;

  font-weight: 700;

}


.employee-form {

  display: grid;

  grid-template-columns:
    repeat(2, 1fr);

  gap: 18px;

}


.form-group {

  display: flex;

  flex-direction: column;

  gap: 7px;

}


.form-group label {

  font-size: 12px;

  font-weight: 650;

  color: #374151;

}


.form-group label span {

  color: #dc2626;

}


.form-group input,
.form-group select {

  width: 100%;

  padding: 11px 13px;

  border: 1px solid #dbe1e8;

  border-radius: 7px;

  background: white;

  color: #374151;

  font-size: 12px;

  outline: none;

  transition: .2s;

}


.form-group input::placeholder {

  color: #b0b7c3;

}


.form-group input:focus,
.form-group select:focus {

  border-color: #2563eb;

  box-shadow:
    0 0 0 3px rgba(37,99,235,.08);

}


.form-actions {

  grid-column: 1 / -1;

  display: flex;

  gap: 9px;

  padding-top: 5px;

}


.save-button,
.cancel-button {

  padding: 11px 18px;

  border-radius: 7px;

  font-size: 12px;

  font-weight: 650;

}


.save-button {

  background: #2563eb;

  color: white;

}


.save-button:hover {

  background: #1d4ed8;

}


.save-button:disabled {

  opacity: .6;

  cursor: not-allowed;

}


.cancel-button {

  background: #f3f4f6;

  color: #374151;

}


/* ================================= */
/* TABLE HEADER */
/* ================================= */

.table-panel {

  overflow: hidden;

}


.table-header {

  display: flex;

  justify-content: space-between;

  align-items: center;

  gap: 20px;

  padding: 24px;

  border-bottom: 1px solid #eef2f7;

}


.table-header p {

  margin: 5px 0 0;

  color: #9ca3af;

  font-size: 11px;

}


/* ================================= */
/* SEARCH */
/* ================================= */

.search-box {

  width: 260px;

  display: flex;

  align-items: center;

  gap: 8px;

  padding: 9px 11px;

  background: #f8fafc;

  border: 1px solid #e5e7eb;

  border-radius: 8px;

}


.search-box > span {

  color: #9ca3af;

  font-size: 17px;

}


.search-box input {

  flex: 1;

  border: 0;

  outline: 0;

  background: transparent;

  font-size: 12px;

}


.search-box button {

  background: transparent;

  color: #9ca3af;

  font-size: 17px;

}


/* ================================= */
/* TABLE */
/* ================================= */

.table-wrapper {

  width: 100%;

  overflow-x: auto;

}


table {

  width: 100%;

  border-collapse: collapse;

  min-width: 850px;

}


thead {

  background: #f8fafc;

}


th {

  padding: 13px 18px;

  text-align: left;

  font-size: 10px;

  text-transform: uppercase;

  letter-spacing: .7px;

  color: #6b7280;

  font-weight: 750;

}


td {

  padding: 14px 18px;

  border-top: 1px solid #f1f5f9;

  color: #374151;

  font-size: 12px;

}


tbody tr {

  transition: .15s;

}


tbody tr:hover {

  background: #fafcff;

}


/* ================================= */
/* EMPLOYEE CELL */
/* ================================= */

.employee-cell {

  display: flex;

  align-items: center;

  gap: 11px;

}


.avatar {

  width: 36px;
  height: 36px;

  display: flex;

  align-items: center;
  justify-content: center;

  flex-shrink: 0;

  background: #eff6ff;

  color: #2563eb;

  border-radius: 50%;

  font-weight: 750;

  font-size: 12px;

}


.employee-cell div:last-child {

  display: flex;

  flex-direction: column;

  gap: 3px;

}


.employee-cell strong {

  color: #1f2937;

  font-size: 12px;

}


.employee-cell span {

  color: #9ca3af;

  font-size: 10px;

}


.employee-id {

  color: #9ca3af;

  font-size: 10px;

  font-weight: 650;

}


.email {

  color: #6b7280;

}


.department-badge {

  display: inline-block;

  padding: 5px 9px;

  background: #f0fdf4;

  color: #15803d;

  border-radius: 5px;

  font-size: 10px;

  font-weight: 650;

}


.position-text {

  color: #4b5563;

}


/* ================================= */
/* ACTIONS */
/* ================================= */

.actions-column {

  text-align: center;

}


.action-buttons {

  display: flex;

  align-items: center;

  justify-content: center;

  gap: 6px;

}


.edit-button,
.delete-button {

  width: 31px;
  height: 31px;

  display: flex;

  align-items: center;
  justify-content: center;

  border-radius: 6px;

  font-size: 13px;

}


.edit-button {

  background: #eff6ff;

  color: #2563eb;

}


.edit-button:hover {

  background: #dbeafe;

}


.delete-button {

  background: #fef2f2;

  color: #dc2626;

}


.delete-button:hover {

  background: #fee2e2;

}


/* ================================= */
/* LOADING */
/* ================================= */

.loading {

  min-height: 300px;

  display: flex;

  flex-direction: column;

  align-items: center;

  justify-content: center;

  gap: 12px;

  color: #9ca3af;

  font-size: 12px;

}


.spinner {

  width: 30px;
  height: 30px;

  border: 3px solid #e5e7eb;

  border-top-color: #2563eb;

  border-radius: 50%;

  animation: spin .8s linear infinite;

}


@keyframes spin {

  to {
    transform: rotate(360deg);
  }

}


/* ================================= */
/* EMPTY */
/* ================================= */

.empty-state {

  min-height: 300px;

  display: flex;

  flex-direction: column;

  align-items: center;

  justify-content: center;

  text-align: center;

}


.empty-icon {

  width: 60px;
  height: 60px;

  display: flex;

  align-items: center;
  justify-content: center;

  border-radius: 50%;

  background: #f8fafc;

  font-size: 25px;

  margin-bottom: 12px;

}


.empty-state h3 {

  margin: 0;

  color: #374151;

  font-size: 15px;

}


.empty-state p {

  margin: 6px 0 18px;

  color: #9ca3af;

  font-size: 12px;

}


/* ================================= */
/* RESPONSIVE */
/* ================================= */

@media (max-width: 900px) {

  .employees-page {

    padding: 24px 20px;

  }

  .statistics-grid {

    grid-template-columns: 1fr 1fr;

  }

}


@media (max-width: 700px) {

  .page-header {

    align-items: flex-start;

    flex-direction: column;

  }

  .header-actions {

    width: 100%;

  }

  .header-actions button {

    flex: 1;

    justify-content: center;

  }

  .employee-form {

    grid-template-columns: 1fr;

  }

  .form-actions {

    grid-column: auto;

  }

  .table-header {

    align-items: flex-start;

    flex-direction: column;

  }

  .search-box {

    width: 100%;

  }

}


@media (max-width: 480px) {

  .employees-page {

    padding: 18px 12px;

  }

  .page-header h1 {

    font-size: 24px;

  }

  .page-icon {

    width: 45px;
    height: 45px;

  }

}



/* ================================= */
/* PAGINATION */
/* ================================= */

.pagination {
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 16px;
  padding: 18px 24px;
  border-top: 1px solid #eef2f7;
}

.pagination-info {
  font-size: 12px;
  color: #6b7280;
}

.pagination-controls {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 6px;
}

.pagination-controls label {
  font-size: 12px;
  color: #6b7280;
}

.pagination-controls select,
.pagination-controls button {
  padding: 8px 11px;
  border: 1px solid #e5e7eb;
  border-radius: 6px;
  background: white;
  color: #374151;
  font-size: 12px;
  cursor: pointer;
}

.pagination-controls button.active {
  background: #2563eb;
  border-color: #2563eb;
  color: white;
}

.pagination-controls button:disabled {
  opacity: 0.45;
  cursor: not-allowed;
}

.pagination-controls button:not(:disabled):hover {
  border-color: #2563eb;
}

@media (max-width: 640px) {
  .pagination {
    padding: 16px;
    align-items: flex-start;
  }

  .pagination-controls {
    width: 100%;
  }
}

</style>
