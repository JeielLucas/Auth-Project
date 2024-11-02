import { Link } from "react-router-dom";


export const DashboardPage = () => {
    return(
        <div>
            Dashboard page
            <p>Já tem uma conta? <Link to="/login">Clique aqui</Link></p>
            <p>Não tem uma conta? <Link to="/register">Clique aqui</Link></p>
        </div>
    );
};