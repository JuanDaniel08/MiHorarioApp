import React, { useState } from 'react';
import { CalendarPlus, User, Briefcase, CalendarDays, Clock, ToggleLeft, MessageSquare, Save } from 'lucide-react';

export default function ShiftForm({ employees, labors, onSave }) {
  const getTodayDateString = () => {
    return new Date().toISOString().split('T')[0];
  };

  const [employeeId, setEmployeeId] = useState('');
  const [laborId, setLaborId] = useState('');
  const [date, setDate] = useState(getTodayDateString());
  const [startTime, setStartTime] = useState('');
  const [endTime, setEndTime] = useState('');
  const [active, setActive] = useState(true);
  const [observation, setObservation] = useState('');

  // Validation States
  const [errors, setErrors] = useState({
    employee: false,
    labor: false,
    date: false,
    startTime: false,
    endTime: false,
    range: false
  });

  const [isSubmitting, setIsSubmitting] = useState(false);

  const validateForm = () => {
    const newErrors = {
      employee: employeeId === '',
      labor: laborId === '',
      date: date === '',
      startTime: startTime === '',
      endTime: endTime === '',
      range: false
    };

    if (startTime && endTime) {
      if (startTime >= endTime) {
        newErrors.range = true;
      }
    }

    setErrors(newErrors);

    // Form is valid if no values in newErrors are true
    return !Object.values(newErrors).some(val => val === true);
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!validateForm()) return;

    // Format times from HH:MM to HH:MM:SS format
    const formatTimeToLocalTime = (timeStr) => {
      if (timeStr && timeStr.split(':').length === 2) {
        return `${timeStr}:00`;
      }
      return timeStr;
    };

    const shiftData = {
      employeeId,
      laborId,
      date,
      startTime: formatTimeToLocalTime(startTime),
      endTime: formatTimeToLocalTime(endTime),
      active,
      observation: observation.trim()
    };

    setIsSubmitting(true);
    const success = await onSave(shiftData);
    setIsSubmitting(false);

    if (success) {
      // Reset form
      setEmployeeId('');
      setLaborId('');
      setDate(getTodayDateString());
      setStartTime('');
      setEndTime('');
      setActive(true);
      setObservation('');
    }
  };

  return (
    <section id="schedule-section" className="card form-card">
      <div className="card-header">
        <CalendarPlus size={22} style={{ color: 'var(--primary)', marginRight: '8px' }} />
        <h2>Programar Nuevo Turno</h2>
      </div>
      
      <form id="shift-form" onSubmit={handleSubmit} noValidate>
        {/* Employee Select */}
        <div className={`form-group ${errors.employee ? 'invalid' : ''}`}>
          <label htmlFor="employee-select">
            <User size={14} style={{ marginRight: '6px' }} /> Empleado
          </label>
          <select 
            id="employee-select" 
            value={employeeId}
            onChange={(e) => setEmployeeId(e.target.value)}
            required
          >
            <option value="" disabled>Selecciona un empleado...</option>
            {employees.map(emp => (
              <option key={emp.id} value={emp.id}>
                {emp.name} {emp.lastName} ({emp.identification})
              </option>
            ))}
          </select>
          {errors.employee && (
            <span className="error-msg" id="employee-error" style={{ display: 'block' }}>
              El empleado es obligatorio.
            </span>
          )}
        </div>

        {/* Labor Select */}
        <div className={`form-group ${errors.labor ? 'invalid' : ''}`}>
          <label htmlFor="labor-select">
            <Briefcase size={14} style={{ marginRight: '6px' }} /> Labor / Tarea
          </label>
          <select 
            id="labor-select" 
            value={laborId}
            onChange={(e) => setLaborId(e.target.value)}
            required
          >
            <option value="" disabled>Selecciona una labor...</option>
            {labors.map(lab => (
              <option key={lab.id} value={lab.id}>
                {lab.name} - {lab.description}
              </option>
            ))}
          </select>
          {errors.labor && (
            <span className="error-msg" id="labor-error" style={{ display: 'block' }}>
              La labor es obligatoria.
            </span>
          )}
        </div>

        {/* Date */}
        <div className={`form-group ${errors.date ? 'invalid' : ''}`}>
          <label htmlFor="shift-date">
            <CalendarDays size={14} style={{ marginRight: '6px' }} /> Fecha del Turno
          </label>
          <input 
            type="date" 
            id="shift-date" 
            value={date}
            onChange={(e) => setDate(e.target.value)}
            required
          />
          {errors.date && (
            <span className="error-msg" id="date-error" style={{ display: 'block' }}>
              Selecciona una fecha válida.
            </span>
          )}
        </div>

        {/* Time Range */}
        <div className="time-range-group">
          <div className={`form-group ${errors.startTime ? 'invalid' : ''}`}>
            <label htmlFor="start-time">
              <Clock size={14} style={{ marginRight: '6px' }} /> Hora Inicio
            </label>
            <input 
              type="time" 
              id="start-time" 
              value={startTime}
              onChange={(e) => setStartTime(e.target.value)}
              required
            />
            {errors.startTime && (
              <span className="error-msg" id="start-time-error" style={{ display: 'block' }}>
                Requerido.
              </span>
            )}
          </div>
          <div className={`form-group ${errors.endTime ? 'invalid' : ''}`}>
            <label htmlFor="end-time">
              <Clock size={14} style={{ marginRight: '6px' }} /> Hora Fin
            </label>
            <input 
              type="time" 
              id="end-time" 
              value={endTime}
              onChange={(e) => setEndTime(e.target.value)}
              required
            />
            {errors.endTime && (
              <span className="error-msg" id="end-time-error" style={{ display: 'block' }}>
                Requerido.
              </span>
            )}
          </div>
        </div>
        
        {errors.range && (
          <span className="error-msg" id="range-error" style={{ display: 'block' }}>
            La hora de inicio debe ser anterior a la hora de fin.
          </span>
        )}

        {/* State Toggle */}
        <div className="form-group toggle-group">
          <label htmlFor="shift-active" className="toggle-label">
            <span className="label-text">
              <ToggleLeft size={14} style={{ marginRight: '6px' }} /> Turno Activo
            </span>
            <input 
              type="checkbox" 
              id="shift-active" 
              checked={active}
              onChange={(e) => setActive(e.target.checked)}
            />
            <span className="toggle-slider"></span>
          </label>
        </div>

        {/* Observations */}
        <div className="form-group">
          <label htmlFor="shift-observation">
            <MessageSquare size={14} style={{ marginRight: '6px' }} /> Observaciones (Opcional)
          </label>
          <textarea 
            id="shift-observation" 
            rows="3" 
            placeholder="Ej. Llevar implementos de seguridad..."
            value={observation}
            onChange={(e) => setObservation(e.target.value)}
          ></textarea>
        </div>

        {/* Submit Button */}
        <button type="submit" id="btn-submit" className="btn btn-primary" disabled={isSubmitting}>
          {isSubmitting ? (
            <>
              <div className="spinner" style={{ width: '16px', height: '16px', borderWidth: '2px', marginRight: '6px' }}></div>
              Guardando...
            </>
          ) : (
            <>
              <Save size={18} style={{ marginRight: '6px' }} /> Guardar Turno
            </>
          )}
        </button>
      </form>
    </section>
  );
}
