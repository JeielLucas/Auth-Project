package com.auth.api.services;

import com.auth.api.entities.User;
import com.auth.api.exceptions.EmailAlreadyExistsException;
import com.auth.api.exceptions.InvalidCredentialsException;
import com.auth.api.exceptions.MismatchException;
import com.auth.api.repositories.RegisterRequestDTO;
import com.auth.api.repositories.UserRepository;
import com.auth.api.repositories.LoginRequestDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public ResponseEntity<String> RegisterUser(RegisterRequestDTO user) {

        if(!user.email().equals(user.confirmEmail())){
            throw new MismatchException("Emails não coincidem");
        }

        if(!user.password().equals(user.confirmPassword())){
            throw new MismatchException("Senhas não coincidem");
        }

        if(userRepository.findByEmail(user.email()) != null) {
            throw new EmailAlreadyExistsException("Email já cadastrado");
        };


        User newUser = new User(user.email(), user.password());

        userRepository.save(newUser);

        return ResponseEntity.status(HttpStatus.CREATED).body("Usuário registado com sucesso");
    }

    public ResponseEntity<String> LoginUser(LoginRequestDTO user){
        User newUser = userRepository.findByEmail(user.email());

        if(newUser == null) {
            throw new InvalidCredentialsException("Email não cadastrado");
        }

        if(!user.password().equals(newUser.getPassword())) {
            throw new InvalidCredentialsException("Senha incorreta");
        }

        return ResponseEntity.status(HttpStatus.OK).body("Usuário logado com sucesso");
    }
}
