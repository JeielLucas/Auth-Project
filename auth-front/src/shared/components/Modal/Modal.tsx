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
        const hasInput = (props: ModalWithInputProps): boolean => 'input' in props;

        if (!isOpen) return null;

        return (
            <div className={styles.container}>
                
                <div className={styles.modal}>
                    <div className={styles.modalHeader}>
                        <p>{mensagem}</p>
                        {botaoFechar && (
                            <button onClick={onClose} className={styles.closeButton}>
                              Fechar
                          </button>
                        )}
                    </div>

                    {hasInput(rest as ModalWithInputProps) && (
                        <div>
                            <div className={styles.modalInputContainer}>
                                <Input
                                    id={rest.input.id}
                                    type={rest.input.type}
                                    placeholder={rest.input.placeholder}
                                    value={rest.input.value}
                                    onChange={rest.input.onChange}
                                />
                            </div>
                            <div className={styles.modalFooter}>
                                <p className={styles.errorMessage}>{rest.errorMessage}</p>
                                <button onClick={rest.onButtonClick} className={styles.button}>
                                    {rest.button.text}
                                </button>
                            </div>
                        </div>
                    )}
                </div>
            </div>
        );
    };
