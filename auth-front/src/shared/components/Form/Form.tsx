import React from "react";
import { Input } from "../Input/Input";
import './Form.css'
import { Link } from "react-router-dom";

interface FormProps {
    input:{
        id: string;
        type: string;
        minLength?: number;
        placeholder: string;
        required?: boolean;
        value: string;
        labelText?: string;
        onChange: (event: React.ChangeEvent<HTMLInputElement>) => void;
    }[];
    onSubmit: (value: { [key: string]: string }) => void;
    buttonText?: string;
    buttonType?: "submit" | "reset" | "button";
    text: string;
    link: string;
    linkText: string;
    errorMessage?: string;
};

export const Form: React.FC<FormProps> = ({
    input, 
    onSubmit, 
    buttonText,
    buttonType,
    text,
    link,
    linkText,
    errorMessage
}) => {

    const handleSubmit = (event: React.FormEvent) => {
        event.preventDefault();
        const formData: { [key: string]: string } = {};

        input.forEach((field) => {
            formData[field.id] = field.value;
        });

        onSubmit(formData);
    };

    return(
        <div className='container'>
            <h2>{text}</h2>
            <form className='form' onSubmit={handleSubmit}>
                {input.map((field) =>(
                    <Input
                        key = {field.id}
                        id={field.id}
                        type={field.type}
                        minLength={field.minLength}
                        required={field.required}
                        value={field.value}
                        labelText={field.labelText}
                        onChange={field.onChange}
                        placeholder={field.placeholder}
                    />
                ))}
                <button className='button' type={buttonType}>{buttonText}</button>
            </form>
            {errorMessage && <p className='error-message' style={{ color: 'red' }}>{errorMessage}</p>}
            <p>{linkText} <Link to={link}>Clique aqui</Link></p>
        </div>
        
    );
};