import { Navigate, Route, Routes, BrowserRouter} from "react-router-dom";
import { DashboardPage, LoginPage, RegisterPage} from "../pages";
import { ProtectedRoutes } from "./ProtectedRoutes";

export const Router = () =>{

    return(
        <BrowserRouter>
            <Routes>

                <Route path="/login" element={<LoginPage />}/>
                
                <Route path="/register" element={<RegisterPage />}/>

                <Route path="/dashboard"
                    element={
                        <ProtectedRoutes>
                            <DashboardPage />
                        </ProtectedRoutes>
                    }
                 />
                
                <Route path="*" element={<Navigate to="/login" />} />

                

            </Routes>
        </BrowserRouter>
    );
};