import { useState } from "react";
import { Form } from "../../shared/components/Form/Form";
import { useAuth } from "../../shared/hooks/Auth";
import { useNavigate } from "react-router-dom";


export const EmailResetPassword = () =>{
    const [email, setEmail] = useState('');
    const [error, setError] = useState('');
    const { sendResetPasswordEmail } = useAuth();
    const navigate = useNavigate();

    const handleTrocarSenha = async (formData: {[key: string]: string}) => {
        try{
            await sendResetPasswordEmail(formData.email);
            navigate('/login')
        }catch(error: any){
            setError(error.message)
        }
    }

    const inputs = [
        {
            id: 'email',
            type: 'email',
            placeholder: 'Digite seu email',
            required: true,
            value: email,
            labelText: 'Digite seu email',
            onChange: (e: React.ChangeEvent<HTMLInputElement>) => setEmail(e.target.value),
        },
    ]

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
}