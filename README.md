# Projeto de Autenticação - Spring Boot + React + TypeScript + Vite

Este projeto é uma aplicação web que utiliza **Spring Boot** no backend (Java 17) e **React**, **TypeScript** e **Vite** no frontend. Ele oferece um sistema de autenticação com funcionalidades de login, social login, register, logout, redefinição de senha, ativação de conta e validação de token via cookies.

## Tecnologias Utilizadas

### Containerização:
- Docker

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
- Registro: Usuários podem se registrar fornecendo e-mail, confirmação de e-mail, senha e confirmação de senha.
- Login: Usuários podem fazer login com e-mail e senha. Um token JWT é gerado e enviado via cookies.
- Social Login: Usuários podem fazer login com sua conta do google.
- Logout: Funcionalidade para sair da sua conta, removendo os tokens inseridos nos cookies.
- Ativação de Conta: Usuários precisam ativar sua conta após o registro.
- Redefinir Senha: Usuários podem solicitar a redefinição de senha via e-mail.
- Validação de Token: Verifica se o token de autenticação é válido. O token é enviado via cookies.
- Ping: Endpoint para testar a conectividade com o servidor.

## Endpoints da API (Backend)

### `Documentação da API`
- **`GET /swagger-ui/index.html`**  
  Rota para página de documentação da api.

- **`GET /api/v2/auth/ping`**  
  Testa a conectividade com o servidor. Não requer corpo na requisição.

- **`POST /api/v2/auth/register`**  
  Realiza o registro de um novo usuário. O corpo da requisição deve conter:
  ```json
  {
    "email": "user@example.com",
    "confirmEmail": "user@example.com",
    "password": "userPassword",
    "confirmPassword": "userPassword"
  }
  ```

- **`POST /api/v2/auth/login`**  
  Realiza o login do usuário. O corpo da requisição deve conter:
  ```json
  {
    "email": "user@example.com",
    "password": "userPassword"
  }
  ```

- **`POST /api/v2/auth/login/social/google?token=token`**  
  Realiza o login do usuário com o google. Token do google deve ser enviado na rota.
    **Exemplo de URL**:  
  `POST /api/v2/auth/login/google?token=token1234b`  


- **`POST /api/v2/auth/logout`**  
  Remove o access_token e refresh_token dos cookies, fazendo o logout do usuário.

- **`PATCH  /api/v2/auth/activate?token=token`**  
  Ativa a conta do usuário. O token de ativação é enviado por e-mail, e o usuário deve acessar o link para ativar sua conta.

- **`POST /api/v2/auth/forgot-password?email=email`**  
  Envia um e-mail de redefinição de senha para o endereço fornecido. O e-mail deve ser enviado como parâmetro na URL, não sendo necessário incluir um corpo na requisição.  
  
  **Exemplo de URL**:  
  `POST /api/v2/auth/reset-password?email=user@example.com`  
  
  O servidor irá verificar se o e-mail fornecido está registrado e ativo, em caso afirmativo, enviará um e-mail com as instruções para redefinir a senha.

- **`PATCH /api/v2/auth/reset-password?token=token`**  
Altera a senha do usuário. O token para redefinir a senha é enviado para o e-mail do usuário, após a solicitação de redefinição de senha. O token estará incluído no link enviado ao e-mail. O usuário deve acessar a URL fornecida no e-mail para obter o token.

O corpo da requisição deve conter:
  ```json
  {
    "password": "newPassword",
    "confirmPassword": "newPassword"
  }
  ```

  - **`GET /api/v2/auth/check`**  
  Valida o token de autenticação do usuário. Não requer corpo na requisição (o token será enviado via cookies).

## Como Executar o Projeto

1. Clone o repositório:
  ```bash
  git clone https://github.com/JeielLucas/To-Do-api.git
  ```
2. Navegue até a pasta do projeto.
```
cd Auth-Project
```
3. Na pasta frontend, altere o arquivo .env, adicionando seu Client Id do google Cloud no campo de VITE_GOOGLE_CLIENT_ID.
Exemplo:
```
VITE_GOOGLE_CLIENT_ID=123Token
```
4. Na pasta backend, altere o arquivo .env, adicionando seu Client Id e Client Secret do google cloud nos campos CLIENT_ID, CLIENT_SECRET, respectivamente. Também adicione seu email e senha de app do google nos campos MAIL_USER, MAIL_PASSWORD, respectivamente.
Exemplo:
```
CLIENT_ID=123
CLIENT_SECRET=secret123
MAIL_USER=user@email.com
MAIL_PASSWORD=123Password
```

### Com Docker Compose

1. Certifique-se de ter o Docker e Docker Compose instalados em sua máquina 
2. Na raiz do projeto, rode o comando:
```
docker-compose up -d --build
```
3. Verifique se o container está rodando, utilize o seguinte comando:
```
docker-compose ps
```
4. Acesse o frontend pelo navegador em http://localhost:8081. O backend estará disponível em http://localhost:8080

### Manualmente
### Backend (Spring Boot)
1. Navegue até a pasta do backend.
```
cd backend
```

2. Execute o comando abaixo para rodar o backend:
  ```bash
  mvn spring-boot:run
  ```

### Frontend (React + TypeScript + Vite)
1. Navegue até a pasta do frontend.
```
cd frontend
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
- "Email já cadastrado": Quando o e-mail fornecido no cadastro já está registrado no sistema.
- "Email não cadastrado": Quando o e-mail fornecido no login não é encontrado no sistema.
- "Usuário inativo, por favor, verifique seu email": Quando o usuário não está ativo, ou seja, não concluiu o processo de ativação da conta.
- "Usuário já ativo": Quando o usuário já foi ativado anteriormente.
- "Credenciais inválidas": Quando a senha fornecida no login não corresponde à senha registrada.
- "Nova senha não pode ser igual à antiga": Quando a nova senha fornecida no processo de redefinição de senha é igual à senha antiga.
- "Refresh token não encontrado": Quando o access_token está ausente ou expirado, retorna essa mensage se o refresh token estivar ausente na hora de gerar um novo access_token.
- "Token invalido ou expirado": Quando a verificação dos tokens JWT falha devido tokens inválidos ou corrompidos.
- "Token inválido": Quando o token fornecido para ativação ou redefinição de senha é inválido (não foi gerado para o usuário).
- "Token expirado": Quando o token fornecido para ativação ou redefinição de senha expirou.
- "Token do tipo incorreto": Quando o token fornecido não corresponde ao tipo esperado (como activation ou reset-password).
- "Usuário não encontrado": O refresh token utilizado para gerar um novo access token não corresponde a nenhum usuário.


#### Estilo de resposta
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
  - "Token validado com sucesso": Quando o access_token é validado.
  - "Refresh token validado com sucesso": Quando o refresh_token é validado.
  - "Cookies limpos com sucesso": Quando o usuário faz logout.
#### Estilo de resposta
```
{
	"success": true,
	"data": "",
	"message": "Login efetuado com sucesso"
}
```

- **Exemplo de mensagens de redirecionamento**:

  - "Usuário precisa se cadastrar": Quando o usuário realiza login via google mas não está cadastrado, precisa se cadastrar para conseguir fazer o login.
#### Estilo de resposta
```
{
	"success": false,
	"data": {
    "email": "email@gmail.com"
    },
	"message": "Usuario precisa se cadastrar"
}
```



