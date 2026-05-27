// API Configuration
const API_BASE = '/api/v1';
const ENDPOINTS = {
    shifts: `${API_BASE}/shifts`,
    employees: `${API_BASE}/employees`,
    labors: `${API_BASE}/labors`
};

function getAuthHeaders(includeCaptcha = false) {
    const headers = {};
    const savedUser = localStorage.getItem('mihorario_user');
    let user = currentUser;
    if (!user && savedUser) {
        try {
            user = JSON.parse(savedUser);
        } catch (e) {}
    }
    if (user) {
        const token = user.role === 'admin' ? 'token-valid-coordinador' : 'token-invalid-empleado';
        headers['Authorization'] = `Bearer ${token}`;
    }
    if (includeCaptcha) {
        headers['X-Captcha-Token'] = 'google-recaptcha-v3-token-valid';
    }
    return headers;
}

// Global State
let employees = [];
let labors = [];
let shifts = [];
let currentUser = null; // { id, name, email, role: 'admin' | 'employee' }

// Captcha State
let captchaSolved = false;
let pendingUser = null;
let targetX = 0;
let isDragging = false;
let startX = 0;
let currentLeft = 0;

// DOM Elements - Login Screen
const loginScreen = document.getElementById('login-screen');
const loginForm = document.getElementById('login-form');
const loginUsername = document.getElementById('login-username');
const loginPassword = document.getElementById('login-password');
const loginCredentialsError = document.getElementById('login-credentials-error');
const loginUsernameError = document.getElementById('login-username-error');
const loginPasswordError = document.getElementById('login-password-error');

// DOM Elements - Main App Container
const appContainer = document.getElementById('app-container');
const btnLogout = document.getElementById('btn-logout');
const userAvatar = document.getElementById('user-avatar');
const userName = document.getElementById('user-name');
const userRole = document.getElementById('user-role');
const navItemSchedule = document.getElementById('nav-item-schedule');

// DOM Elements - Form & Dashboard
const shiftForm = document.getElementById('shift-form');
const employeeSelect = document.getElementById('employee-select');
const laborSelect = document.getElementById('labor-select');
const shiftDate = document.getElementById('shift-date');
const startTime = document.getElementById('start-time');
const endTime = document.getElementById('end-time');
const shiftActive = document.getElementById('shift-active');
const shiftObservation = document.getElementById('shift-observation');
const btnSubmit = document.getElementById('btn-submit');
const scheduleSection = document.getElementById('schedule-section');
const splitView = document.querySelector('.split-view');

const shiftsTbody = document.getElementById('shifts-tbody');
const searchInput = document.getElementById('search-input');
const thActions = document.getElementById('th-actions');

// Statistics DOM Elements
const statTotal = document.getElementById('stat-total');
const statActive = document.getElementById('stat-active');
const statInactive = document.getElementById('stat-inactive');

const toastContainer = document.getElementById('toast-container');

// DOM Elements - Captcha Modal
const captchaModal = document.getElementById('captcha-modal');
const captchaBg = document.getElementById('captcha-bg');
const captchaSlot = document.getElementById('captcha-slot');
const captchaPiece = document.getElementById('captcha-piece');
const captchaThumb = document.getElementById('captcha-thumb');
const captchaSliderBar = document.getElementById('captcha-slider-bar');
const captchaErrorMsg = document.getElementById('captcha-error-msg');
const btnCaptchaCancel = document.getElementById('btn-captcha-cancel');

// Initialize the Application
document.addEventListener('DOMContentLoaded', async () => {
    // 1. Initialise Lucide icons
    lucide.createIcons();
    
    // 2. Set default date to today
    const today = new Date().toISOString().split('T')[0];
    shiftDate.value = today;

    // 3. Pre-load Static API Data (needed for login check and selects)
    await loadStaticData();

    // 4. Check for existing session
    checkSession();

    // 5. Captcha Dragging Listeners
    captchaThumb.addEventListener('mousedown', handleStart);
    document.addEventListener('mousemove', handleMove);
    document.addEventListener('mouseup', handleEnd);
    
    captchaThumb.addEventListener('touchstart', handleStart);
    document.addEventListener('touchmove', handleMove);
    document.addEventListener('touchend', handleEnd);
    
    btnCaptchaCancel.addEventListener('click', () => {
        captchaModal.classList.add('hidden');
        pendingUser = null;
    });
});

