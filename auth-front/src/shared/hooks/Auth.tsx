import { useCallback, useState } from "react";
import axiosInstance from "../../axiosConfig";

export const useAuth = () => {
    const [isAuthenticated, setIsAuthenticated] = useState(false);
    const [isActive, setIsActive] = useState(false);

    const login = async (email: string, password: string) => {
        try {
            const response = await axiosInstance.post('/login', { email, password });
            setIsAuthenticated(true);
            return response.data;
        } catch (error) {
            console.error("Erro ao fazer login:", error);
            throw error;
        }
    };

    const register = async (email: string, confirmEmail: string, password: string, confirmPassword: string) => {
        try {
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
        } catch (error) {
            console.error("Erro ao cadastrar:", error);
            throw error;
        }
    };

    const ativarConta = useCallback(async (token: string) => {
        try {
            const response = await axiosInstance.get(`/ativar-conta?token=${token}`);
            setIsActive(true);
            setIsAuthenticated(true);
            return response.data;
        } catch (error) {
            console.error("Erro ao ativar conta:", error);
            throw error;
        }
    }, []);

    const sendResetPasswordEmail = async (email: string) => {
        try {
            const response = await axiosInstance.post(`/reset-password?email=${email}`);
            return response.data;
        } catch (error) {
            console.error("Erro ao enviar email de redefinição de senha:", error);
            throw error;
        }
    };

    const redefinirSenha = async (token: string, password: string, confirmPassword: string) => {
        try {
            const response = await axiosInstance.post(`/redefinir-senha?token=${token}`, {
                password,
                confirmPassword,
            });
            return response.data;
        } catch (error) {
            console.error("Erro ao redefinir senha:", error);
            throw error;
        }
    };

    const tokenVerification = async (): Promise<void> => {
        try {
            await axiosInstance.get('/validar-token');
            setIsAuthenticated(true);
        } catch {
            console.error("Erro ao verificar token");
            setIsAuthenticated(false);
        }
    };

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
