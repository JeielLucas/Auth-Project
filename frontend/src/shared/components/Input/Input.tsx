import './Input.css'

interface InputProps {
    id: string;
    type: string;
    minLength?: number;
    placeholder: string;
    required?: boolean;
    value: string;
    labelText?: string;
    onChange: (event: React.ChangeEvent<HTMLInputElement>) => void;
}

export const Input: React.FC<InputProps> = ({type, minLength, placeholder, required, value, onChange, id, labelText}) => {
    return(
        <div className='divInput'>
            <label className='label' htmlFor={id}>{labelText}</label>
            <input
                className='input'
                id={id}
                type={type} 
                minLength={minLength} 
                placeholder={placeholder} 
                required={required}
                value={value}
                onChange={onChange}
            />
        </div>
       
    );
};