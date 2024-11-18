import { useNavigate } from "react-router-dom";
import { useState } from "react";
import { Form } from "../../shared/components/Form/Form";
import { useAuth } from "../../shared/hooks/Auth";
import { Modal } from "../../shared/components/Modal/Modal";

export const LoginPage = () => {
    const { login } = useAuth();
    const [openModal, setOpenModal] = useState(false);
    const navigate = useNavigate();
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [error, setError] = useState('');

    const isEmailValid = (email: string): boolean =>{
        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        return emailRegex.test(email);
    }

    const handleInputChange = (id: string, value: string) =>{
        if(id === "email"){
            setEmail(value)
        } else if(id === "password"){
            setPassword(value)
        }

    };

    const handleEntrar = async (formData: { [key: string]: string}) => {
        setError('');

        if(!isEmailValid(email)){
            setError('Email inválido');
            return;
        }

        if(password.length < 8){
            setError('Senha inválida')
            return;
        }

        try{
            await login(formData.email, formData.password);
            navigate('/dashboard')
        }catch(error: any){
            setError(error.message);
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

    const links= [
        {
            descriptionText: 'Não tem uma conta? ',
            redirectLink: '/register',
            linkLabel: 'Registre-se',
        },
        {
            redirectLink: '/send-email-reset',
            linkLabel: 'Esqueceu sua senha?',
        },
    ]

    return(
        <div className='divLogin'>
            <Form 
            text='Login page' 
            input={inputs} 
            onSubmit={handleEntrar} 
            buttonText="Entrar"
            buttonType="submit"
            links={links}
            errorMessage={error}
            />
            <Modal
                mensagem="E-mail de confirmação enviado, por favor, ative sua conta para usá-la!"
                isOpen={openModal}
                setModalOpen={() => setOpenModal(!openModal)}
            />
        </div>
    );
};