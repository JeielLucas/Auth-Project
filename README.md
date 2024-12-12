# Projeto de Autenticação - Spring Boot + React + TypeScript + Vite

Este projeto é uma aplicação web que utiliza **Spring Boot** no backend (Java 21) e **React**, **TypeScript** e **Vite** no frontend. Ele oferece um sistema de autenticação com funcionalidades de login, registro, redefinição de senha, ativação de conta e validação de token via cookies.

## Tecnologias Utilizadas

### Backend (Spring Boot):
- Java 17
- Spring Boot
- Spring Security
- JWT (JSON Web Tokens) para autenticação
- Cookies para armazenar tokens

### Frontend (React + TypeScript + Vite):
- React
- TypeScript
- Vite (build tool)
- Axios (para comunicação com o backend)

### Pré-requisitos
- Java 17 ou superior
- Maven 3.3.5 ou superior
- Node.js 16 ou superior

### Funcionalidades do Projeto
- Login: Usuários podem fazer login com e-mail e senha. Um token JWT é gerado e enviado via cookies.
- Registro: Usuários podem se registrar fornecendo e-mail, confirmação de e-mail, senha e confirmação de senha.
- Ativação de Conta: Usuários precisam ativar sua conta após o registro.
- Redefinir Senha: Usuários podem solicitar a redefinição de senha via e-mail.
- Validação de Token: Verifica se o token de autenticação é válido. O token é enviado via cookies.
- Ping: Endpoint para testar a conectividade com o servidor.

## Endpoints da API (Backend)

### `/api/v2/auth`
- **`GET /ping`**  
  Testa a conectividade com o servidor. Não requer corpo na requisição.

- **`POST /login`**  
  Realiza o login do usuário. O corpo da requisição deve conter:
  ```json
  {
    "email": "user@example.com",
    "password": "userPassword"
  }
  ```

- **`POST /register`**  
  Realiza o registro de um novo usuário. O corpo da requisição deve conter:
  ```json
  {
    "email": "user@example.com",
    "confirmEmail": "user@example.com",
    "password": "userPassword",
    "confirmPassword": "userPassword"
  }
  ```

- **`POST  /ativar-conta?token=token`**  
  Ativa a conta do usuário. O token de ativação é enviado por e-mail, e o usuário deve acessar o link para ativar sua conta.

- **`POST /reset-password?email=email`**  
  Envia um e-mail de redefinição de senha para o endereço fornecido. O e-mail deve ser enviado como parâmetro na URL, não sendo necessário incluir um corpo na requisição.  
  
  **Exemplo de URL**:  
  `POST /reset-password?email=user@example.com`  
  
  O servidor irá verificar se o e-mail fornecido está registrado e ativo, em caso afirmativo, enviará um e-mail com as instruções para redefinir a senha.

- **`POST /redefinir-senha?token=token`**  
Altera a senha do usuário. O token para redefinir a senha é enviado para o e-mail do usuário, após a solicitação de redefinição de senha. O token estará incluído no link enviado ao e-mail. O usuário deve acessar a URL fornecida no e-mail para obter o token.

O corpo da requisição deve conter:
  ```json
  {
    "password": "newPassword",
    "confirmPassword": "newPassword"
  }
  ```

  - **`GET /validar-token`**  
  Valida o token de autenticação do usuário. Não requer corpo na requisição (o token será enviado via cookies).

## Como Rodar o Projeto

1. Clone o repositório:
  ```bash
  git clone https://github.com/JeielLucas/To-Do-api.git
  ```
2. Navegue até a pasta do projeto.
```
cd Auth-Project
```
### Backend (Spring Boot)
1. Navegue até a pasta do backend.
```
cd api
```

2. Configurar as propriedades da aplicação:
Altere os campos spring.mail.username e spring.mail.password para o seu e-mail do Gmail e a senha gerada pela aplicação Google (senha de app), respectivamente. Caso prefira, você também pode utilizar variáveis de ambiente para definir essas informações de forma mais segura.

3. Execute o comando abaixo para rodar o backend:
  ```bash
  mvn spring-boot:run
  ```

### Frontend (React + TypeScript + Vite)
1. Navegue até a pasta do frontend.
```
cd auth-front
```
2. Instale as dependências com o comando:
  ```bash
  npm install
  ```
3. Para rodar o frontend em desenvolvimento, execute:
  ```bash
  npm run dev
  ```

### Observações
 - Confirmação de email pós cadastro para confirmar identidade.
- **`access_token`**: Um token JWT de curta duração usado para autenticação. Ele é enviado em cada requisição para proteger os endpoints da API. Esse token tem um tempo de expiração (30 minutos).
- **`refresh_token`**: Um token JWT de longa duração usado para obter um novo `access_token` quando o anterior expira. O `refresh_token` tem um tempo de expiração de 3 dias e é armazenado em um cookie seguro.
  
Todos os tokens são armazenados em cookies com a flag `HttpOnly` para prevenir ataques de XSS.

## Exemplo de respostas
- **Exemplo de mensagens de erro**:

- "Emails não coincidem": Quando o e-mail fornecido no processo de registro não é igual ao e-mail de confirmação.
- "Senhas não coincidem": Quando a senha fornecida não é igual à senha de confirmação no processo de registro ou redefinição de senha.
- "Email já cadastrado": Quando o e-mail fornecido já está registrado no sistema.
- "Email não cadastrado": Quando o e-mail fornecido no login não é encontrado no sistema.
- "Usuário inativo, por favor, verifique seu email": Quando o usuário não está ativo, ou seja, não concluiu o processo de ativação da conta.
- "Senha incorreta": Quando a senha fornecida no login não corresponde à senha registrada.
- "Token inválido": Quando o token fornecido para ativação ou redefinição de senha não é encontrado ou não é válido.
- "Token expirado": Quando o token fornecido para ativação ou redefinição de senha expirou.
- "Usuário já ativo": Quando o usuário já foi ativado anteriormente.
- "Token do tipo incorreto": Quando o token fornecido não corresponde ao tipo esperado (como activation ou reset-password).
- "Nova senha não pode ser igual à antiga": Quando a nova senha fornecida no processo de redefinição de senha é igual à senha antiga.
- "Falha na verificação do token": Quando a verificação do token JWT falha devido a um token inválido ou corrompido.

### Estilo de resposta
```
{
  "code": 401,
  "message": "Token expirado",
  "details": [
    "O token fornecido expirou. Solicite um novo token"
  ]
}
```

- **Exemplo de mensagens de sucesso**:

  - "Usuário criado com sucesso": Quando o usuário se registra.
  - "Login efetuado com sucesso": Quando o usuário faz o login.
  - "Conta ativada com sucesso": Quando o usuário ativa sua conta através do email.
  - "Verifique seu email": Quando o usuário solicita a troca de senha.
  - "Senha redefinida com sucesso": Quando o usuário troca sua senha.
  - "Token validado com sucesso": Quando o token é validado.
### Estilo de resposta
```
{
	"success": true,
	"data": "",
	"message": "Login efetuado com sucesso"
}
```



