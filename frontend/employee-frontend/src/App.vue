
<script setup>
import { ref } from 'vue'

const sidebarOpen = ref(false)

function closeSidebar() {
  sidebarOpen.value = false
}
</script>


<template>

  <div class="app-layout">

    <!-- ================================= -->
    <!-- MOBILE OVERLAY -->
    <!-- ================================= -->

    <div
      v-if="sidebarOpen"
      class="sidebar-overlay"
      @click="closeSidebar"
    ></div>


    <!-- ================================= -->
    <!-- SIDEBAR -->
    <!-- ================================= -->

    <aside
      class="sidebar"
      :class="{ 'sidebar-open': sidebarOpen }"
    >

      <!-- Logo -->

      <div class="brand">

        <div class="brand-icon">
          ES
        </div>

        <div class="brand-text">

          <strong>
            Employee
          </strong>

          <span>
            Management System
          </span>

        </div>

      </div>


      <!-- Navigation -->

      <nav class="navigation">

        <div class="nav-section-title">
          MAIN MENU
        </div>


        <RouterLink
          to="/"
          class="nav-item"
          @click="closeSidebar"
        >

          <span class="nav-icon">
            <svg viewBox="0 0 24 24">
              <path d="M3 13h8V3H3v10zm0 8h8v-6H3v6zm10 0h8V11h-8v10zm0-18v6h8V3h-8z"/>
            </svg>
          </span>

          <span>
            Dashboard
          </span>

        </RouterLink>


        <RouterLink
          to="/employees"
          class="nav-item"
          @click="closeSidebar"
        >

          <span class="nav-icon">
            <svg viewBox="0 0 24 24">
              <path d="M16 11c1.66 0 2.99-1.34 2.99-3S17.66 5 16 5s-3 1.34-3 3 1.34 3 3 3zm-8 0c1.66 0 2.99-1.34 2.99-3S9.66 5 8 5 5 6.34 5 8s1.34 3 3 3zm0 2c-2.33 0-7 1.17-7 3.5V19h14v-2.5C15 14.17 10.33 13 8 13zm8 0c-.29 0-.62.02-.97.05 1.16.84 1.97 1.9 1.97 3.45V19h5v-2.5c0-2.33-3.67-3.5-6-3.5z"/>
            </svg>
          </span>

          <span>
            Employees
          </span>

        </RouterLink>


        <RouterLink
          to="/departments"
          class="nav-item"
          @click="closeSidebar"
        >

          <span class="nav-icon">
            <svg viewBox="0 0 24 24">
              <path d="M10 4H2c-1.1 0-2 .9-2 2v12c0 1.1.9 2 2 2h20c1.1 0 2-.9 2-2V8c0-1.1-.9-2-2-2h-10l-2-2z"/>
            </svg>
          </span>

          <span>
            Departments
          </span>

        </RouterLink>


        <div class="nav-section-title second-section">
          SYSTEM
        </div>


        <a
          href="#"
          class="nav-item"
          @click.prevent
        >

          <span class="nav-icon">
            <svg viewBox="0 0 24 24">
              <path d="M19.43 12.98c.04-.32.07-.65.07-.98s-.02-.66-.07-.98l2.11-1.65c.19-.15.24-.42.12-.64l-2-3.46c-.12-.22-.37-.31-.6-.22l-2.49 1c-.52-.4-1.08-.73-1.69-.98L14.5 2.42C14.47 2.18 14.25 2 14 2h-4c-.25 0-.46.18-.5.42L9.12 5.07c-.61.25-1.17.59-1.69.98l-2.49-1c-.23-.08-.48 0-.6.22l-2 3.46c-.13.22-.07.49.12.64l2.11 1.65c-.04.32-.08.65-.08.98s.03.66.08.98l-2.11 1.65c-.19.15-.24.42-.12.64l2 3.46c.12.22.37.31.6.22l2.49-1c.52.4 1.08.73 1.69.98l.38 2.65c.04.24.25.42.5.42h4c.25 0 .46-.18.5-.42l.38-2.65c.61-.25 1.17-.58 1.69-.98l2.49 1c.23.08.48 0 .6-.22l2-3.46c.12-.22.07-.49-.12-.64l-2.11-1.65zM12 15.5A3.5 3.5 0 1 1 12 8a3.5 3.5 0 0 1 0 7.5z"/>
            </svg>
          </span>

          <span>
            Settings
          </span>

        </a>

      </nav>


      <!-- Sidebar Footer -->

      <div class="sidebar-footer">

        <div class="system-status">

          <span class="status-dot"></span>

          <div>

            <strong>
              System Online
            </strong>

            <span>
              API Connected
            </span>

          </div>

        </div>

      </div>

    </aside>


    <!-- ================================= -->
    <!-- MAIN AREA -->
    <!-- ================================= -->

    <div class="main-area">


      <!-- ================================= -->
      <!-- TOPBAR -->
      <!-- ================================= -->

      <header class="topbar">

        <!-- Mobile Menu -->

        <button
          class="mobile-menu-button"
          @click="sidebarOpen = !sidebarOpen"
        >

          <svg viewBox="0 0 24 24">
            <path d="M3 6h18M3 12h18M3 18h18"/>
          </svg>

        </button>


        <!-- Breadcrumb / Page Context -->

        <div class="topbar-title">

          <span>
            Employee Management
          </span>

        </div>


        <!-- Right Side -->

        <div class="topbar-right">

          <button class="notification-button">

            <svg viewBox="0 0 24 24">
              <path d="M12 22a2.5 2.5 0 0 0 2.45-2h-4.9A2.5 2.5 0 0 0 12 22zm6-6v-5a6 6 0 0 0-5-5.92V4a1 1 0 0 0-2 0v1.08A6 6 0 0 0 6 11v5l-2 2v1h16v-1l-2-2z"/>
            </svg>

            <span class="notification-dot"></span>

          </button>


          <div class="topbar-divider"></div>


          <!-- User -->

          <div class="user-profile">

            <div class="user-avatar">
              A
            </div>

            <div class="user-info">

              <strong>
                Administrator
              </strong>

              <span>
                System Admin
              </span>

            </div>

          </div>

        </div>

      </header>


      <!-- ================================= -->
      <!-- PAGE CONTENT -->
      <!-- ================================= -->

      <main class="main-content">

        <RouterView />

      </main>


      <!-- ================================= -->
      <!-- FOOTER -->
      <!-- ================================= -->

      <footer class="app-footer">

        <span>
          © 2026 Employee Management System
        </span>

        <span>
          Spring Boot + Vue.js
        </span>

      </footer>

    </div>

  </div>

