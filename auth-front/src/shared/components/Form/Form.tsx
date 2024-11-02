import React from "react";
import { Input } from "../Input/Input";

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
};

export const Form: React.FC<FormProps> = ({
    input, 
    onSubmit, 
    buttonText,
    buttonType
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
        <form onSubmit={handleSubmit}>
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
            <button type={buttonType}>{buttonText}</button>
        </form>
    );
};