
<script setup>
import { onMounted, ref, computed } from 'vue'

import {
  getDepartments,
  createDepartment,
  updateDepartment,
  deleteDepartment,
  getEmployees
} from '../api/api'


// ========================================
// DATA
// ========================================

const departments = ref([])
const employees = ref([])

const loading = ref(false)
const saving = ref(false)

const error = ref('')
const success = ref('')

const search = ref('')

const editingId = ref(null)


// ========================================
// FORM
// ========================================

const department = ref({
  name: ''
})


// ========================================
// FILTER
// ========================================

const filteredDepartments = computed(() => {

  const keyword =
    search.value.toLowerCase().trim()

  if (!keyword) {
    return departments.value
  }

  return departments.value.filter(dep =>
    dep.name?.toLowerCase().includes(keyword)
  )
})


// ========================================
// EMPLOYEE COUNT
// ========================================

function employeeCount(departmentId) {

  return employees.value.filter(
    employee =>
      employee.department?.id === departmentId
  ).length
}


// ========================================
// LOAD DATA
// ========================================

async function loadData() {

  loading.value = true

  error.value = ''

  try {

    const [
      departmentResponse,
      employeeResponse
    ] = await Promise.all([

      getDepartments('', 0, 1000),

      getEmployees('', 0, 1000)

    ])

    departments.value =
      departmentResponse.data.content ?? departmentResponse.data

    employees.value =
      employeeResponse.data.content ?? employeeResponse.data

  } catch (err) {

    console.error(err)

    error.value =
      'Could not load department data.'

  } finally {

    loading.value = false

  }
}


// ========================================
// RESET
// ========================================

function resetForm() {

  department.value = {
    name: ''
  }

  editingId.value = null

  error.value = ''

}


// ========================================
// EDIT
// ========================================

