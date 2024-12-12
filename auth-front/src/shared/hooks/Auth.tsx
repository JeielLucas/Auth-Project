import { useCallback, useState } from "react";
import axiosInstance from "../../axiosConfig";

export const useAuth = () => {
    const [isAuthenticated, setIsAuthenticated] = useState(false);
    const [isActive, setIsActive] = useState(false);

    const login = async (email: string, password: string) => {

            const response = await axiosInstance.post('/login', { email, password });
            setIsAuthenticated(true);
            return response.data;
    
    };

    const register = async (email: string, confirmEmail: string, password: string, confirmPassword: string) => {
        const response = await axiosInstance.post('/register', {
                email,
                confirmEmail,
                password,
                confirmPassword,
        });

        if (response.status !== 201) {
            throw Error(response.data.message || "Erro desconhecido");
        }
        setIsAuthenticated(true);
        return response.data;
       
    };

    const ativarConta = useCallback(async (token: string) => {
        const response = await axiosInstance.get(`/ativar-conta?token=${token}`);
        setIsActive(true);
        setIsAuthenticated(true);
        return response.data;

    }, []);

    const sendResetPasswordEmail = async (email: string) => {
        const response = await axiosInstance.post(`/reset-password?email=${email}`);
        return response.data;
    };

    const redefinirSenha = async (token: string, password: string, confirmPassword: string) => {
        const response = await axiosInstance.post(`/redefinir-senha?token=${token}`, {
                password,
                confirmPassword,
        });
        return response.data;
    };

    const tokenVerification = useCallback(async () => {
        try {
            const response = await axiosInstance.get('/validar-token');
            setIsAuthenticated(true);
            return response;
        } catch(error){
            setIsAuthenticated(false);
            throw error;
        }
    }, []);

    return {
        isAuthenticated,
        login,
        register,
        ativarConta,
        redefinirSenha,
        sendResetPasswordEmail,
        tokenVerification,
        isActive,
    };
};
