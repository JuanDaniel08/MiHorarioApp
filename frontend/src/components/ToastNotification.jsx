import React from 'react';
import { CheckCircle2, AlertCircle } from 'lucide-react';

export default function ToastNotification({ title, description, type, onClose }) {
  const Icon = type === 'success' ? CheckCircle2 : AlertCircle;

  React.useEffect(() => {
    const timer = setTimeout(() => {
      onClose();
    }, 4000);
    return () => clearTimeout(timer);
  }, [onClose]);

  return (
    <div className={`toast toast-${type}`} style={{ opacity: 1, transform: 'translateY(0)', transition: 'all 0.3s ease' }}>
      <div className="toast-icon">
        <Icon className="toast-icon-svg" size={20} />
      </div>
      <div className="toast-message">
        <span className="toast-title">{title}</span>
        <span className="toast-description">{description}</span>
      </div>
    </div>
  );
}
