import React, { useState, useEffect, useCallback } from 'react';
import { ENDPOINTS, getAuthHeaders } from './config';
import Sidebar from './components/Sidebar';
import Header from './components/Header';
import StatsGrid from './components/StatsGrid';
import ShiftForm from './components/ShiftForm';
import ShiftTable from './components/ShiftTable';
import LoginScreen from './components/LoginScreen';
import CaptchaModal from './components/CaptchaModal';
import ToastNotification from './components/ToastNotification';

export default function App() {
  // Global State
  const [currentUser, setCurrentUser] = useState(() => {
    const saved = localStorage.getItem('mihorario_user');
    if (saved) {
      try {
        return JSON.parse(saved);
      } catch (e) {
        localStorage.removeItem('mihorario_user');
      }
    }
    return null;
  });

  const [pendingUser, setPendingUser] = useState(null);
  const [showCaptcha, setShowCaptcha] = useState(false);
  const [activeSection, setActiveSection] = useState('dashboard');
  const [toasts, setToasts] = useState([]);

  // Data State
  const [employees, setEmployees] = useState([]);
  const [labors, setLabors] = useState([]);
  const [shifts, setShifts] = useState([]);
  
  // Loading & Error States
  const [shiftsLoading, setShiftsLoading] = useState(false);
  const [shiftsError, setShiftsError] = useState(false);

  // Toast Helper
  const addToast = useCallback((title, description, type = 'success') => {
    const id = Date.now() + Math.random().toString(36).substring(2, 9);
    setToasts(prev => [...prev, { id, title, description, type }]);
  }, []);

  const removeToast = useCallback((id) => {
    setToasts(prev => prev.filter(t => t.id !== id));
  }, []);

  // Fetch Static API Data (Employees & Labors)
  const loadStaticData = useCallback(async () => {
    try {
      const [empRes, labRes] = await Promise.all([
        fetch(ENDPOINTS.employees, { headers: getAuthHeaders(currentUser) }).catch(() => null),
        fetch(ENDPOINTS.labors, { headers: getAuthHeaders(currentUser) }).catch(() => null)
      ]);

      if (empRes && empRes.ok) {
        const empData = await empRes.json();
        setEmployees(empData);
      }
      if (labRes && labRes.ok) {
        const labData = await labRes.json();
        setLabors(labData);
      }
    } catch (err) {
      console.error('Error preloading static data:', err);
    }
  }, [currentUser]);

  // Fetch shifts from API
  const loadShifts = useCallback(async () => {
    setShiftsLoading(true);
    setShiftsError(false);
    try {
      const res = await fetch(ENDPOINTS.shifts, { headers: getAuthHeaders(currentUser) });
      if (res.ok) {
        const data = await res.json();
        setShifts(data);
      } else {
        addToast('Error', 'No se pudieron cargar los turnos.', 'error');
        setShiftsError(true);
      }
    } catch (err) {
      console.error('Error fetching shifts:', err);
      addToast('Error de Red', 'No se pudieron cargar los turnos del servidor.', 'error');
      setShiftsError(true);
    } finally {
      setShiftsLoading(false);
    }
  }, [currentUser, addToast]);

  // Load static data on startup
  useEffect(() => {
    loadStaticData();
  }, [loadStaticData]);

  // Load shifts on login
  useEffect(() => {
    if (currentUser) {
      loadShifts();
    }
  }, [currentUser, loadShifts]);

  // Handle section scrolling
  const handleSectionChange = (section) => {
    setActiveSection(section);
    if (section === 'dashboard') {
      window.scrollTo({ top: 0, behavior: 'smooth' });
    } else if (section === 'shifts') {
      document.getElementById('shifts-section')?.scrollIntoView({ behavior: 'smooth' });
    } else if (section === 'schedule') {
      document.getElementById('schedule-section')?.scrollIntoView({ behavior: 'smooth' });
    }
  };

  // Auth Handlers
  const handlePendingLogin = (user) => {
    setPendingUser(user);
    setShowCaptcha(true);
  };

  const handleCaptchaSuccess = () => {
    setCurrentUser(pendingUser);
    localStorage.setItem('mihorario_user', JSON.stringify(pendingUser));
    localStorage.setItem('mihorario_captcha_token', 'google-recaptcha-v3-token-valid');
    
    setShowCaptcha(false);
    addToast('Acceso Concedido', `Bienvenido(a), ${pendingUser.name}.`, 'success');
    setPendingUser(null);
    setActiveSection('dashboard');
  };

  const handleLogout = () => {
    setCurrentUser(null);
    localStorage.removeItem('mihorario_user');
    localStorage.removeItem('mihorario_captcha_token');
    addToast('Sesión Cerrada', 'Has cerrado tu sesión correctamente.', 'success');
  };

  // Save Shift Handler
  const handleSaveShift = async (shiftData) => {
    try {
      const res = await fetch(ENDPOINTS.shifts, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          ...getAuthHeaders(currentUser, true)
        },
        body: JSON.stringify(shiftData)
      });

      if (res.ok || res.status === 201) {
        addToast('Turno Registrado', 'El turno ha sido guardado y programado con éxito.', 'success');
        loadShifts();
        return true;
      } else {
        const errorText = await res.text();
        addToast('Error al Guardar', errorText || 'El turno no se pudo guardar.', 'error');
        return false;
      }
    } catch (err) {
      console.error('Error saving shift:', err);
      addToast('Error de Conexión', 'No se pudo conectar con el servidor para registrar el turno.', 'error');
      return false;
    }
  };

  // Delete Shift Handler
  const handleDeleteShift = async (id) => {
    if (!window.confirm('¿Estás seguro de que deseas eliminar este turno?')) {
      return;
    }

    try {
      const res = await fetch(`${ENDPOINTS.shifts}/${id}`, {
        method: 'DELETE',
        headers: getAuthHeaders(currentUser)
      });

      if (res.ok) {
        addToast('Turno Eliminado', 'El turno ha sido eliminado del sistema con éxito.', 'success');
        loadShifts();
      } else {
        addToast('Error', 'No se pudo eliminar el turno.', 'error');
      }
    } catch (err) {
      console.error('Error deleting shift:', err);
      addToast('Error', 'Problema de conexión al eliminar el turno.', 'error');
    }
  };

  // Unauthenticated view
  if (!currentUser) {
    return (
      <>
        <LoginScreen 
          employees={employees} 
          onPendingLogin={handlePendingLogin} 
        />
        
        {showCaptcha && (
          <CaptchaModal 
            onClose={() => {
              setShowCaptcha(false);
              setPendingUser(null);
            }} 
            onSuccess={handleCaptchaSuccess} 
          />
        )}

        <div className="toast-container">
          {toasts.map(t => (
            <ToastNotification 
              key={t.id} 
              title={t.title} 
              description={t.description} 
              type={t.type} 
              onClose={() => removeToast(t.id)} 
            />
          ))}
        </div>
      </>
    );
  }

  // Authenticated view
  return (
    <div className="app-container">
      <Sidebar 
        currentUser={currentUser} 
        activeSection={activeSection}
        onSectionChange={handleSectionChange}
        onLogout={handleLogout}
      />
      
      <main className="main-content">
        <Header currentUser={currentUser} />
        
        <StatsGrid shifts={shifts} currentUser={currentUser} />

        <div className={`split-view ${currentUser.role === 'employee' ? 'full-width' : ''}`}>
          {currentUser.role === 'admin' && (
            <ShiftForm 
              employees={employees} 
              labors={labors} 
              onSave={handleSaveShift} 
            />
          )}
          
          <ShiftTable 
            shifts={shifts} 
            employees={employees} 
            labors={labors} 
            currentUser={currentUser} 
            onDelete={handleDeleteShift} 
            loading={shiftsLoading}
            error={shiftsError}
          />
        </div>
      </main>

      <div className="toast-container">
        {toasts.map(t => (
          <ToastNotification 
            key={t.id} 
            title={t.title} 
            description={t.description} 
            type={t.type} 
            onClose={() => removeToast(t.id)} 
          />
        ))}
      </div>
    </div>
  );
}
