import { useEffect, useState } from "react";
import { Navigate } from "react-router-dom";
import { useAuth } from "../shared/hooks/Auth";

interface ProtectedRoutesProps{
    children: React.ReactNode;
}

export const ProtectedRoutes = ( {children}: ProtectedRoutesProps) =>{
    const { tokenVerification } = useAuth();
    const [isAuthenticated, setIsAuthenticated] = useState<boolean | null>(null);


    useEffect(() =>{
        const validarToken = async () =>{
            try{
                const response = await tokenVerification();
                setIsAuthenticated(response.isAuthenticated);
            }catch(error){
                console.log(error);
                setIsAuthenticated(false);
            }
        };

        validarToken();

    }, [tokenVerification]);

    return (
        isAuthenticated === null
            ? <div>Loading...</div>
            : isAuthenticated
                ? children 
                : <Navigate to="/login" />
    );

}