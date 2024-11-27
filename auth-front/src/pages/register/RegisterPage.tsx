import { useState } from "react";
import { Form } from "../../shared/components/Form/Form";
import { useAuth } from "../../shared/hooks/Auth";
import { Modal } from "../../shared/components/Modal/Modal";
import { Link } from "react-router-dom";


export const RegisterPage = () => {
    const { register } = useAuth();
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [confirmEmail, setConfirmEmail] = useState('');
    const [confirmPassword, setConfirmPassword] = useState('');
    const [error, setError] = useState('');
    const [openModal, setOpenModal] = useState(false);


    const isEmailValid = (email: string): boolean =>{
        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        return emailRegex.test(email);
    }

    const handleRegistrar = async (formData: { [key:string]: string}) => {
        setError('');

        if (!isEmailValid(email)) {
            setError('Formato de email inválido');
            return;
        }
        
        if (email !== confirmEmail) {
            setError('Emails não correspondem');
            return;
        }

        if(!(password === confirmPassword) || password.length < 8){
            setError('Senha inválida (diferentes ou tamanho menor que 8 caracteres)');
            return;
        }

        try{
            await register(formData.email, formData.confirmEmail, formData.password, formData.confirmPassword);
            setOpenModal(true);
        }catch(error){
            console.log("erro", error)
            if(error instanceof Error) {
                setError(error.message);
                return;
            }
            setError('Erro desconhecido ao tentar cadastrar, por favor, tente novamente mais tarde');
        }

    };

    const handleInputChange = (id: string, value: string) => {
        if(id === "email"){
            setEmail(value);
        }else if(id === "password"){
            setPassword(value)
        }else if(id === "confirmEmail"){
            setConfirmEmail(value)
        }else if(id === "confirmPassword"){
            setConfirmPassword(value)
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
            id: 'confirmEmail',
            type: 'email',
            placeholder: 'Confirme seu email',
            required: true,
            value: confirmEmail,
            labelText: 'Confirme seu email',
            onChange: (e: React.ChangeEvent<HTMLInputElement>) => handleInputChange("confirmEmail", e.target.value),
        },
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
    ];

    const links = [
        <p key="register">
            Já tem uma conta? <Link to='/login'>Faça seu login</Link>
        </p>
    ];

    return(
        <div className='divRegister'>
            <Form
            text='Register page'
            input={inputs} 
            onSubmit={handleRegistrar} 
            buttonText={"Entrar"} 
            buttonType={"submit"}
            errorMessage={error}
            links={links}
            />
            <Modal
                mensagem="E-mail de confirmação enviado, por favor, ative sua conta para usá-la!"
                isOpen={openModal}
                setModalOpen={() => setOpenModal(!openModal)}
            />
        </div>
    );
};