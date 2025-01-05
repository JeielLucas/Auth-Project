import { useNavigate } from 'react-router-dom';
import { useAuth } from '../../shared/hooks/Auth';
import styles from './Dashboard.module.css'


export const DashboardPage = () => {
    const { logout } = useAuth();
    const navigate = useNavigate();


    const handleLogout = async () => {
        await logout();
        navigate('/login')
    }
    return(
        <div className={styles.container}>
            <div className={styles.text}>
                <h2>Dashboard page</h2>
                <p>Bem vindo(a), você está numa página de acesso privado.</p>
            </div>
           
            <button className={styles.button} onClick={handleLogout}>Log out</button>
        </div>
    );
};