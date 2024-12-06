import { useEffect, useRef, useState } from "react";
import { useAuth } from "../../shared/hooks/Auth";
import { useNavigate, useParams } from "react-router-dom";

export const ActivatePage = () => {
    const { ativarConta } = useAuth();
    const { token } = useParams<{token: string}>();

    const [status, setStatus] = useState<"loading" | "success" | "error">("loading");
    const hasRun = useRef(false);
    const navigate = useNavigate();

    useEffect(() => {
        if (!token) {
            console.log("Token não encontrado na URL");
            setStatus("error");
            return;
        }

        if (hasRun.current) return;
        hasRun.current = true;

        (async () => {     
            try {
                await ativarConta(token);
                setStatus("success");
                setTimeout(() => {
                    navigate("/login");
                }, 5000)
            } catch (error) {
                console.error("Erro ao ativar conta:", error);
                setStatus("error");
            }
        })();

    }, [token, ativarConta, navigate]);
    
    const divStyle = {
        display: "flex",
        justifyContent: "center",
        alignItems: "center",
        height: "100vh",
        textAlign: "center" as "center",
        fontSize: '1.2rem',
    };

     return (
        <div style={divStyle}>
            {status === "loading" && <div>Ativando conta...</div>}
            {status === "success" && <div>Conta ativada com sucesso! Você será redirecionado para página de login.</div>}
            {status === "error" && <div>Erro ao ativar conta. Por favor, tente novamente.</div>}
        </div>
    );
};
