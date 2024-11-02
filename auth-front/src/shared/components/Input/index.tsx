
interface InputProps {
    type: string;
}

export const Input: React.FC<InputProps> = ({type}) => {
    return(
        <input type={type}/>
    );
};