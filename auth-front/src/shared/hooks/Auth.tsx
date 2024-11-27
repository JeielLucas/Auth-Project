import { useState } from "react";

export const useAuth = () => {
    const[isAuthenticated, setIsAuthenticated] = useState(false);
    const baseUrl = 'http://localhost:8080/api/v2/auth'

    const login = async (email: string, password: string) => {
        try{
            const response = await fetch(baseUrl + '/login', {
                method: 'POST',
                headers:{
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({email, password}),
                credentials: 'include',
            });

            const data = await response.json();

            if(!response.ok){
                throw new Error(data.message || 'Erro desconhecido');
            }

            setIsAuthenticated(true);

            return data;
        }catch(error){
            console.log("Erro ao fazer login: ", error);
            throw error;
        }
    };

    const register = async (email: string, confirmEmail: string, password: string, confirmPassword: string) => {
        try{
            const response = await fetch(baseUrl + '/register', {
                method: 'POST',
                headers:{
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({email, confirmEmail, password, confirmPassword}),
                 credentials: 'include',
            });

            const data = await response.json();

            if(!(response.status === 201)){
                throw Error(data.message || 'Erro desconhecido');
            }

            setIsAuthenticated(true);

            return data;
        }catch(error){
            console.log("Erro ao cadastrar: ", error);
            throw error;
        }
    };

    const ativarConta = async (token: string) =>{
        try{
            const response = await fetch(baseUrl + `/ativar-conta?token=${token}`, {
                method: 'GET',
                headers:{
                    'Content-Type': 'application/json',
                },
                 credentials: 'include',
            });

            const data = await response.json();

            if(!response.ok){
                throw new Error(data.message || 'Erro desconhecido')
            }

            setIsAuthenticated(true);

            return data;
        }catch(error){
            console.log("Erro ao ativar conta: " + error)
            throw error;
        }
    };

    const sendResetPasswordEmail = async (email: string) =>{
        try{
            const response = await fetch(baseUrl + `/reset-password?email=${email}`,{
                method: 'POST',
                headers:{
                    'Content-Type': 'application/json',
                },
                credentials: 'include',
            })
            const data = await response.json();

            if(!response.ok){
                throw new Error(data.message || "Erro desconhecido")
            }

            return data;

        }catch(error){
            console.log('Erro ao enviar email reset de senha', error)
            throw error;
        }
    };

    const redefinirSenha = async(token: string, password: string, confirmPassword: string) =>{
        try{
            const response = await fetch(baseUrl + `/redefinir-senha?token=${token}`,{
                method: 'POST',
                headers:{
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({password, confirmPassword}),
                credentials: 'include',
            })

            const data = await response.json();

            if(!response.ok){
                throw new Error(data.message || 'Erro desconhecido');
            }
            
            return data;
        }catch(error){
            console.log('Erro ao redefinir senha', error);
            throw error;
        }
    };

    const tokenVerification = async() =>{
        try{
            const response = await fetch(baseUrl + `validar-token`,{
                method: 'GET',
                headers:{
                    'Content-Type': 'application/json',
                },
                credentials: 'include'
            })

            const data = await response.json();

            if(!response.ok){
                throw new Error(data.message || 'Erro desconhecido');
            }

            return data;
        }catch(error){
            console.log('Erro ao verificar token: ', error)
            throw error;
        }
    }

    return {isAuthenticated, login, register, ativarConta, redefinirSenha, sendResetPasswordEmail, tokenVerification};
};