// Toast notification helper
function showToast(title, description, type = 'success') {
    const toast = document.createElement('div');
    toast.className = `toast toast-${type}`;
    
    const iconName = type === 'success' ? 'check-circle-2' : 'alert-circle';
    
    toast.innerHTML = `
        <div class="toast-icon">
            <i data-lucide="${iconName}"></i>
        </div>
        <div class="toast-message">
            <span class="toast-title">${title}</span>
            <span class="toast-description">${description}</span>
        </div>
    `;
    
    toastContainer.appendChild(toast);
    lucide.createIcons({ props: { class: 'toast-icon-svg' } });
    
    // Auto remove after 4 seconds
    setTimeout(() => {
        toast.style.opacity = '0';
        toast.style.transform = 'translateY(20px)';
        setTimeout(() => toast.remove(), 300);
    }, 4000);
}

// Check session in localStorage
function checkSession() {
    const savedUser = localStorage.getItem('mihorario_user');
    if (savedUser) {
        try {
            currentUser = JSON.parse(savedUser);
            setupAppForUser();
        } catch (e) {
            localStorage.removeItem('mihorario_user');
            showLogin();
        }
    } else {
        showLogin();
    }
}

// Show login screen
function showLogin() {
    loginScreen.classList.remove('hidden');
    appContainer.classList.add('hidden');
    loginCredentialsError.style.display = 'none';
}

// Set up App state and visibility based on role
async function setupAppForUser() {
    loginScreen.classList.add('hidden');
    appContainer.classList.remove('hidden');
    
    // 1. Update Profile Information
    userName.textContent = currentUser.name;
    userRole.textContent = currentUser.role === 'admin' ? 'Administrador' : 'Empleado';
    userAvatar.textContent = getInitials(currentUser.name);

    // 2. Adjust layouts and menus depending on role
    if (currentUser.role === 'admin') {
        // Show Scheduling features
        scheduleSection.classList.remove('hidden');
        navItemSchedule.classList.remove('hidden');
        splitView.classList.remove('full-width');
        thActions.classList.remove('hidden');
        
        // Update Stat Labels
        document.querySelector('.stat-card:nth-child(1) .stat-label').textContent = 'Turnos Totales';
        document.querySelector('.stat-card:nth-child(2) .stat-label').textContent = 'Turnos Activos';
        document.querySelector('.stat-card:nth-child(3) .stat-label').textContent = 'Turnos Inactivos';
    } else {
        // Hide Scheduling features for Employees
        scheduleSection.classList.add('hidden');
        navItemSchedule.classList.add('hidden');
        splitView.classList.add('full-width');
        thActions.classList.add('hidden');
        
        // Update Stat Labels
        document.querySelector('.stat-card:nth-child(1) .stat-label').textContent = 'Mis Turnos Totales';
        document.querySelector('.stat-card:nth-child(2) .stat-label').textContent = 'Mis Turnos Activos';
        document.querySelector('.stat-card:nth-child(3) .stat-label').textContent = 'Mis Turnos Inactivos';
    }

    // Refresh static data in case it wasn't loaded
    if (employees.length === 0 || labors.length === 0) {
        await loadStaticData();
    }

    // Refresh shifts from API
    await loadShifts();
}

// Helper to get name initials
function getInitials(name) {
    const parts = name.split(' ');
    if (parts.length >= 2) {
        return (parts[0][0] + parts[1][0]).toUpperCase();
    }
    return name.substring(0, 2).toUpperCase();
}

