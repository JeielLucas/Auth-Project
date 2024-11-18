import styles from './Dashboard.module.css'

export const DashboardPage = () => {
    return(
        <div className={styles.container}>
            <h2 className={styles.teste}>Dashboard page</h2>
            <p>Bem vindo(a), você está numa página de acesso privado.</p>
        </div>
    );
};