</template>


<style scoped>

/* ================================= */
/* GLOBAL */
/* ================================= */

* {
  box-sizing: border-box;
}


.app-layout {

  min-height: 100vh;

  display: flex;

  background: #f5f7fb;

  color: #111827;

}


/* ================================= */
/* SIDEBAR */
/* ================================= */

.sidebar {

  width: 250px;

  flex-shrink: 0;

  display: flex;

  flex-direction: column;

  background: #111827;

  color: white;

  min-height: 100vh;

  position: sticky;

  top: 0;

  z-index: 100;

}


/* ================================= */
/* BRAND */
/* ================================= */

.brand {

  height: 76px;

  display: flex;

  align-items: center;

  gap: 12px;

  padding: 0 22px;

  border-bottom: 1px solid
    rgba(255,255,255,.07);

}


.brand-icon {

  width: 39px;
  height: 39px;

  display: flex;

  align-items: center;
  justify-content: center;

  border-radius: 10px;

  background: #2563eb;

  color: white;

  font-size: 12px;

  font-weight: 800;

  letter-spacing: .5px;

}


.brand-text {

  display: flex;

  flex-direction: column;

  gap: 2px;

}


.brand-text strong {

  font-size: 14px;

  font-weight: 700;

}


.brand-text span {

  color: #9ca3af;

  font-size: 9px;

  letter-spacing: .4px;

}


/* ================================= */
/* NAVIGATION */
/* ================================= */

.navigation {

  flex: 1;

  padding: 25px 14px;

}


.nav-section-title {

  padding: 0 12px;

  margin-bottom: 9px;

  color: #6b7280;

  font-size: 9px;

  font-weight: 750;

  letter-spacing: 1.2px;

}


.second-section {

  margin-top: 28px;

}


.nav-item {

  height: 44px;

  display: flex;

  align-items: center;

  gap: 12px;

  padding: 0 12px;

  margin-bottom: 4px;

  border-radius: 8px;

  color: #9ca3af;

  text-decoration: none;

  font-size: 12px;

  font-weight: 550;

  transition: all .18s ease;

}


.nav-item:hover {

  background: rgba(255,255,255,.06);

  color: white;

}


.nav-item.router-link-active {

  background: #2563eb;

  color: white;

  box-shadow:
    0 5px 15px rgba(37,99,235,.2);

}


.nav-icon {

  width: 20px;
  height: 20px;

  display: flex;

  align-items: center;
  justify-content: center;

  flex-shrink: 0;

}


.nav-icon svg {

  width: 17px;
  height: 17px;

  fill: currentColor;

}


/* ================================= */
/* SIDEBAR FOOTER */
/* ================================= */

.sidebar-footer {

  padding: 18px 15px;

  border-top: 1px solid
    rgba(255,255,255,.07);

}


.system-status {

  display: flex;

  align-items: center;

  gap: 10px;

  padding: 11px;

  border-radius: 8px;

  background: rgba(255,255,255,.04);

}


