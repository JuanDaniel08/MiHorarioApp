import React from 'react';
import { CalendarRange, LayoutDashboard, Clock, PlusCircle, LogOut } from 'lucide-react';

export default function Sidebar({ currentUser, activeSection, onSectionChange, onLogout }) {
  const isAdmin = currentUser?.role === 'admin';

  const handleNavClick = (e, section) => {
    e.preventDefault();
    onSectionChange(section);
  };

  return (
    <aside className="sidebar">
      <div className="logo-area">
        <div className="logo-icon-container">
          <CalendarRange className="logo-icon" size={22} />
        </div>
        <h2>MiHorario</h2>
      </div>
      
      <nav className="nav-menu">
        <a 
          href="#" 
          className={`nav-item ${activeSection === 'dashboard' ? 'active' : ''}`}
          onClick={(e) => handleNavClick(e, 'dashboard')}
        >
          <LayoutDashboard size={20} />
          <span>Dashboard</span>
        </a>
        
        <a 
          href="#shifts-section" 
          className={`nav-item ${activeSection === 'shifts' ? 'active' : ''}`}
          onClick={(e) => handleNavClick(e, 'shifts')}
        >
          <Clock size={20} />
          <span>Turnos</span>
        </a>
        
        {isAdmin && (
          <a 
            href="#schedule-section" 
            id="nav-item-schedule" 
            className={`nav-item ${activeSection === 'schedule' ? 'active' : ''}`}
            onClick={(e) => handleNavClick(e, 'schedule')}
          >
            <PlusCircle size={20} />
            <span>Programar</span>
          </a>
        )}
        
        <a 
          href="#" 
          id="btn-logout" 
          className="nav-item nav-logout"
          onClick={(e) => {
            e.preventDefault();
            onLogout();
          }}
        >
          <LogOut size={20} />
          <span>Cerrar Sesión</span>
        </a>
      </nav>
      
      <div className="sidebar-footer">
        <p>Versión 1.0.0</p>
        <div className="status-indicator">
          <span className="dot pulse"></span>
          <span>Servidor Conectado</span>
        </div>
      </div>
    </aside>
  );
}
