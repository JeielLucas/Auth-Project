import { useEffect, useRef, useState } from "react";
import { useAuth } from "../../shared/hooks/Auth";
import { useNavigate, useParams } from "react-router-dom";
import styles from './style.module.css'
import { AxiosError } from "axios";

export const ActivatePage = () => {
    const { ativarConta } = useAuth();
    const { token } = useParams<{ token: string }>();

    const [status, setStatus] = useState<"loading" | "success" | "error">("loading");
    const hasRun = useRef(false);
    const navigate = useNavigate();
    const [errorMessage, setErrorMessage] = useState("");

    useEffect(() => {
        if (!token) {
            setStatus("error");
            return;
        }

        if (hasRun.current) return;
        hasRun.current = true;

        const activateAccount = async () => {
            try {
                await ativarConta(token);
                setStatus("success");

                setTimeout(() => {
                    navigate("/login");
                }, 4000);
            } catch (error: unknown) {
                if (error instanceof AxiosError) {
                    setErrorMessage(error.response?.data.message || "Erro desconhecido");
                    setStatus("error");
                } else {
                    setStatus("error");
                }
            }
        };

        activateAccount();

    }, [token, ativarConta, navigate]);

    return (
        <div className={styles.div}>
            {status === "loading" && <div>Ativando conta...</div>}
            {status === "success" && <div>Conta ativada com sucesso! Você será redirecionado para página de login.</div>}
            {status === "error" && <div>Erro ao ativar conta. {errorMessage}.</div>}
        </div>
    );
};