.status-dot {

  width: 8px;
  height: 8px;

  flex-shrink: 0;

  border-radius: 50%;

  background: #22c55e;

  box-shadow:
    0 0 0 4px rgba(34,197,94,.1);

}


.system-status div {

  display: flex;

  flex-direction: column;

  gap: 2px;

}


.system-status strong {

  color: #d1d5db;

  font-size: 10px;

}


.system-status span {

  color: #6b7280;

  font-size: 9px;

}


/* ================================= */
/* MAIN AREA */
/* ================================= */

.main-area {

  flex: 1;

  min-width: 0;

  display: flex;

  flex-direction: column;

  min-height: 100vh;

}


/* ================================= */
/* TOPBAR */
/* ================================= */

.topbar {

  height: 76px;

  flex-shrink: 0;

  display: flex;

  align-items: center;

  padding: 0 30px;

  background: white;

  border-bottom: 1px solid #e5e7eb;

}


.topbar-title {

  color: #6b7280;

  font-size: 12px;

}


.topbar-right {

  margin-left: auto;

  display: flex;

  align-items: center;

  gap: 17px;

}


.notification-button {

  position: relative;

  width: 34px;
  height: 34px;

  display: flex;

  align-items: center;
  justify-content: center;

  background: transparent;

  border: 0;

  cursor: pointer;

  color: #6b7280;

}


.notification-button svg {

  width: 18px;
  height: 18px;

  fill: currentColor;

}


.notification-button:hover {

  color: #2563eb;

}


.notification-dot {

  position: absolute;

  width: 6px;
  height: 6px;

  top: 7px;
  right: 7px;

  background: #ef4444;

  border: 1.5px solid white;

  border-radius: 50%;

}


.topbar-divider {

  height: 30px;

  width: 1px;

  background: #e5e7eb;

}


/* ================================= */
/* USER */
/* ================================= */

.user-profile {

  display: flex;

  align-items: center;

  gap: 10px;

}


.user-avatar {

  width: 35px;
  height: 35px;

  display: flex;

  align-items: center;
  justify-content: center;

  border-radius: 50%;

  background: #eff6ff;

  color: #2563eb;

  font-size: 12px;

  font-weight: 750;

}


.user-info {

  display: flex;

  flex-direction: column;

  gap: 2px;

}


.user-info strong {

  font-size: 11px;

  color: #374151;

}


.user-info span {

  font-size: 9px;

  color: #9ca3af;

}


/* ================================= */
/* CONTENT */
/* ================================= */

.main-content {

  flex: 1;

  min-width: 0;

}


/* ================================= */
/* FOOTER */
/* ================================= */

.app-footer {

  min-height: 48px;

  display: flex;

  align-items: center;

  justify-content: space-between;

  padding: 0 30px;

  background: white;

  border-top: 1px solid #e5e7eb;

  color: #9ca3af;

  font-size: 9px;

}


/* ================================= */
/* MOBILE BUTTON */
/* ================================= */

.mobile-menu-button {

  display: none;

  width: 38px;
  height: 38px;

  align-items: center;
  justify-content: center;

  margin-right: 12px;

  border: 1px solid #e5e7eb;

  border-radius: 8px;

  background: white;

  color: #374151;

  cursor: pointer;

}


.mobile-menu-button svg {

  width: 19px;
  height: 19px;

  fill: none;

  stroke: currentColor;

  stroke-width: 2;

  stroke-linecap: round;

}


/* ================================= */
/* OVERLAY */
/* ================================= */

.sidebar-overlay {

  display: none;

}


/* ================================= */
/* TABLET */
/* ================================= */

@media (max-width: 900px) {

  .sidebar {

    width: 220px;

  }

  .topbar {

    padding: 0 20px;

  }

}


/* ================================= */
/* MOBILE */
/* ================================= */

@media (max-width: 700px) {

  .sidebar {

    position: fixed;

    left: 0;

    top: 0;

    bottom: 0;

    width: 250px;

    transform: translateX(-100%);

    transition:
      transform .25s ease;

    box-shadow:
      10px 0 30px rgba(0,0,0,.15);

  }


  .sidebar.sidebar-open {

    transform: translateX(0);

  }


  .sidebar-overlay {

    display: block;

    position: fixed;

    inset: 0;

    z-index: 90;

    background: rgba(0,0,0,.45);

  }


  .mobile-menu-button {

    display: flex;

  }


  .topbar {

    height: 65px;

    padding: 0 15px;

  }


  .topbar-title {

    font-size: 11px;

  }


  .user-info {

    display: none;

  }


  .topbar-divider {

    display: none;

  }


  .app-footer {

    padding: 12px 15px;

    flex-direction: column;

    justify-content: center;

    gap: 4px;

  }

}


/* ================================= */
/* SMALL MOBILE */
/* ================================= */

@media (max-width: 420px) {

  .notification-button {

    display: none;

  }

  .topbar-title {

    display: none;

  }

}

</style>
