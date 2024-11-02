import { Link } from "react-router-dom";
import { useState } from "react";
import { Form } from "../../shared/components/Form/Form";


export const RegisterPage = () => {
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [confirmEmail, setConfirmEmail] = useState('');
    const [confirmPassword, setConfirmPassword] = useState('');


    const handleRegistrar = (formData: { [key:string]: string}) => { // Fazer manipulação dos dados e enviar para o back
        console.log("Email: ", formData.email);
        console.log("ConfirmEmail: ", formData.confirmEmail);
        console.log("Password: ", formData.password);
        console.log("ConfirmPassword: ", formData.confirmPassword);
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

    return(
        <div>
            Register page
            <Form 
            input={inputs} 
            onSubmit={handleRegistrar} 
            buttonText={"Entrar"} 
            buttonType={"submit"}
            />
            <p>Já tem uma conta? <Link to="/login">Clique aqui</Link></p>
        </div>
    );
};