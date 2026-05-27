import React, { useState, useEffect, useRef } from 'react';
import { ShieldCheck, ChevronsRight } from 'lucide-react';

export default function CaptchaModal({ onClose, onSuccess }) {
  const [captchaSolved, setCaptchaSolved] = useState(false);
  const [errorMsg, setErrorMsg] = useState(false);
  const [targetX, setTargetX] = useState(150);
  const [thumbLeft, setThumbLeft] = useState(0);
  const [pieceLeft, setPieceLeft] = useState(10);
  const [guideTextOpacity, setGuideTextOpacity] = useState(1);
  const [isDragging, setIsDragging] = useState(false);
  const [transitionEnabled, setTransitionEnabled] = useState(false);

  const dragStartX = useRef(0);
  const bgRef = useRef(null);
  const sliderBarRef = useRef(null);
  const thumbRef = useRef(null);

  // Initialize captcha slot target position randomly
  useEffect(() => {
    const timer = setTimeout(() => {
      const bgWidth = bgRef.current ? bgRef.current.offsetWidth : 280;
      const maxTarget = bgWidth > 0 ? (bgWidth - 70) : 210;
      const target = Math.floor(Math.random() * (maxTarget - 100)) + 100;
      setTargetX(target);
    }, 50);
    return () => clearTimeout(timer);
  }, []);

  const handleStart = (e) => {
    if (captchaSolved) return;
    setIsDragging(true);
    setTransitionEnabled(false);
    const clientX = e.clientX || (e.touches && e.touches[0].clientX);
    dragStartX.current = clientX;
  };

  const handleMove = (e) => {
    if (!isDragging || captchaSolved) return;
    const clientX = e.clientX || (e.touches && e.touches[0].clientX);
    if (clientX === undefined) return;

    const deltaX = clientX - dragStartX.current;
    const sliderWidth = sliderBarRef.current ? sliderBarRef.current.offsetWidth : 280;
    const thumbWidth = thumbRef.current ? thumbRef.current.offsetWidth : 44;
    const maxSlide = sliderWidth - thumbWidth;

    const newLeft = Math.max(0, Math.min(deltaX, maxSlide));
    setThumbLeft(newLeft);

    const bgWidth = bgRef.current ? bgRef.current.offsetWidth : 280;
    const maxPieceSlide = bgWidth - 50 - 10; // puzzle piece width is roughly 50px
    const pLeft = 10 + (newLeft / maxSlide) * maxPieceSlide;
    setPieceLeft(pLeft);

    setGuideTextOpacity(Math.max(0, 1 - (newLeft / 60)));
  };

  const handleEnd = () => {
    if (!isDragging || captchaSolved) return;
    setIsDragging(false);

    const tolerance = 6;
    const diff = Math.abs(pieceLeft - targetX);

    if (diff <= tolerance) {
      setCaptchaSolved(true);
      setErrorMsg(false);
      
      setTimeout(() => {
        onSuccess();
      }, 600);
    } else {
      setErrorMsg(true);
      setTransitionEnabled(true);
      setThumbLeft(0);
      setPieceLeft(10);
      setGuideTextOpacity(1);
      
      // Clear transitions after they finish
      setTimeout(() => {
        setTransitionEnabled(false);
      }, 300);
    }
  };

  // Add global touch/mouse event listeners while dragging
  useEffect(() => {
    if (isDragging) {
      const onMove = (e) => handleMove(e);
      const onEnd = () => handleEnd();
      
      window.addEventListener('mousemove', onMove);
      window.addEventListener('mouseup', onEnd);
      window.addEventListener('touchmove', onMove);
      window.addEventListener('touchend', onEnd);
      
      return () => {
        window.removeEventListener('mousemove', onMove);
        window.removeEventListener('mouseup', onEnd);
        window.removeEventListener('touchmove', onMove);
        window.removeEventListener('touchend', onEnd);
      };
    }
  }, [isDragging, pieceLeft, targetX]);

  return (
    <div id="captcha-modal" className="captcha-overlay">
      <div className="captcha-container animate-fade-in">
        <div className="captcha-header">
          <div className="captcha-icon-container">
            <ShieldCheck className="captcha-logo-icon" size={24} />
          </div>
          <h3>Verificación de Seguridad</h3>
          <p>Desliza el bloque para encajarlo en el área sombreada.</p>
        </div>
        
        <div className="captcha-puzzle-area">
          <div className="captcha-bg-image" id="captcha-bg" ref={bgRef}>
            {/* The slot that needs to be filled (shaded target area) */}
            <div 
              className="captcha-target-slot" 
              id="captcha-slot" 
              style={{ left: `${targetX}px` }}
            />
            {/* The sliding piece */}
            <div 
              className="captcha-slider-piece" 
              id="captcha-piece" 
              style={{ 
                left: `${pieceLeft}px`,
                borderColor: captchaSolved ? 'var(--success)' : '',
                transition: transitionEnabled ? 'left 0.3s ease' : 'none'
              }}
            />
          </div>
        </div>

        {/* Slider bar */}
        <div className="captcha-slider-bar-container" id="captcha-slider-bar" ref={sliderBarRef}>
          <div className="captcha-slider-track">
            <span className="captcha-slider-guide-text" style={{ opacity: guideTextOpacity }}>
              Desliza para verificar →
            </span>
          </div>
          <div 
            className="captcha-slider-thumb" 
            id="captcha-thumb"
            ref={thumbRef}
            onMouseDown={handleStart}
            onTouchStart={handleStart}
            style={{ 
              left: `${thumbLeft}px`,
              background: captchaSolved ? 'var(--success)' : '',
              cursor: captchaSolved ? 'default' : isDragging ? 'grabbing' : 'grab',
              transition: transitionEnabled ? 'left 0.3s ease' : 'none'
            }}
          >
            <ChevronsRight size={18} />
          </div>
        </div>
        
        {errorMsg && (
          <span className="error-msg" id="captcha-error-msg" style={{ display: 'block', textAlign: 'center', marginTop: '10px', backgroundColor: 'var(--danger-light)', padding: '8px', borderRadius: 'var(--radius-sm)' }}>
            ¡Verificación incorrecta! Inténtalo de nuevo.
          </span>
        )}
        
        <div className="captcha-footer">
          <button type="button" id="btn-captcha-cancel" className="btn btn-secondary" onClick={onClose} disabled={captchaSolved}>
            Cancelar
          </button>
        </div>
      </div>
    </div>
  );
}
