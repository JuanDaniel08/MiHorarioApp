import React, { useState, useEffect, useCallback } from 'react';
import Keycloak from 'keycloak-js';
import { ENDPOINTS, getAuthHeaders, KEYCLOAK_CONFIG } from './config';
import Sidebar from './components/Sidebar';
import Header from './components/Header';
import StatsGrid from './components/StatsGrid';
import ShiftForm from './components/ShiftForm';
import ShiftTable from './components/ShiftTable';
import ToastNotification from './components/ToastNotification';

export default function App() {
  // Keycloak & Auth State
  const [keycloak, setKeycloak] = useState(null);
  const [token, setToken] = useState(null);
  const [currentUser, setCurrentUser] = useState(null);
  const [authLoading, setAuthLoading] = useState(true);

  // App Navigation & Toasts State
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

  // Initialize Keycloak
  useEffect(() => {
    const kc = new Keycloak(KEYCLOAK_CONFIG);
    
    kc.init({ 
      onLoad: 'login-required',
      checkLoginIframe: false 
    })
    .then(authenticated => {
      if (authenticated) {
        setKeycloak(kc);
        setToken(kc.token);
        
        // Extract roles from realm_access
        const roles = kc.tokenParsed?.realm_access?.roles || [];
        const isCoordinador = roles.includes('COORDINADOR');
        const role = isCoordinador ? 'admin' : 'employee';
        
        const userObj = {
          id: kc.tokenParsed?.sub, // Fallback until employee list is loaded
          role: role,
          name: kc.tokenParsed?.name || kc.tokenParsed?.preferred_username || 'Usuario',
          email: kc.tokenParsed?.email || ''
        };
        
        setCurrentUser(userObj);
        addToast('Sesión Iniciada', `Conectado como ${userObj.name}.`, 'success');

        // Set up token auto-refresh
        const interval = setInterval(() => {
          kc.updateToken(70).then(refreshed => {
            if (refreshed) {
              setToken(kc.token);
            }
          }).catch(err => {
            console.error('Failed to refresh Keycloak token:', err);
          });
        }, 60000);

        return () => clearInterval(interval);
      }
    })
    .catch(err => {
      console.error('Error during Keycloak initialization:', err);
      addToast('Error de Autenticación', 'No se pudo conectar con el servidor de autenticación Keycloak.', 'error');
    })
    .finally(() => {
      setAuthLoading(false);
    });
  }, [addToast]);

  // Fetch Static API Data (Employees & Labors)
  const loadStaticData = useCallback(async () => {
    if (!token) return;
    try {
      const [empRes, labRes] = await Promise.all([
        fetch(ENDPOINTS.employees, { headers: getAuthHeaders(token) }).catch(() => null),
        fetch(ENDPOINTS.labors, { headers: getAuthHeaders(token) }).catch(() => null)
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
  }, [token]);

  // Fetch shifts from API
  const loadShifts = useCallback(async () => {
    if (!token) return;
    setShiftsLoading(true);
    setShiftsError(false);
    try {
      const res = await fetch(ENDPOINTS.shifts, { headers: getAuthHeaders(token) });
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
  }, [token, addToast]);

  // Sync Logged User with DB Employee ID
  useEffect(() => {
    if (currentUser && employees.length > 0) {
      const matched = employees.find(e => e.email.toLowerCase() === currentUser.email.toLowerCase());
      if (matched && currentUser.id !== matched.id) {
        setCurrentUser(prev => ({
          ...prev,
          id: matched.id
        }));
      }
    }
  }, [employees, currentUser]);

  // Load static data on token acquisition
  useEffect(() => {
    if (token) {
      loadStaticData();
      loadShifts();
    }
  }, [token, loadStaticData, loadShifts]);

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

  const handleLogout = () => {
    if (keycloak) {
      keycloak.logout();
    }
  };

  // Save Shift Handler
  const handleSaveShift = async (shiftData) => {
    if (!token) return false;
    try {
      const res = await fetch(ENDPOINTS.shifts, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          ...getAuthHeaders(token, true)
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
    if (!token) return;
    if (!window.confirm('¿Estás seguro de que deseas eliminar este turno?')) {
      return;
    }

    try {
      const res = await fetch(`${ENDPOINTS.shifts}/${id}`, {
        method: 'DELETE',
        headers: getAuthHeaders(token)
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

  // Loading Splash Screen
  if (authLoading) {
    return (
      <div className="login-screen">
        <div className="loading-container" style={{ color: 'white' }}>
          <div className="spinner" style={{ width: '40px', height: '40px', borderWidth: '3px' }}></div>
          <span style={{ fontSize: '1.1rem', fontWeight: 500 }}>Cargando portal de seguridad...</span>
        </div>
      </div>
    );
  }

  if (!currentUser) return null;

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
