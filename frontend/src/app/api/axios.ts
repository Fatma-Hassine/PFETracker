import axios, { AxiosHeaders } from 'axios';
import { getTemporaryAuthHeaders } from '../utils/authHeaders';

export const api = axios.create({
  baseURL: '/api/v2/pfeTrack',
});

api.interceptors.request.use((config) => {
  const temporaryHeaders = getTemporaryAuthHeaders();

  const headers = AxiosHeaders.from(config.headers);
  headers.set('X-User-Id', temporaryHeaders['X-User-Id']);
  headers.set('X-User-Role', temporaryHeaders['X-User-Role']);

  config.headers = headers;

  return config;
});