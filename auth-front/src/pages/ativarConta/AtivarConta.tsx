import { useNavigate } from "react-router-dom";
import { useAuth } from "../../shared/hooks/Auth";
import { useEffect } from "react";

export const AtivarConta = () => {
    const { ativarConta } = useAuth();
    const navigate = useNavigate();

    useEffect(() => {
        window.onload = () => {
            const token = new URLSearchParams(window.location.search).get('token');
            if (token) {
                handleAtivarConta(token);
            } else {
                alert('Token não encontrado');
            }
        };
    }, []);

    const handleAtivarConta = async (token: string) => {
        try {
            await ativarConta(token);
            navigate('/dashboard');
        } catch (error) {
            alert('Erro ao ativar a conta: ' + error);
        }
    };

    return (
        <div>
            Ativando conta...
        </div>
    );
};
