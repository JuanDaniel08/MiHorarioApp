import React, { useState } from 'react';
import { ClipboardList, Search, Trash2, CalendarX, AlertTriangle } from 'lucide-react';

// Format date for display (e.g. 24 de May, 2026)
function formatDate(dateString) {
  if (!dateString) return '';
  try {
    const [year, month, day] = dateString.split('-');
    const date = new Date(year, month - 1, day);
    return date.toLocaleDateString('es-ES', {
      day: 'numeric',
      month: 'short',
      year: 'numeric'
    });
  } catch {
    return dateString;
  }
}

export default function ShiftTable({ shifts, employees, labors, currentUser, onDelete, loading, error }) {
  const [searchQuery, setSearchQuery] = useState('');
  const isAdmin = currentUser?.role === 'admin';

  // 1. Filter by employee if user is 'employee'
  let filteredShifts = [...shifts];
  if (currentUser?.role === 'employee') {
    filteredShifts = shifts.filter(s => s.employeeId === currentUser.id);
  }

  // 2. Filter by search query (employee name)
  if (searchQuery.trim() !== '') {
    const query = searchQuery.toLowerCase().trim();
    filteredShifts = filteredShifts.filter(shift => {
      const emp = employees.find(e => e.id === shift.employeeId);
      const empName = emp ? `${emp.name} ${emp.lastName}`.toLowerCase() : '';
      return empName.includes(query);
    });
  }

  const columnsCount = isAdmin ? 7 : 6;

  return (
    <section id="shifts-section" className="card list-card">
      <div className="card-header search-header">
        <div className="title-with-icon">
          <ClipboardList size={22} style={{ color: 'var(--primary)' }} />
          <h2>Listado de Turnos</h2>
        </div>
        <div className="search-box">
          <Search size={18} />
          <input 
            type="text" 
            placeholder="Buscar por empleado..."
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
          />
        </div>
      </div>

      <div className="table-container">
        <table>
          <thead>
            <tr>
              <th>Empleado</th>
              <th>Labor</th>
              <th>Fecha</th>
              <th>Horario</th>
              <th>Estado</th>
              <th>Observaciones</th>
              {isAdmin && <th className="actions-header" id="th-actions">Acción</th>}
            </tr>
          </thead>
          <tbody>
            {loading ? (
              <tr className="loading-row">
                <td colSpan={columnsCount}>
                  <div className="loading-container">
                    <div className="spinner"></div>
                    <span>Cargando turnos...</span>
                  </div>
                </td>
              </tr>
            ) : error ? (
              <tr className="empty-row">
                <td colSpan={columnsCount}>
                  <div className="empty-container">
                    <AlertTriangle size={40} style={{ color: 'var(--danger)' }} />
                    <span>Error al cargar datos. Verifica la conexión con el backend.</span>
                  </div>
                </td>
              </tr>
            ) : filteredShifts.length === 0 ? (
              <tr className="empty-row">
                <td colSpan={columnsCount}>
                  <div className="empty-container">
                    <CalendarX size={40} />
                    <span>No hay turnos registrados en este momento.</span>
                  </div>
                </td>
              </tr>
            ) : (
              filteredShifts.map(shift => {
                // Find Names from IDs
                const emp = employees.find(e => e.id === shift.employeeId);
                const empName = emp ? `${emp.name} ${emp.lastName}` : `ID: ${shift.employeeId.substring(0, 8)}...`;
                
                const lab = labors.find(l => l.id === shift.laborId);
                const labName = lab ? lab.name : `ID: ${shift.laborId.substring(0, 8)}...`;

                const timeStart = shift.startTime.substring(0, 5);
                const timeEnd = shift.endTime.substring(0, 5);

                return (
                  <tr key={shift.id}>
                    <td style={{ fontWeight: 600 }}>{empName}</td>
                    <td>{labName}</td>
                    <td>{formatDate(shift.date)}</td>
                    <td style={{ fontFamily: 'monospace', fontWeight: 500 }}>{timeStart} - {timeEnd}</td>
                    <td>
                      {shift.active ? (
                        <span className="badge badge-active">Activo</span>
                      ) : (
                        <span className="badge badge-inactive">Inactivo</span>
                      )}
                    </td>
                    <td className="observation-cell" title={shift.observation || 'Ninguna'}>
                      {shift.observation || '—'}
                    </td>
                    {isAdmin && (
                      <td>
                        <div className="actions-cell">
                          <button 
                            className="btn-delete" 
                            onClick={() => onDelete(shift.id)}
                            title="Eliminar Turno"
                          >
                            <Trash2 size={16} />
                          </button>
                        </div>
                      </td>
                    )}
                  </tr>
                );
              })
            )}
          </tbody>
        </table>
      </div>
    </section>
  );
}
