import { useState, useEffect } from "react";
import axios from "axios";

const API_URL = "http://localhost:8080/api/auth";

const axiosInstance = axios.create({
  baseURL: API_URL,
  withCredentials: true, // gửi cookie tự động
});

export const useAuth = () => {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  // ========================
  // LOGIN
  // ========================
  const login = async (usernameOrEmail, password) => {
    setLoading(true);
    setError(null);
    try {
      const res = await axiosInstance.post("/login", { usernameOrEmail, password });
      // user info có thể trả trong body
      setUser(res.data.user || { username: usernameOrEmail });
      setLoading(false);
      return res.data;
    } catch (err) {
      setError(err);
      setLoading(false);
      throw err;
    }
  };

  // ========================
  // REGISTER
  // ========================
  const register = async (username, email, password) => {
    setLoading(true);
    setError(null);
    try {
      const res = await axiosInstance.post("/register", { username, email, password });
      setLoading(false);
      return res.data;
    } catch (err) {
      setError(err);
      setLoading(false);
      throw err;
    }
  };

  // ========================
  // LOGOUT
  // ========================
  const logout = async () => {
    setLoading(true);
    setError(null);
    try {
      await axiosInstance.post("/logout");
      setUser(null);
      setLoading(false);
    } catch (err) {
      setError(err);
      setLoading(false);
      throw err;
    }
  };

  // ========================
  // REFRESH TOKEN
  // ========================
  const refreshToken = async () => {
    try {
      const res = await axiosInstance.post("/refresh");
      return res.data;
    } catch (err) {
      setUser(null);
      throw err;
    }
  };

  // ========================
  // AXIOS INTERCEPTOR – auto refresh
  // ========================
  useEffect(() => {
    const interceptor = axios.interceptors.response.use(
      response => response,
      async error => {
        const originalRequest = error.config;
        // 401 → access token hết hạn
        if (error.response?.status === 401 && !originalRequest._retry) {
          originalRequest._retry = true;
          try {
            await refreshToken();
            return axiosInstance(originalRequest); // retry request
          } catch (err) {
            logout(); // refresh thất bại → logout
            return Promise.reject(err);
          }
        }
        return Promise.reject(error);
      }
    );

    return () => {
      axios.interceptors.response.eject(interceptor);
    };
  }, []);

  return {
    user,
    loading,
    error,
    login,
    register,
    logout,
    refreshToken,
  };
};
