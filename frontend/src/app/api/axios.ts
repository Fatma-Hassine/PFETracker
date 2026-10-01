import axios, { AxiosHeaders } from 'axios';
import { rafraichirToken, sessionExpiree } from '../../services/api';

// Le module 2 (PFE/tâches/jalons) est protégé par le JWT du Module 1.
export const api = axios.create({
  baseURL: '/api/v2/pfeTrack',
});

api.interceptors.request.use((config) => {
  const token = localStorage.getItem('token');

  const headers = AxiosHeaders.from(config.headers);
  if (token) {
    headers.set('Authorization', `Bearer ${token}`);
  }

  config.headers = headers;

  return config;
});

api.interceptors.response.use(
  (response) => response,
  async (error) => {
    const originalRequest = error.config;

    if (error.response?.status === 401 && !originalRequest._retry) {
      originalRequest._retry = true;
      const nouveau = await rafraichirToken();

      if (nouveau) {
        originalRequest.headers.set('Authorization', `Bearer ${nouveau}`);
        return api(originalRequest);
      }

      sessionExpiree();
    }
    return Promise.reject(error);
  }
);