function editDepartment(dep) {

  editingId.value = dep.id

  department.value = {

    name: dep.name || ''

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

async function saveDepartment() {

  error.value = ''

  success.value = ''

  const name =
    department.value.name.trim()

  if (!name) {

    error.value =
      'Department name is required.'

    return

  }


  saving.value = true

  try {

    if (editingId.value !== null) {

      await updateDepartment(

        editingId.value,

        {
          name: name
        }

      )

      success.value =
        'Department updated successfully.'

    } else {

      await createDepartment({

        name: name

      })

      success.value =
        'Department created successfully.'

    }


    resetForm()

    await loadData()

  } catch (err) {

    console.error(err)

    error.value =
      err.response?.data?.message ||
      'Could not save department.'

  } finally {

    saving.value = false

  }
}


// ========================================
// DELETE
// ========================================

async function removeDepartment(dep) {

  const count =
    employeeCount(dep.id)


  if (count > 0) {

    const confirmed =
      confirm(
        `"${dep.name}" has ${count} employee(s). Are you sure you want to delete it?`
      )

    if (!confirmed) {
      return
    }

  } else {

    const confirmed =
      confirm(
        `Are you sure you want to delete "${dep.name}"?`
      )

    if (!confirmed) {
      return
    }

  }


  try {

    await deleteDepartment(dep.id)

    success.value =
      'Department deleted successfully.'

    await loadData()

  } catch (err) {

    console.error(err)

    error.value =
      'Could not delete department.'

  }
}


// ========================================
// INIT
// ========================================

onMounted(() => {

  loadData()

})
</script>


<template>

  <section class="departments-page">

    <!-- ================================= -->
    <!-- HEADER -->
    <!-- ================================= -->

    <header class="page-header">

      <div class="header-title">

        <div class="page-icon">
          ▦
        </div>

        <div>

          <span class="eyebrow">
            ORGANIZATION MANAGEMENT
          </span>

          <h1>
            Departments
          </h1>

          <p>
            Manage organizational departments
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

          Add Department

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
            {{
              editingId !== null
                ? 'EDIT RECORD'
                : 'NEW RECORD'
            }}
          </span>

          <h2>

            {{
              editingId !== null
                ? 'Edit Department'
                : 'Add New Department'
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
        class="department-form"
        @submit.prevent="saveDepartment"
      >

        <div class="form-group">

          <label>
            Department Name
            <span>*</span>
          </label>

          <input
            v-model="department.name"
            type="text"
            placeholder="e.g. Information Technology"
          />

        </div>


        <div class="form-actions">

          <button
            type="submit"
            class="save-button"
            :disabled="saving"
          >

            {{
              saving
                ? 'Saving...'
                : editingId !== null
                  ? 'Update Department'
                  : 'Save Department'
            }}

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
    <!-- DEPARTMENT TABLE -->
    <!-- ================================= -->

    <div class="table-panel">

      <div class="table-header">

        <div>

          <span class="section-label">
            ORGANIZATION
          </span>

          <h2>
            Department List
          </h2>

          <p>
            {{ departments.length }}
            departments registered
          </p>

        </div>


        <div class="search-box">

          <span>
            ⌕
          </span>

          <input
            v-model="search"
            type="text"
            placeholder="Search departments..."
          />

          <button
            v-if="search"
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
          Loading departments...
        </span>

      </div>


      <!-- Table -->

      <div
        v-else-if="filteredDepartments.length"
        class="table-wrapper"
      >

        <table>

          <thead>

            <tr>

              <th>
                #
              </th>

              <th>
                Department
              </th>

              <th>
                Employees
              </th>

              <th>
                Status
              </th>

              <th class="actions-column">
                Actions
              </th>

            </tr>

          </thead>


          <tbody>

            <tr
              v-for="(dep, index) in filteredDepartments"
              :key="dep.id"
            >

              <!-- Number -->

              <td>

                <span class="department-number">
                  {{ String(index + 1).padStart(2, '0') }}
                </span>

              </td>


              <!-- Department -->

              <td>

                <div class="department-cell">

                  <div class="department-icon">
                    ▦
                  </div>

                  <div>

                    <strong>
                      {{ dep.name }}
                    </strong>

                    <span>
                      Department #{{ dep.id }}
                    </span>

                  </div>

                </div>

              </td>


              <!-- Employees -->

              <td>

                <div class="employee-count">

                  <strong>
                    {{ employeeCount(dep.id) }}
                  </strong>

                  <span>
                    employees
                  </span>

                </div>

              </td>


              <!-- Status -->

              <td>

                <span
                  class="status-badge"
                  :class="{
                    empty:
                      employeeCount(dep.id) === 0
                  }"
                >

                  {{
                    employeeCount(dep.id) > 0
                      ? 'Active'
                      : 'No employees'
                  }}

                </span>

              </td>


              <!-- Actions -->

              <td>

                <div class="action-buttons">

                  <button
                    class="edit-button"
                    title="Edit department"
                    @click="editDepartment(dep)"
                  >
                    ✎
                  </button>

                  <button
                    class="delete-button"
                    title="Delete department"
                    @click="removeDepartment(dep)"
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
          ▦
        </div>

        <h3>

          {{
            search
              ? 'No departments found'
              : 'No departments yet'
          }}

        </h3>

        <p>

          {{
            search
              ? 'Try a different search term.'
              : 'Create your first department to get started.'
          }}

        </p>


        <button
          v-if="!search"
          class="add-button"
          @click="resetForm"
        >
          + Add Department
        </button>

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

.departments-page {

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

  background: #16a34a;

  color: white;

  font-size: 22px;

  box-shadow:
    0 8px 20px rgba(22,163,74,.18);

}


.eyebrow,
.section-label {

  display: block;

  font-size: 10px;

  font-weight: 750;

  letter-spacing: 1.3px;

  color: #16a34a;

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

  background: #16a34a;

  color: white;

  box-shadow:
    0 5px 15px rgba(22,163,74,.18);

}


.add-button:hover {

  background: #15803d;

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

.form-panel {

  background: white;

  border: 1px solid #e5e7eb;

  border-radius: 14px;

  margin-bottom: 22px;

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


.form-header h2 {

  margin: 0;

  font-size: 18px;

  color: #111827;

}


.editing-badge {

  padding: 6px 10px;

  background: #f0fdf4;

  color: #16a34a;

  border-radius: 20px;

  font-size: 10px;

  font-weight: 700;

}


.department-form {

  display: grid;

  grid-template-columns:
    minmax(250px, 1fr) auto;

  gap: 18px;

  align-items: end;

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


.form-group input {

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


.form-group input:focus {

  border-color: #16a34a;

  box-shadow:
    0 0 0 3px rgba(22,163,74,.08);

}


.form-actions {

  display: flex;

  gap: 9px;

}


.save-button,
.cancel-button {

  padding: 11px 18px;

  border-radius: 7px;

  font-size: 12px;

  font-weight: 650;

}


.save-button {

  background: #16a34a;

  color: white;

}


.save-button:hover {

  background: #15803d;

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
/* TABLE */
/* ================================= */

.table-panel {

  background: white;

  border: 1px solid #e5e7eb;

  border-radius: 14px;

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


.table-header h2 {

  margin: 0;

  font-size: 18px;

  color: #111827;

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

  min-width: 750px;

}


thead {

  background: #f8fafc;

}


th {

  padding: 13px 20px;

  text-align: left;

  font-size: 10px;

  text-transform: uppercase;

  letter-spacing: .7px;

  color: #6b7280;

  font-weight: 750;

}


td {

  padding: 15px 20px;

  border-top: 1px solid #f1f5f9;

  font-size: 12px;

}


tbody tr {

  transition: .15s;

}


tbody tr:hover {

  background: #fafcff;

}


/* ================================= */
/* DEPARTMENT CELL */
/* ================================= */

.department-cell {

  display: flex;

  align-items: center;

  gap: 12px;

}


.department-icon {

  width: 38px;
  height: 38px;

  display: flex;

  align-items: center;
  justify-content: center;

  border-radius: 9px;

  background: #f0fdf4;

  color: #16a34a;

  font-size: 17px;

}


.department-cell div:last-child {

  display: flex;

  flex-direction: column;

  gap: 3px;

}


.department-cell strong {

  color: #1f2937;

  font-size: 12px;

}


.department-cell span {

  color: #9ca3af;

  font-size: 10px;

}


.department-number {

  color: #9ca3af;

  font-size: 10px;

  font-weight: 700;

}


/* ================================= */
/* EMPLOYEE COUNT */
/* ================================= */

.employee-count {

  display: flex;

  align-items: baseline;

  gap: 5px;

}


.employee-count strong {

  font-size: 17px;

  color: #111827;

}


.employee-count span {

  color: #9ca3af;

  font-size: 10px;

}


/* ================================= */
/* STATUS */
/* ================================= */

.status-badge {

  display: inline-block;

  padding: 5px 9px;

  border-radius: 20px;

  background: #dcfce7;

  color: #15803d;

  font-size: 10px;

  font-weight: 700;

}


.status-badge.empty {

  background: #f3f4f6;

  color: #6b7280;

}


/* ================================= */
/* ACTIONS */
/* ================================= */

.actions-column {

  text-align: center;

}


.action-buttons {

  display: flex;

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

  background: #f0fdf4;

  color: #16a34a;

}


.edit-button:hover {

  background: #dcfce7;

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

  border-top-color: #16a34a;

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

@media (max-width: 700px) {

  .departments-page {

    padding: 20px 15px;

  }


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


  .department-form {

    grid-template-columns: 1fr;

  }


  .form-actions {

    width: 100%;

  }


  .form-actions button {

    flex: 1;

  }


  .table-header {

    align-items: flex-start;

    flex-direction: column;

  }


  .search-box {

    width: 100%;

  }

}

</style>
