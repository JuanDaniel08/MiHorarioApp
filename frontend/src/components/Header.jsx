import React from 'react';

function getInitials(name) {
  if (!name) return '??';
  const parts = name.trim().split(/\s+/);
  if (parts.length >= 2) {
    return (parts[0][0] + parts[1][0]).toUpperCase();
  }
  return name.substring(0, 2).toUpperCase();
}

export default function Header({ currentUser }) {
  const isEmployee = currentUser?.role === 'employee';

  return (
    <header className="top-header">
      <div className="header-title">
        <h1>{isEmployee ? 'Mi Panel de Turnos' : 'Planificación de Turnos'}</h1>
        <p>
          {isEmployee 
            ? 'Visualiza tus turnos de trabajo asignados y programados' 
            : 'Gestiona y programa los turnos de trabajo de tus empleados'}
        </p>
      </div>
      
      <div className="user-profile">
        <div className="avatar" id="user-avatar">
          {getInitials(currentUser?.name)}
        </div>
        <div className="profile-info">
          <span className="profile-name" id="user-name">
            {currentUser?.name || 'Usuario'}
          </span>
          <span className="profile-role" id="user-role">
            {currentUser?.role === 'admin' ? 'Administrador' : 'Empleado'}
          </span>
        </div>
      </div>
    </header>
  );
}
