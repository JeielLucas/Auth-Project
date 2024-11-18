import { Navigate, Route, Routes, BrowserRouter} from "react-router-dom";
import { DashboardPage, LoginPage, RegisterPage, RedefinirSenha, AtivarConta, EmailResetPassword } from "../pages";
export const Router = () =>{

    return(
        <BrowserRouter>
            <Routes>

                <Route path="/login" element={<LoginPage />}/>
                
                <Route path="/register" element={<RegisterPage />}/>
                
                <Route path="/dashboard"element={<DashboardPage />}/>

                <Route path="/ativar-conta" element={<AtivarConta />}/>

                <Route path="/send-email-reset" element={<EmailResetPassword />}/>
                
                <Route path="/redefinir-senha" element={<RedefinirSenha />}/>

                <Route path="*" element={<Navigate to="/login" />} />
            </Routes>
        </BrowserRouter>
    );
};