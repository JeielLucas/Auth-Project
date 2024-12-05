import { useState } from "react";
import axiosInstance from "../../axiosConfig";

export const useAuth = () => {
    const[isAuthenticated, setIsAuthenticated] = useState(false);

    const login = async (email: string, password: string) => {
        try{
            const response = await axiosInstance.post(
                '/login',
                {email, password},
            );

            if(response.status !== 200){
                throw new Error(response.data.message || 'Erro desconhecido');
            }

            setIsAuthenticated(true);

            return response.data;
        }catch(error){
            console.log("Erro ao fazer login: ", error);
            throw error;
        }
    };

    const register = async (email: string, confirmEmail: string, password: string, confirmPassword: string) => {
        try{
            const response = await axiosInstance.post(
                '/register',
                {email, confirmEmail, password, confirmPassword}
            );

            if(response.status !== 201){
                throw Error(response.data.message || 'Erro desconhecido');
            }

            setIsAuthenticated(true);

            return response.data;
        }catch(error){
            console.log("Erro ao cadastrar: ", error);
            throw error;
        }
    };

    const ativarConta = async (token: string) =>{
        try{
            const response = await axiosInstance.get(
                `/ativar-conta?token=${token}`
            );

            if(response.status !== 200){
                throw new Error(response.data.message || 'Erro desconhecido')
            }

            setIsAuthenticated(true);

            return response.data;
        }catch(error){
            console.log("Erro ao ativar conta: " + error)
            throw error;
        }
    };

    const sendResetPasswordEmail = async (email: string) =>{
        try{
            const response = await axiosInstance.post(
                `/reset-password?email=${email}`,
            );

            if(response.status !== 200){
                throw new Error(response.data.message || "Erro desconhecido")
            }

            return response.data;

        }catch(error){
            console.log('Erro ao enviar email reset de senha', error)
            throw error;
        }
    };

    const redefinirSenha = async(token: string, password: string, confirmPassword: string) =>{
        try{
            const response = await axiosInstance.post(
                `/redefinir-senha?token${token}`,
                {password, confirmPassword},
            );

            if(response.status !== 200){
                throw new Error(response.data.message || 'Erro desconhecido');
            }
            
            return response.data;
        }catch(error){
            console.log('Erro ao redefinir senha', error);
            throw error;
        }
    };

    const tokenVerification = async(): Promise<{ isAuthenticated: boolean}> =>{
        try{
            const response = await axiosInstance.get('/validar-token');
            return {isAuthenticated: true}
        }catch{
            console.log("error")
            return {isAuthenticated: false}
        }
    }

    return {isAuthenticated, login, register, ativarConta, redefinirSenha, sendResetPasswordEmail, tokenVerification};
};