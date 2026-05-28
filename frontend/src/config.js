export const API_BASE = '/api/v1';

export const ENDPOINTS = {
    shifts: `${API_BASE}/shifts`,
    employees: `${API_BASE}/employees`,
    labors: `${API_BASE}/labors`
};

export const KEYCLOAK_CONFIG = {
    url: import.meta.env.VITE_KEYCLOAK_URL || 'http://localhost:8090',
    realm: 'mihorario-realm',
    clientId: 'mihorario-client'
};

export function getAuthHeaders(token, includeCaptcha = false) {
    const headers = {};
    if (token) {
        headers['Authorization'] = `Bearer ${token}`;
    }
    if (includeCaptcha) {
        headers['X-Captcha-Token'] = 'google-recaptcha-v3-token-valid';
    }
    return headers;
}
