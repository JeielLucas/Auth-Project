import { useState } from "react";

export const useAuth = () => {
    const[isAuthenticated, setIsAuthenticated] = useState(false);
    const baseUrl = 'http://localhost:8080/api/v1/auth'

    const login = async (email: string, password: string) => {

        try{
            const response = await fetch(baseUrl + '/login', {
                method: 'POST',
                headers:{
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({email, password}),
            });

            if(!response.ok){
                throw new Error('Login falhou');
            }

            const data = await response.text();
            alert(data);
            setIsAuthenticated(true);

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
            });

            if(!(response.status === 201)){
                if(response.status === 400){
                    throw new Error("Email e/ou senha não conferem");
                }
                if(response.status === 409){
                    console.log("409")
                    throw new Error("E-mail já cadastrado");
                }
                throw new Error('Cadastro falhou');
            }
            

            const data = await response.text();
            alert(data);
            setIsAuthenticated(true);

        }catch(error){
            console.log(error);
            throw error;
        }
    };

    return {isAuthenticated, login, register};
};