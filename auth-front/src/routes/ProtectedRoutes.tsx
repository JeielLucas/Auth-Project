import { useEffect, useRef, useState } from "react";
import { Navigate } from "react-router-dom";
import { useAuth } from "../shared/hooks/Auth";

interface ProtectedRoutesProps{
    children: React.ReactNode;
}

export const ProtectedRoutes = ( {children}: ProtectedRoutesProps) =>{
    const { tokenVerification } = useAuth();
    const [isAuthenticated, setIsAuthenticated] = useState<boolean | null>(null);
    const hasRun = useRef(false);


    useEffect(() =>{
        if(hasRun.current) return;
        hasRun.current = (true);
        
        const validarToken = async () =>{
            try{
                await tokenVerification();
                setIsAuthenticated(true);
            }catch(error){
                console.log(error.response.data.message);
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