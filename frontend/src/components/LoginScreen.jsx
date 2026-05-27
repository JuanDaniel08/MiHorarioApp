import React, { useState } from 'react';
import { CalendarRange, User, KeyRound, LogIn } from 'lucide-react';

export default function LoginScreen({ employees, onPendingLogin }) {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [usernameError, setUsernameError] = useState(false);
  const [passwordError, setPasswordError] = useState(false);
  const [credentialsError, setCredentialsError] = useState(false);

  const handleSubmit = (e) => {
    e.preventDefault();
    setCredentialsError(false);

    const usernameVal = username.trim().toLowerCase();
    const passwordVal = password.trim();

    let isValid = true;
    if (!usernameVal) {
      setUsernameError(true);
      isValid = false;
    } else {
      setUsernameError(false);
    }

    if (!passwordVal) {
      setPasswordError(true);
      isValid = false;
    } else {
      setPasswordError(false);
    }

    if (!isValid) return;

    // Admin Auth Mock Check
    if (usernameVal === 'admin' && passwordVal === 'admin123') {
      const adminUser = {
        role: 'admin',
        name: 'Administrador Sistema',
        email: 'admin@mihorario.com'
      };
      onPendingLogin(adminUser);
      return;
    }

    // Employee Auth Mock Check
    const matchedEmployee = employees.find(
      e => e.email.toLowerCase() === usernameVal || e.identification === usernameVal
    );

    if (matchedEmployee) {
      const empUser = {
        id: matchedEmployee.id,
        role: 'employee',
        name: `${matchedEmployee.name} ${matchedEmployee.lastName}`,
        email: matchedEmployee.email
      };
      onPendingLogin(empUser);
    } else {
      setCredentialsError(true);
    }
  };

  return (
    <div id="login-screen" className="login-screen">
      <div className="login-container">
        <div className="login-header">
          <div className="login-logo-container">
            <CalendarRange className="login-logo-icon" size={24} />
          </div>
          <h2>MiHorario</h2>
          <p>Ingresa tus credenciales para acceder al sistema</p>
        </div>
        
        <form id="login-form" onSubmit={handleSubmit} noValidate>
          <div className={`form-group ${usernameError ? 'invalid' : ''}`}>
            <label htmlFor="login-username">
              <User size={14} style={{ marginRight: '6px', verticalAlign: 'middle' }} /> Usuario o Correo
            </label>
            <input 
              type="text" 
              id="login-username" 
              placeholder="admin o correo@uco.edu.co" 
              value={username}
              onChange={(e) => setUsername(e.target.value)}
              required 
            />
            {usernameError && (
              <span className="error-msg" id="login-username-error" style={{ display: 'block' }}>
                Ingresa tu usuario o correo.
              </span>
            )}
          </div>

          <div className={`form-group ${passwordError ? 'invalid' : ''}`}>
            <label htmlFor="login-password">
              <KeyRound size={14} style={{ marginRight: '6px', verticalAlign: 'middle' }} /> Contraseña
            </label>
            <input 
              type="password" 
              id="login-password" 
              placeholder="••••••••" 
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              required 
            />
            {passwordError && (
              <span className="error-msg" id="login-password-error" style={{ display: 'block' }}>
                Ingresa tu contraseña.
              </span>
            )}
          </div>
          
          {credentialsError && (
            <span className="error-msg" id="login-credentials-error" style={{ display: 'block', textAlign: 'center', backgroundColor: 'var(--danger-light)', padding: '8px', borderRadius: 'var(--radius-sm)' }}>
              Credenciales incorrectas. Inténtalo de nuevo.
            </span>
          )}

          <button type="submit" id="btn-login-submit" className="btn btn-primary btn-block">
            <LogIn size={18} style={{ marginRight: '6px', verticalAlign: 'middle' }} /> Ingresar
          </button>
        </form>
        
        <div className="login-help">
          <p><strong>Administrador:</strong> admin / admin123</p>
          <p><strong>Empleado:</strong> juan.dan@uco.edu.co o 10203040</p>
        </div>
      </div>
    </div>
  );
}