// Pre-load static data (employees & labors)
async function loadStaticData() {
    try {
        const [empRes, labRes] = await Promise.all([
            fetch(ENDPOINTS.employees, { headers: getAuthHeaders() }).catch(() => null),
            fetch(ENDPOINTS.labors, { headers: getAuthHeaders() }).catch(() => null)
        ]);

        if (empRes && empRes.ok) {
            employees = await empRes.json();
            populateSelect(employeeSelect, employees, e => `${e.name} ${e.lastName} (${e.identification})`);
        }
        
        if (labRes && labRes.ok) {
            labors = await labRes.json();
            populateSelect(laborSelect, labors, l => `${l.name} - ${l.description}`);
        }
    } catch (err) {
        console.error('Error in initial load:', err);
    }
}

// Helper to populate select dropdowns
function populateSelect(selectEl, items, textFn) {
    const placeholder = selectEl.options[0];
    selectEl.innerHTML = '';
    selectEl.appendChild(placeholder);

    items.forEach(item => {
        const option = document.createElement('option');
        option.value = item.id;
        option.textContent = textFn(item);
        selectEl.appendChild(option);
    });
}

// Fetch all shifts and render
async function loadShifts() {
    renderLoading(true);
    try {
        const res = await fetch(ENDPOINTS.shifts, { headers: getAuthHeaders() });
        if (res.ok) {
            shifts = await res.json();
            
            // Filter shifts if current user is an employee
            let filteredShifts = [...shifts];
            if (currentUser && currentUser.role === 'employee') {
                filteredShifts = shifts.filter(s => s.employeeId === currentUser.id);
            }

            renderShifts(filteredShifts);
            updateStats(filteredShifts);
        } else {
            showToast('Error', 'No se pudieron cargar los turnos.', 'error');
            renderErrorState();
        }
    } catch (err) {
        console.error('Error fetching shifts:', err);
        showToast('Error de Red', 'No se pudieron cargar los turnos del servidor.', 'error');
        renderErrorState();
    }
}

// Render shifts into the table
function renderShifts(shiftsList) {
    shiftsTbody.innerHTML = '';
    
    if (shiftsList.length === 0) {
        const columns = currentUser && currentUser.role === 'admin' ? 7 : 6;
        shiftsTbody.innerHTML = `
            <tr class="empty-row">
                <td colspan="${columns}">
                    <div class="empty-container">
                        <i data-lucide="calendar-x"></i>
                        <span>No hay turnos registrados en este momento.</span>
                    </div>
                </td>
            </tr>
        `;
        lucide.createIcons();
        return;
    }

    shiftsList.forEach(shift => {
        const tr = document.createElement('tr');
        
        // Find Names from IDs
        const emp = employees.find(e => e.id === shift.employeeId);
        const empName = emp ? `${emp.name} ${emp.lastName}` : `ID: ${shift.employeeId.substring(0,8)}...`;
        
        const lab = labors.find(l => l.id === shift.laborId);
        const labName = lab ? lab.name : `ID: ${shift.laborId.substring(0,8)}...`;

        // Format Date nicely
        const dateFormatted = formatDate(shift.date);

        // Format times (remove seconds if present)
        const timeStart = shift.startTime.substring(0, 5);
        const timeEnd = shift.endTime.substring(0, 5);

        // Status Badge
        const statusBadge = shift.active 
            ? '<span class="badge badge-active">Activo</span>' 
            : '<span class="badge badge-inactive">Inactivo</span>';

        let actionColumn = '';
        if (currentUser && currentUser.role === 'admin') {
            actionColumn = `
                <td>
                    <div class="actions-cell">
                        <button class="btn-delete" onclick="handleDeleteShift('${shift.id}')" title="Eliminar Turno">
                            <i data-lucide="trash-2"></i>
                        </button>
                    </div>
                </td>
            `;
        }

        tr.innerHTML = `
            <td style="font-weight: 600;">${escapeHtml(empName)}</td>
            <td>${escapeHtml(labName)}</td>
            <td>${dateFormatted}</td>
            <td style="font-family: monospace; font-weight: 500;">${timeStart} - ${timeEnd}</td>
            <td>${statusBadge}</td>
            <td class="observation-cell" title="${escapeHtml(shift.observation || 'Ninguna')}">
                ${escapeHtml(shift.observation || '—')}
            </td>
            ${actionColumn}
        `;
        
        shiftsTbody.appendChild(tr);
    });

    lucide.createIcons();
}

