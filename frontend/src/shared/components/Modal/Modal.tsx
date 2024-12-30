import React from "react";
import styles from './Modal.module.css';
import { Input } from "../Input/Input";

interface ModalBaseProps {
    mensagem: string;
    isOpen: boolean;
    onClose: () => void;
    botaoFechar?: boolean;
}

interface ModalWithInputProps extends ModalBaseProps {
    input?: {
        id: string;
        type: string;
        placeholder: string;
        value: string;
        onChange?: (event: React.ChangeEvent<HTMLInputElement>) => void;
    };
    button?: { text: string };
    errorMessage?: string;
    onButtonClick?: () => void;
}

type ModalProps = ModalBaseProps | ModalWithInputProps;

export const Modal: React.FC<ModalProps> = ({
    mensagem,
    isOpen,
    onClose,
    botaoFechar = true,
    ... rest  
}) => {
    const inputProps = rest as ModalWithInputProps;

    if (!isOpen) return null;

    return (
        <div className={styles.container}>        
            <div className={styles.modal}>
                <div className={styles.modalHeader}>
                    <p>{mensagem}</p>
                    {botaoFechar && (
                        <button onClick={onClose} className={styles.closeButton}>
                        X
                        </button>
                    )}
                </div>

                {inputProps.input && (
                <div>
                    <div className={styles.modalInputContainer}>
                        <Input
                            id={inputProps.input.id}
                            type={inputProps.input.type}
                            placeholder={inputProps.input.placeholder}
                            value={inputProps.input.value}
                            onChange={inputProps.input.onChange ?? (() => {})} 
                        />
                    </div>
                    <div className={styles.modalFooter}>
                        <p className={styles.errorMessage}>{inputProps.errorMessage}</p>
                        <button onClick={inputProps.onButtonClick} className={styles.button}>
                            {inputProps.button?.text}
                        </button>
                    </div>
                </div>
                )}
            </div>
        </div>
    );
};