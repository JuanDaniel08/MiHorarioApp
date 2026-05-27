export const API_BASE = '/api/v1';

export const ENDPOINTS = {
    shifts: `${API_BASE}/shifts`,
    employees: `${API_BASE}/employees`,
    labors: `${API_BASE}/labors`
};

export function getAuthHeaders(currentUser, includeCaptcha = false) {
    const headers = {};
    if (currentUser) {
        const token = currentUser.role === 'admin' ? 'token-valid-coordinador' : 'token-invalid-empleado';
        headers['Authorization'] = `Bearer ${token}`;
    }
    if (includeCaptcha) {
        headers['X-Captcha-Token'] = 'google-recaptcha-v3-token-valid';
    }
    return headers;
}
