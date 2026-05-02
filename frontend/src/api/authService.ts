import axiosInstance from './axiosConfig';
import { jwtDecode } from 'jwt-decode';

export interface LoginCredentials {
  email: string;
  password?: string;
}

export interface AuthResponse {
  token: string;
  id: number;
  email: string;
  firstName: string;
  lastName: string;
  role: string;
}

class AuthService {
  async login(credentials: LoginCredentials): Promise<AuthResponse> {
    const response = await axiosInstance.post<AuthResponse>('/public/auth/login', credentials);
    if (response.data.token) {
      localStorage.setItem('token', response.data.token);
      localStorage.setItem('user', JSON.stringify(response.data));
    }
    return response.data;
  }

  logout() {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
  }

  getCurrentUser(): AuthResponse | null {
    const userStr = localStorage.getItem('user');
    if (userStr) {
      return JSON.parse(userStr);
    }
    return null;
  }

  isAuthenticated(): boolean {
    const token = localStorage.getItem('token');
    if (!token) return false;

    try {
      const decodedToken: any = jwtDecode(token);
      // Check if token is expired
      if (decodedToken.exp * 1000 < Date.now()) {
        this.logout();
        return false;
      }
      return true;
    } catch (e) {
      return false;
    }
  }
}

export default new AuthService();
