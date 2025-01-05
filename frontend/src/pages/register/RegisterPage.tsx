import { useEffect, useState } from "react";
import { Form } from "../../shared/components/Form/Form";
import { useAuth } from "../../shared/hooks/Auth";
import { Modal } from "../../shared/components/Modal/Modal";
import { Link, useNavigate } from "react-router-dom";
import { useDispatch, useSelector } from "react-redux";
import { RootState } from "../../redux/store";
import { CredentialResponse, GoogleLogin } from "@react-oauth/google";
import { AxiosError } from "axios";
import { setEmailData } from "../../redux/authSlice";


export const RegisterPage = () => {
    const { register, loginGoogle } = useAuth();
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [confirmEmail, setConfirmEmail] = useState('');
    const [confirmPassword, setConfirmPassword] = useState('');
    const [error, setError] = useState('');
    const [openModal, setOpenModal] = useState(false);
    const emailData = useSelector((state: RootState) => state.auth.emailData);
    const navigate = useNavigate();
    const dispatch = useDispatch();

    useEffect(() => {
        if (emailData) {
            setEmail(emailData);
            setConfirmEmail(emailData)
            dispatch(setEmailData(''))
        }
    }, [dispatch, emailData]);

    const isEmailValid = (email: string): boolean =>{
        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        return emailRegex.test(email);
    }

    const handleRegistrar = async (formData: { [key:string]: string}) => {
        setError('');

        if (!isEmailValid(email) || !isEmailValid(confirmEmail)) {
            setError('Formato de email inválido');
            return;
        }
        
        if (email !== confirmEmail) {
            setError('Emails não correspondem');
            return;
        }

        if(password !== confirmPassword || password.length < 8){
            setError('Senha inválida (diferentes ou tamanho menor que 8 caracteres)');
            return;
        }

        try{
            await register(formData.email, formData.confirmEmail, formData.password, formData.confirmPassword);
            setOpenModal(true);
            setEmail('');
            setConfirmEmail('');
            setPassword('');
            setConfirmPassword('');
        }catch(error: unknown){
            if(error instanceof AxiosError){
                setError(error.response?.data.message || "Erro desconhecido")
            }else{
                setError("Erro desconhecido")
            }
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

    const HandleLoginGoogle = async(credentialResponse: CredentialResponse) => {

        const token = credentialResponse?.credential;

        if (typeof token !== "string") {
            setError("Credenciais inválidas");
            return;
        }

        try{
            const response = await loginGoogle(token);

            if(response.status === 200){
                navigate("/dashboard");
            }

        }catch(error: unknown){
            if(error instanceof AxiosError){
                if(error.response?.status === 422 && emailData){
                    setEmail(emailData);
                    setConfirmEmail(emailData)
                }
                setError(error.response?.data.message || "Erro desconhecido");
            }else{
                setError("Erro desconhecido");
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
        <div className='divRegister'>
            <Form
            text='Register page'
            input={inputs} 
            onSubmit={handleRegistrar} 
            buttonText={"Cadastrar"} 
            buttonType={"submit"}
            errorMessage={error}
            links={links}
            auth={auth}
            />
            <Modal
                mensagem="E-mail de confirmação enviado, por favor, ative sua conta para usá-la!"
                isOpen={openModal}
                onClose={() => setOpenModal(!openModal)}
            />
        </div>
    );
};
