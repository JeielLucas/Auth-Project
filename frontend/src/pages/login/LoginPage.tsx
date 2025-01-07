import { Link, useNavigate } from "react-router-dom";
import { useState } from "react";
import { Form } from "../../shared/components/Form/Form";
import { useAuth } from "../../shared/hooks/Auth";
import { Modal } from "../../shared/components/Modal/Modal";
import { GoogleLogin, CredentialResponse } from "@react-oauth/google";
import { AxiosError } from "axios";

export const LoginPage = () => {
    const { login, sendResetPasswordEmail, loginGoogle } = useAuth();
    const [openModal, setOpenModal] = useState(false);
    const navigate = useNavigate();
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [loginError, setLoginError] = useState('');
    const [modalError, setModalError] = useState('');
    const [modalEmail, setModalEmail] = useState('');

 

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

        try{
            await login(formData.email, formData.password);
            navigate('/dashboard')
        }catch(error: unknown){
            if(error instanceof AxiosError){
                setLoginError(error.response?.data.message || "Erro desconhecido")
            }else{
                setLoginError("Erro desconhecido")
            }
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
        }catch(error: unknown){
            if(error instanceof AxiosError){
                setModalError(error.response?.data.message || "Erro desconhecido")
            }else{
                setModalError("Erro desconhecido")
            }
        }
    }   

    const onClose = () =>{
        setModalError('');
        setLoginError('');
        setOpenModal(!openModal);
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

    const HandleLoginGoogle = async(credentialResponse: CredentialResponse) => {
        
        const token = credentialResponse?.credential;

        if (typeof token !== "string") {
            setLoginError("Credenciais inválidas");
            return;
        }
        
        try{
            const response = await loginGoogle(token);
            
            if(response?.status === 200){
                navigate("/dashboard");
            }

        }catch(error: unknown){
            if(error instanceof AxiosError){
                if ((error.response?.status === 401 || error.response?.status === 422) && error.response?.data.data?.email) {
                    navigate("/register");
                    return;
                }
                setLoginError(error.response?.data.message || "Erro desconhecido");
            }else{
                setLoginError("Erro desconhecido");
            }
        }
    }

    const auth = (
        <GoogleLogin
            onSuccess={HandleLoginGoogle}
            onError={() => {
                console.log('error')
            }}
        />  
    )

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
            auth={auth}
            />
            <Modal
                mensagem="Digite seu e-mail para redefinir senha"
                isOpen={openModal}
                onClose={onClose}
                input={{
                        id: 'emailModal',
                        type: 'email',
                        placeholder: 'Digite seu e-mail',
                        value: modalEmail,
                        onChange: (e: React.ChangeEvent<HTMLInputElement>) => setModalEmail(e.target.value),
                    }}
                button={{
                        text: 'Redefinir',
                }}
                errorMessage={modalError}
                onButtonClick={() => handleRedefinirSenha(modalEmail)}
            />
        </div>
    );
};
