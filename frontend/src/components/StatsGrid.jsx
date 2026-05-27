import React from 'react';
import { Calendar, CheckCircle2, XCircle } from 'lucide-react';

export default function StatsGrid({ shifts, currentUser }) {
  const isEmployee = currentUser?.role === 'employee';
  
  const totalShifts = shifts.length;
  const activeShifts = shifts.filter(s => s.active).length;
  const inactiveShifts = totalShifts - activeShifts;

  const prefix = isEmployee ? 'Mis Turnos' : 'Turnos';

  return (
    <section className="stats-grid">
      <div className="stat-card">
        <div className="stat-icon-wrapper blue">
          <Calendar size={24} />
        </div>
        <div className="stat-data">
          <span className="stat-label">{prefix} Totales</span>
          <h3 id="stat-total">{totalShifts}</h3>
        </div>
      </div>
      
      <div className="stat-card">
        <div className="stat-icon-wrapper green">
          <CheckCircle2 size={24} />
        </div>
        <div className="stat-data">
          <span className="stat-label">{prefix} Activos</span>
          <h3 id="stat-active">{activeShifts}</h3>
        </div>
      </div>
      
      <div className="stat-card">
        <div className="stat-icon-wrapper orange">
          <XCircle size={24} />
        </div>
        <div className="stat-data">
          <span className="stat-label">{prefix} Inactivos</span>
          <h3 id="stat-inactive">{inactiveShifts}</h3>
        </div>
      </div>
    </section>
  );
}
