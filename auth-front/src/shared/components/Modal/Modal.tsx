import React from "react";
import styles from './Modal.module.css'

interface ModalProps{
    mensagem: string;
    isOpen: boolean;
    setModalOpen: () => void;
}

export const Modal: React.FC<ModalProps> = ({mensagem, isOpen, setModalOpen}) =>{
    if(isOpen){
        return(
            <div className={styles.container}>
                <div className={styles.modal}>
                    <p className={styles.text}>{mensagem}</p>
                    <button onClick={setModalOpen} className={styles.button}>Fechar</button>
                </div>
            </div>
            
        )
    }
    
    return null;
}