import { Link, useNavigate } from "react-router-dom";
import { useState } from "react";
import { Form } from "../../shared/components/Form/Form";
import { useAuth } from "../../shared/hooks/Auth";
import { Modal } from "../../shared/components/Modal/Modal";

export const LoginPage = () => {
    const { login, sendResetPasswordEmail } = useAuth();
    const [openModal, setOpenModal] = useState(false);
    const navigate = useNavigate();
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [loginError, setLoginError] = useState('');
    const [modalError, setModalError] = useState('');

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
        setLoginError('');

        if(!isEmailValid(email)){
            setLoginError('Email inválido');
            return;
        }

        if(password.length < 8){
            setLoginError('Senha inválida')
            return;
        }

        try{
            await login(formData.email, formData.password);
            navigate('/dashboard')
        }catch(error: any){
            setLoginError(error.message);
        }
    };

    const handleRedefinirSenha = async (email: string) => {
        setModalError('');
        if(!isEmailValid(email)){
            setModalError('Email inválido');
            return;
        }
        try{
            await sendResetPasswordEmail(email);
            setModalError('Redefinição enviada com sucesso, por favor, verifique seu email')
        }catch(error: any){
             setModalError(error.message)
        }
    }

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

    const links = [
        <p key="register">
            Não tem uma conta? <Link to='/register'>Registre-se</Link>
        </p>,
        <p key="forgot-password">
            <span
                style={{ cursor: 'pointer', color: 'blue', textDecoration: 'underline' }}
                onClick={() => setOpenModal(!openModal)}>
                Esqueceu sua senha?
            </span>
        </p>
    ];

    return(
        <div>
            <Form 
            text='Login page' 
            input={inputs} 
            onSubmit={handleEntrar} 
            buttonText="Entrar"
            buttonType="submit"
            errorMessage={loginError}
            links={links}
            />
            <Modal
                mensagem="Digite seu e-mail para redefinir senha"
                input={{
                        id: 'email',
                        type: 'email',
                        placeholder: 'Digite seu e-mail',
                        value: email,
                        onChange: (e: React.ChangeEvent<HTMLInputElement>) => setEmail(e.target.value),
                    }}
                    button={{
                        text: 'Redefinir senha',
                    }}
                errorMessage={modalError}
                isOpen={openModal}
                setModalOpen={() => setOpenModal(!openModal)}
                onButtonClick={() => handleRedefinirSenha(email)}
            />
        </div>
    );
};