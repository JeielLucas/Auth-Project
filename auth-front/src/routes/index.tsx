import { Navigate, Route, Routes, BrowserRouter} from "react-router-dom";

import { DashboardPage, LoginPage, RegisterPage } from "../pages";

export const Router = () =>{
    return(
        <BrowserRouter>
            <Routes>

                <Route path="/dashboard" element={<DashboardPage />} />
                <Route path="/login" element={<LoginPage />}/>
                <Route path="/register" element={<RegisterPage />}/>

                <Route path="*" element={<Navigate to="/dashboard" />} />
            </Routes>
        </BrowserRouter>
    );
};