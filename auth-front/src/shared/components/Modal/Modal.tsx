import React from "react";
import styles from './Modal.module.css';

interface ModalProps {
    mensagem: string;
    isOpen: boolean;
    setModalOpen: () => void;
    input?: {
        id: string;
        type: string;
        placeholder?: string;
        value: string;
        onChange?: (event: React.ChangeEvent<HTMLInputElement>) => void;
    };
    button?: {
        text: string;
    }
    onButtonClick?: () => void;
    errorMessage?: string;
    botaoFechar: boolean;
}

export const Modal: React.FC<ModalProps> = ({ mensagem, isOpen, setModalOpen, input, button, onButtonClick, errorMessage, botaoFechar }) => {

    const handleInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        if (input?.onChange) {
            input.onChange(e);
        }
    };

    if (isOpen) {
        return (
            <div className={styles.container}>
            <div className={styles.modal}>
                <div className={styles.buttonDiv}>
                    { botaoFechar == true ? (
                        <button onClick={setModalOpen} className={styles.button}>
                            Fechar
                        </button>
                        ) : (<></>)
                    }
                </div>
                <p className={styles.text}>{mensagem}</p>
                {input && (
                    <form className={styles.form}>
                        <input
                            id={input.id}
                            type={input.type}
                            placeholder={input.placeholder}
                            value={input.value}
                            onChange={handleInputChange}
                            className={styles.input}
                        />
                    </form>
                )}
                {errorMessage && <p className={styles.errorMessage} style={{ color: 'red' }}>{errorMessage}</p>}
                {button && (
                    <button onClick={onButtonClick} className={styles.button}>
                        {button.text}
                    </button>
                )}
            </div>
        </div>        
        );
    }

    return null;
};
