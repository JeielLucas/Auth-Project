import { useCallback, useState } from "react";
import axiosInstance from "../../axiosConfig";
import { useDispatch } from "react-redux";
import { setEmailData } from "../../redux/authSlice";
import { AxiosError } from "axios";

export const useAuth = () => {
    const [isAuthenticated, setIsAuthenticated] = useState(false);
    const [isActive, setIsActive] = useState(false);
    const dispatch = useDispatch();

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
        const response = await axiosInstance.get(`/users/activate?token=${token}`);
        setIsActive(true);
        setIsAuthenticated(true);
        return response.data;

    }, []);

    const sendResetPasswordEmail = async (email: string) => {
        const response = await axiosInstance.post(`/forgot-password?email=${email}`);
        return response.data;
    };

    const redefinirSenha = async (token: string, password: string, confirmPassword: string) => {
        const response = await axiosInstance.patch(`/reset-password?token=${token}`, {
                password,
                confirmPassword,
        });
        return response.data;
    };

    const tokenVerification = useCallback(async () => {
        try {
            const response = await axiosInstance.get('/check');
            setIsAuthenticated(true);
            return response;
        } catch(error){
            setIsAuthenticated(false);
            throw error;
        }
    }, []);

    const loginGoogle = async (credentialResponse: string) =>{
        try{
            const response = await axiosInstance.post(`/login/google?token=${credentialResponse}`)
            return response;
        }catch(error: unknown){
            if(error instanceof AxiosError){
                if(error.response && error.response.data && error.response.data.data && error.response.data.data.email){
                    const email = error.response.data.data.email;
                    dispatch(setEmailData(email))
                }
            }
            
            throw error;
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
        loginGoogle,
    };
};
