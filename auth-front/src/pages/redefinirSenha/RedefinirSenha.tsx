import { useState } from "react";
import { Form } from "../../shared/components/Form/Form";
import { useAuth } from "../../shared/hooks/Auth";
import { useNavigate } from "react-router-dom";

export const RedefinirSenha = () => {
    const [password, setPassword] = useState('');
    const [confirmPassword, setConfirmPassword] = useState('');
    const [error, setError] = useState('');
    const { redefinirSenha } = useAuth();
    const navigate = useNavigate();

    const handleTrocarSenha = async (formData: { [key: string]: string}) => {
        setError('');

        if(!(password === confirmPassword) || password.length < 8){
            setError('Senha inválida (diferentes ou tamanho menor que 8 caracteres)');
            return;
        }

        const token = new URLSearchParams(window.location.search).get('token')

        if(token){
            try{
                await redefinirSenha(token, formData.password, formData.confirmPassword);
                navigate('/login')
            }catch(error: any){
                setError(error.message);
            }
        }
    };

    
    const handleInputChange = (id: string, value: string) => {
        if(id === "password"){
            setPassword(value)
        }else if(id === "confirmPassword"){
            setConfirmPassword(value)
        }
    }

    const links = [
        {
            descriptionText: 'Já tem uma conta? ',
            redirectLink: '/login',
            linkLabel: 'Faça login',
        },
        {
            descriptionText: 'Não tem uma conta? ',
            redirectLink: '/register',
            linkLabel: 'Registre-se',
        },
    ]

    const inputs = [
        {
            id: 'password',
            type: 'password',
            placeholder: 'Digite sua senha',
            required: true,
            value: password,
            labelText: 'Digite sua senha',
            onChange: (e: React.ChangeEvent<HTMLInputElement>) => handleInputChange("password", e.target.value),
        },
        {
            id: 'confirmPassword',
            type: 'password',
            placeholder: 'Confirme sua senha',
            required: true,
            value: confirmPassword,
            labelText: 'Confirme sua senha',
            onChange: (e: React.ChangeEvent<HTMLInputElement>) => handleInputChange("confirmPassword", e.target.value),
        },
    ]

    return(
        <div>
            <Form
                text='Redefinir senha' 
                input={inputs} 
                onSubmit={handleTrocarSenha} 
                buttonText="Redefinir senha"
                buttonType="submit"
                links={links}
                errorMessage={error}
            />
        </div>
    );
};