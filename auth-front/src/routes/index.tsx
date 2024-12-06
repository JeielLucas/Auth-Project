import { Navigate, Route, Routes, BrowserRouter } from "react-router-dom";
import { DashboardPage, LoginPage, RegisterPage, ActivatePage, PasswordPage } from "../pages";
import { ProtectedRoutes } from "./ProtectedRoutes";

export const Router = () => {
    return (
        <BrowserRouter>
            <Routes>
                <Route path="/login" element={<LoginPage />} />
                <Route path="/register" element={<RegisterPage />} />
                <Route path="/redefinir-senha/:token" element={<PasswordPage />} />
                <Route
                    path="/dashboard"
                    element={
                        <ProtectedRoutes>
                            <DashboardPage />
                        </ProtectedRoutes>
                    }
                />
                <Route path="/ativar-conta/:token" element={<ActivatePage />} />
                <Route path="*" element={<Navigate to="/login" />} />
            </Routes>
        </BrowserRouter>
    );
};