// Update dashboard cards stats
function updateStats(shiftsList) {
    const total = shiftsList.length;
    const active = shiftsList.filter(s => s.active).length;
    const inactive = total - active;

    statTotal.textContent = total;
    statActive.textContent = active;
    statInactive.textContent = inactive;
}

// Render Loading table state
function renderLoading(isLoading) {
    if (isLoading) {
        const columns = currentUser && currentUser.role === 'admin' ? 7 : 6;
        shiftsTbody.innerHTML = `
            <tr class="loading-row">
                <td colspan="${columns}">
                    <div class="loading-container">
                        <div class="spinner"></div>
                        <span>Cargando turnos...</span>
                    </div>
                </td>
            </tr>
        `;
    }
}

function renderErrorState() {
    const columns = currentUser && currentUser.role === 'admin' ? 7 : 6;
    shiftsTbody.innerHTML = `
        <tr class="empty-row">
            <td colspan="${columns}">
                <div class="empty-container">
                    <i data-lucide="alert-triangle" style="color: var(--danger);"></i>
                    <span>Error al cargar datos. Verifica la conexión con el backend.</span>
                </div>
            </td>
        </tr>
    `;
    lucide.createIcons();
}

// Captcha Implementation
function initCaptcha(user) {
    pendingUser = user;
    captchaSolved = false;
    captchaErrorMsg.style.display = 'none';
    
    // Reset positions
    currentLeft = 0;
    captchaPiece.style.left = '10px';
    captchaThumb.style.left = '0px';
    
    // Generate random target position for slot between 100px and 240px
    const maxTarget = captchaBg.offsetWidth > 0 ? (captchaBg.offsetWidth - 70) : 240;
    targetX = Math.floor(Math.random() * (maxTarget - 100)) + 100;
    captchaSlot.style.left = `${targetX}px`;
    
    // Reset guide text opacity
    const guideText = document.querySelector('.captcha-slider-guide-text');
    if (guideText) {
        guideText.style.opacity = '1';
    }
    
    // Show modal
    captchaModal.classList.remove('hidden');
}

function handleStart(e) {
    if (captchaSolved) return;
    isDragging = true;
    startX = e.clientX || (e.touches && e.touches[0].clientX);
    captchaThumb.style.cursor = 'grabbing';
    captchaPiece.style.cursor = 'grabbing';
}

function handleMove(e) {
    if (!isDragging || captchaSolved) return;
    const clientX = e.clientX || (e.touches && e.touches[0].clientX);
    if (clientX === undefined) return;
    
    const deltaX = clientX - startX;
    
    const maxSlide = captchaSliderBar.offsetWidth - captchaThumb.offsetWidth;
    let newLeft = Math.max(0, Math.min(deltaX, maxSlide));
    
    captchaThumb.style.left = `${newLeft}px`;
    
    const maxPieceSlide = captchaBg.offsetWidth - captchaPiece.offsetWidth - 10;
    const pieceLeft = 10 + (newLeft / maxSlide) * maxPieceSlide;
    captchaPiece.style.left = `${pieceLeft}px`;
    
    currentLeft = pieceLeft;
    
    const guideText = document.querySelector('.captcha-slider-guide-text');
    if (guideText) {
        guideText.style.opacity = Math.max(0, 1 - (newLeft / 60));
    }
}

