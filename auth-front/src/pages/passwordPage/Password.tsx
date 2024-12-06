import { useState } from "react";
import { Form } from "../../shared/components/Form/Form";
import { useAuth } from "../../shared/hooks/Auth"
import { Navigate, useParams } from "react-router-dom";


export const PasswordPage = () =>{
    const{ redefinirSenha } = useAuth();
    const [ password, setPassword ] = useState('');
    const [ confirmPassword, setConfirmPassword ] = useState('');
    const [ error, setError ] = useState('');
    const { token } = useParams<{token: string}>();

    const handleInputChange = (id: string, value: string) =>{
        if(id === 'password'){
            setPassword(value);
        }else if(id === 'confirmPassword'){
            setConfirmPassword(value);
        }
    };

    const handleRedefinirSenha = async () => {
        setError('');
        if(password !== confirmPassword){
            setError('Senhas não conferem');
            return;
        }
        if(token){
            try{
                await redefinirSenha(token, password, confirmPassword);

                <Navigate to="/login" />
            }catch(error){
                setError(error.response.data.message);
            }
        }
    };

    const inputs = [
        {
            id: 'password',
            type: 'password',
            minLength: 8,
            placeholder: 'Digite sua senha',
            required: true,
            value: password,
            labelText: 'Digite sua senha',
            onChange: (e: React.ChangeEvent<HTMLInputElement>) => handleInputChange("password", e.target.value),
        },
        {
            id: 'confirmPassword',
            type: 'password',
            minLength: 8,
            placeholder: 'Digite novamente sua senha',
            required: true,
            value: confirmPassword,
            labelText: 'Digite novamente sua senha',
            onChange: (e: React.ChangeEvent<HTMLInputElement>) => handleInputChange("confirmPassword", e.target.value),
        },
    ];

    return(
        <Form
            input={inputs}
            text='Redefinir senha'
            onSubmit={handleRedefinirSenha}
            buttonText="Redefinir senha"
            buttonType="submit"
            errorMessage={error}
        />
    )
}