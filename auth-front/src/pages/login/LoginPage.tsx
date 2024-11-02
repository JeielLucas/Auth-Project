import { useNavigate } from "react-router-dom";
import { Link } from "react-router-dom"


export const LoginPage = () => {
    const navigate = useNavigate();

    const handleClick = (route: string) => { // Utilizar quando necessita de alguma lógica adicional (Como auth)
        navigate(route);
    };

    return(
        <div>
            Login page
            <p>Não tem uma conta? <Link to="/register">Clique aqui</Link></p> {/* Utilizar para link simples, sem lógica adicional*/}
            <button onClick={() => handleClick('/dashboard')}>Página inicial</button>
        </div>
    );
};