function handleEnd() {
    if (!isDragging || captchaSolved) return;
    isDragging = false;
    captchaThumb.style.cursor = 'grab';
    captchaPiece.style.cursor = 'grab';
    
    const tolerance = 6;
    const diff = Math.abs(currentLeft - targetX);
    
    if (diff <= tolerance) {
        // Success
        captchaSolved = true;
        captchaErrorMsg.style.display = 'none';
        
        captchaThumb.style.background = 'var(--success)';
        captchaPiece.style.borderColor = 'var(--success)';
        
        setTimeout(() => {
            currentUser = pendingUser;
            localStorage.setItem('mihorario_user', JSON.stringify(currentUser));
            localStorage.setItem('mihorario_captcha_token', 'google-recaptcha-v3-token-valid');
            
            captchaModal.classList.add('hidden');
            setupAppForUser();
            
            showToast('Acceso Concedido', `Bienvenido(a), ${currentUser.name}.`, 'success');
            
            // Clean up visual changes
            captchaThumb.style.background = '';
            captchaPiece.style.borderColor = '';
        }, 600);
    } else {
        // Fail
        captchaErrorMsg.style.display = 'block';
        
        captchaThumb.style.transition = 'left 0.3s ease';
        captchaPiece.style.transition = 'left 0.3s ease';
        
        captchaThumb.style.left = '0px';
        captchaPiece.style.left = '10px';
        
        const guideText = document.querySelector('.captcha-slider-guide-text');
        if (guideText) {
            guideText.style.opacity = '1';
        }
        
        setTimeout(() => {
            captchaThumb.style.transition = '';
            captchaPiece.style.transition = '';
        }, 300);
    }
}

// Login Submit Handler
loginForm.addEventListener('submit', (e) => {
    e.preventDefault();
    loginCredentialsError.style.display = 'none';

    const usernameVal = loginUsername.value.trim().toLowerCase();
    const passwordVal = loginPassword.value.trim();

    let isValid = true;
    if (!usernameVal) {
        loginUsername.parentElement.classList.add('invalid');
        loginUsernameError.style.display = 'block';
        isValid = false;
    } else {
        loginUsername.parentElement.classList.remove('invalid');
        loginUsernameError.style.display = 'none';
    }

    if (!passwordVal) {
        loginPassword.parentElement.classList.add('invalid');
        loginPasswordError.style.display = 'block';
        isValid = false;
    } else {
        loginPassword.parentElement.classList.remove('invalid');
        loginPasswordError.style.display = 'none';
    }

    if (!isValid) return;

    // Admin Auth Mock Check
    if (usernameVal === 'admin' && passwordVal === 'admin123') {
        const adminUser = {
            role: 'admin',
            name: 'Administrador Sistema',
            email: 'admin@mihorario.com'
        };
        initCaptcha(adminUser);
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
        initCaptcha(empUser);
    } else {
        loginCredentialsError.style.display = 'block';
    }
});

// Logout Handler
btnLogout.addEventListener('click', (e) => {
    e.preventDefault();
    currentUser = null;
    localStorage.removeItem('mihorario_user');
    localStorage.removeItem('mihorario_captcha_token');
    
    // Clear inputs
    loginUsername.value = '';
    loginPassword.value = '';
    
    showLogin();
    showToast('Sesión Cerrada', 'Has cerrado tu sesión correctamente.', 'success');
});

// Delete Shift Handler
async function handleDeleteShift(id) {
    if (!confirm('¿Estás seguro de que deseas eliminar este turno?')) {
        return;
    }

    try {
        const res = await fetch(`${ENDPOINTS.shifts}/${id}`, {
            method: 'DELETE',
            headers: getAuthHeaders()
        });

        if (res.ok) {
            showToast('Turno Eliminado', 'El turno ha sido eliminado del sistema con éxito.', 'success');
            await loadShifts();
        } else {
            showToast('Error', 'No se pudo eliminar el turno.', 'error');
        }
    } catch (err) {
        console.error('Error deleting shift:', err);
        showToast('Error', 'Problema de conexión al eliminar el turno.', 'error');
    }
}

// Filter shifts dynamically on search input
searchInput.addEventListener('input', (e) => {
    const query = e.target.value.toLowerCase().trim();
    
    // Employee can filter their own, Admin can search all
    let listToFilter = [...shifts];
    if (currentUser && currentUser.role === 'employee') {
        listToFilter = shifts.filter(s => s.employeeId === currentUser.id);
    }

    if (!query) {
        renderShifts(listToFilter);
        return;
    }

    const filtered = listToFilter.filter(shift => {
        const emp = employees.find(e => e.id === shift.employeeId);
        const empName = emp ? `${emp.name} ${emp.lastName}`.toLowerCase() : '';
        return empName.includes(query);
    });

    renderShifts(filtered);
});

