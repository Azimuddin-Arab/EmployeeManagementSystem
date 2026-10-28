
import axios from 'axios'

const api = axios.create({
  baseURL: 'http://localhost:8080/api',
  headers: {
    'Content-Type': 'application/json'
  }
})

// =====================================
// DEPARTMENTS
// =====================================

// Get departments with search and pagination
export const getDepartments = (
  search = '',
  page = 0,
  size = 10
) => {
  return api.get('/departments', {
    params: {
      search,
      page,
      size
    }
  })
}

// Get one department
export const getDepartment = (id) => {
  return api.get(`/departments/${id}`)
}

// Create department
export const createDepartment = (department) => {
  return api.post('/departments', department)
}

// Update department
export const updateDepartment = (id, department) => {
  return api.put(`/departments/${id}`, department)
}

// Delete department
export const deleteDepartment = (id) => {
  return api.delete(`/departments/${id}`)
}

// =====================================
// EMPLOYEES
// =====================================

// Get employees with search and pagination
export const getEmployees = (
  search = '',
  page = 0,
  size = 10
) => {
  return api.get('/employees', {
    params: {
      search,
      page,
      size
    }
  })
}

// Get one employee
export const getEmployee = (id) => {
  return api.get(`/employees/${id}`)
}

// Create employee
export const createEmployee = (employee) => {
  return api.post('/employees', employee)
}

// Update employee
export const updateEmployee = (id, employee) => {
  return api.put(`/employees/${id}`, employee)
}

// Delete employee
export const deleteEmployee = (id) => {
  return api.delete(`/employees/${id}`)
}

export default api
