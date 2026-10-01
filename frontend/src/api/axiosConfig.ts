import axios from 'axios';
import { rafraichirToken, sessionExpiree } from '../services/api';

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || '/api/v3';

const axiosInstance = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

axiosInstance.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token');
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

// Token expiré (401) : on le renouvelle via /auth/refresh (module 1) puis on rejoue la requête.
axiosInstance.interceptors.response.use(
  (response) => response,
  async (error) => {
    const originalRequest = error.config;

    if (error.response?.status === 401 && !originalRequest._retry) {
      originalRequest._retry = true;
      const nouveau = await rafraichirToken();

      if (nouveau) {
        originalRequest.headers['Authorization'] = `Bearer ${nouveau}`;
        return axiosInstance(originalRequest);
      }

      sessionExpiree();
    }
    return Promise.reject(error);
  }
);

export default axiosInstance;