// Form Submission & Validation (Only accessible to admin, but coded safely)
shiftForm.addEventListener('submit', async (e) => {
    e.preventDefault();
    
    if (validateForm()) {
        const shiftDataObj = {
            employeeId: employeeSelect.value,
            laborId: laborSelect.value,
            date: shiftDate.value,
            startTime: formatTimeToLocalTime(startTime.value),
            endTime: formatTimeToLocalTime(endTime.value),
            active: shiftActive.checked,
            observation: shiftObservation.value.trim()
        };

        btnSubmit.disabled = true;
        btnSubmit.innerHTML = '<div class="spinner" style="width: 16px; height: 16px; border-width: 2px;"></div> Guardando...';

        try {
            const res = await fetch(ENDPOINTS.shifts, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    ...getAuthHeaders(true)
                },
                body: JSON.stringify(shiftDataObj)
            });

            if (res.ok || res.status === 201) {
                showToast('Turno Registrado', 'El turno ha sido guardado y programado con éxito.', 'success');
                shiftForm.reset();
                // Set default date back to today
                shiftDate.value = new Date().toISOString().split('T')[0];
                shiftActive.checked = true;
                
                await loadShifts();
            } else {
                const errorText = await res.text();
                showToast('Error al Guardar', errorText || 'El turno no se pudo guardar.', 'error');
            }
        } catch (err) {
            console.error('Error saving shift:', err);
            showToast('Error de Conexión', 'No se pudo conectar con el servidor para registrar el turno.', 'error');
        } finally {
            btnSubmit.disabled = false;
            btnSubmit.innerHTML = '<i data-lucide="save"></i> Guardar Turno';
            lucide.createIcons();
        }
    }
});

// Form validation helper
function validateForm() {
    let isValid = true;

    // Helper to validate individual inputs
    const validateInput = (inputEl, errorEl, condition) => {
        if (condition) {
            inputEl.parentElement.classList.remove('invalid');
            errorEl.style.display = 'none';
        } else {
            inputEl.parentElement.classList.add('invalid');
            errorEl.style.display = 'block';
            isValid = false;
        }
    };

    validateInput(employeeSelect, document.getElementById('employee-error'), employeeSelect.value !== "");
    validateInput(laborSelect, document.getElementById('labor-error'), laborSelect.value !== "");
    validateInput(shiftDate, document.getElementById('date-error'), shiftDate.value !== "");
    validateInput(startTime, document.getElementById('start-time-error'), startTime.value !== "");
    validateInput(endTime, document.getElementById('end-time-error'), endTime.value !== "");

    // Time range validation
    const rangeError = document.getElementById('range-error');
    if (startTime.value && endTime.value) {
        if (startTime.value >= endTime.value) {
            startTime.parentElement.classList.add('invalid');
            endTime.parentElement.classList.add('invalid');
            rangeError.style.display = 'block';
            isValid = false;
        } else {
            rangeError.style.display = 'none';
        }
    } else {
        rangeError.style.display = 'none';
    }

    return isValid;
}

// Format time inputs from HH:MM to HH:MM:SS format
function formatTimeToLocalTime(timeStr) {
    if (timeStr && timeStr.split(':').length === 2) {
        return `${timeStr}:00`;
    }
    return timeStr;
}

// Utility to format date for display (e.g. 24 de May, 2026)
function formatDate(dateString) {
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

// Clean HTML to prevent XSS
function escapeHtml(unsafe) {
    return String(unsafe)
         .replace(/&/g, "&amp;")
         .replace(/</g, "&lt;")
         .replace(/>/g, "&gt;")
         .replace(/"/g, "&quot;")
         .replace(/'/g, "&#039;");
}
