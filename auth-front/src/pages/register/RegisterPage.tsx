import { Link } from "react-router-dom";


export const RegisterPage = () => {
    return(
        <div>
            Register page
            <p>Já tem uma conta? <Link to="/login">Clique aqui</Link></p>
            <Link to="/dashboard">Voltar para página inicial</Link>
        </div>
    );
};