import axios from "axios";

// Base URL backend
const API_URL = "http://localhost:8080/api/auth";

// Axios instance với cookie support
const axiosInstance = axios.create({
  baseURL: API_URL,
  withCredentials: true, // quan trọng để gửi cookie
});

// ========================
// 1. REGISTER
// ========================
export const register = async (username, email, password) => {
  const body = { username, email, password };
  try {
    const res = await axiosInstance.post("/register", body);
    return res.data; // thường là null, server trả 200 OK
  } catch (err) {
    throw err.response?.data || err;
  }
};

// ========================
// 2. LOGIN
// ========================
export const login = async (usernameOrEmail, password) => {
  const body = { usernameOrEmail, password };
  try {
    const res = await axiosInstance.post("/login", body);
    // Token đã được gửi trong cookie HttpOnly → không cần lưu ở client
    return res.data; // có thể trả message hoặc token nếu server trả body
  } catch (err) {
    throw err.response?.data || err;
  }
};

// ========================
// 3. LOGOUT
// ========================
export const logout = async () => {
  try {
    const res = await axiosInstance.post("/logout");
    return res.data;
  } catch (err) {
    throw err.response?.data || err;
  }
};

// ========================
// 4. REFRESH TOKEN (optional)
// ========================
// Nếu backend có API /refresh, cookie refresh_token sẽ được gửi tự động
export const refreshToken = async () => {
  try {
    const res = await axiosInstance.post("/refresh");
    return res.data;
  } catch (err) {
    throw err.response?.data || err;
  }
};

export default {
  register,
  login,
  logout,
  refreshToken,
};
