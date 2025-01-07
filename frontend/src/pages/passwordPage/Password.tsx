import { useState } from "react";
import { Form } from "../../shared/components/Form/Form";
import { useAuth } from "../../shared/hooks/Auth"
import { useNavigate, useParams } from "react-router-dom";
import { Modal } from "../../shared/components/Modal/Modal";
import { AxiosError } from "axios";


export const PasswordPage = () =>{
    const{ redefinirSenha } = useAuth();
    const [ password, setPassword ] = useState('');
    const [ confirmPassword, setConfirmPassword ] = useState('');
    const [ error, setError ] = useState('');
    const { token } = useParams<{token: string}>();
    const navigate = useNavigate();
    const [openModal, setOpenModal] = useState(false);

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
                setOpenModal(true);
                setTimeout(() =>{
                    navigate("/login");
                }, 5000);
            }catch(error: unknown){
                if(error instanceof AxiosError){
                    setError(error.response?.data.message || "Erro desconhecido")
                }else{
                    setError("Erro desconhecido")
                }
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
        <div>
            <Form
                input={inputs}
                text='Redefinir senha'
                onSubmit={handleRedefinirSenha}
                buttonText="Redefinir"
                buttonType="submit"
                errorMessage={error}
            />
            <Modal
                mensagem={"Senha alterada com sucesso! Redirecionando a página de login, aguarde."}
                isOpen={openModal}
                onClose={() => setOpenModal(!openModal)}
                botaoFechar={false}
            />
        </div>
    )
}
