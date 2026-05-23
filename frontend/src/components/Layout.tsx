import { NavLink, Outlet } from 'react-router-dom';

export default function Layout() {
  return (
    <div className="app-layout">
      <nav className="sidebar">
        <div className="sidebar-header">
          <h1>⛽ Moya</h1>
          <span className="subtitle">Gas Station Manager</span>
        </div>
        <ul className="nav-links">
          <li>
            <NavLink to="/" className={({ isActive }) => isActive ? 'active' : ''}>
              Dashboard
            </NavLink>
          </li>
          <li>
            <NavLink to="/tanks" className={({ isActive }) => isActive ? 'active' : ''}>
              Tanks
            </NavLink>
          </li>
          <li>
            <NavLink to="/pumps" className={({ isActive }) => isActive ? 'active' : ''}>
              Pumps
            </NavLink>
          </li>
          <li>
            <NavLink to="/products" className={({ isActive }) => isActive ? 'active' : ''}>
              Products
            </NavLink>
          </li>
        </ul>
      </nav>
      <main className="main-content">
        <Outlet />
      </main>
    </div>
  );
}
