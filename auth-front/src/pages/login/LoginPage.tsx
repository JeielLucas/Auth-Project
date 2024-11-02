import { Link } from "react-router-dom";
import { useState } from "react";
import { Form } from "../../shared/components/Form/Form";


export const LoginPage = () => {
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');


    const handleInputChange = (id: string, value: string) =>{
        if(id === "email"){
            setEmail(value)
        } else if(id === "password"){
            setPassword(value)
        }

    };

    const handleEntrar = (formData: { [key: string]: string}) => {
        console.log("Email: ", formData.email);
        console.log("Password: ", formData.password);
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
            labelText: 'Digite sua senha',
            onChange: (e: React.ChangeEvent<HTMLInputElement>) => handleInputChange("password", e.target.value),
        },
    ];



    return(
        <div>
            Login page
            <Form input={inputs} onSubmit={handleEntrar} buttonText="Entrar" buttonType="submit"/>
            <p>Não tem uma conta? <Link to="/register">Clique aqui</Link></p>
        </div>
    );
};