import { useNavigate } from "react-router-dom";
import { useState } from "react";
import { Form } from "../../shared/components/Form/Form";
import { useAuth } from "../../shared/hooks";

export const LoginPage = () => {
    const { login } = useAuth();
    const navigate = useNavigate();
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [error, setError] = useState('');
    

    const handleInputChange = (id: string, value: string) =>{
        if(id === "email"){
            setEmail(value)
        } else if(id === "password"){
            setPassword(value)
        }

    };

    const handleEntrar = async (formData: { [key: string]: string}) => {
        try{
            await login(formData.email, formData.password);
            navigate('/dashboard')
        }catch(error){
            setError('Erro ao fazer login, verifique suas credenciais');
        }


    };

    const inputs = [
        {
            id: 'email',
            type: 'email',
            placeholder: 'Digite seu email',
            required: true,
            value: email,
            labelText: 'Email',
            onChange: (e: React.ChangeEvent<HTMLInputElement>) => handleInputChange("email", e.target.value),
        },
        {
            id: 'password',
            type: 'password',
            placeholder: 'Digite sua senha',
            required: true,
            value: password,
            labelText: 'Senha',
            onChange: (e: React.ChangeEvent<HTMLInputElement>) => handleInputChange("password", e.target.value),
        },
    ];



    return(
        <div className='divLogin'>
            <Form 
            text='Login page' 
            input={inputs} 
            onSubmit={handleEntrar} 
            buttonText="Entrar"
             buttonType="submit"
             link='/register'
             linkText= 'Já tem uma conta?'
             errorMessage={error}
             />
        </div>
    );
};