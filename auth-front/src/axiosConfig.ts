import axios from "axios";

const axiosInstance = axios.create({
    baseURL: 'http://localhost:8080/api/v2/auth',
    withCredentials: true,
});

export default axiosInstance;
