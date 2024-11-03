import requests

def buscar_dados_api():
    url = "https://pv-credenciamento-api-live.tsuru2.maida.health/rede-atendimento/prestadores/vinculados"
    params = {
        "bairro": "",
        "idsEspecialidadesApi": "25",
        "categoria": "CREDENCIADO",
        "buscaEstendida": "true",
        "page": 0,
        "size": 5
    }

    try:
        response = requests.get(url, params=params)
        response.raise_for_status()  # Levanta um erro para códigos de status 4xx e 5xx

        # Supondo que a resposta seja em JSON
        dados = response.json()

        # Organizando os dados (exemplo)
        prestadores = []
        for item in dados.get("content", []):
            prestador = {
                "nome": item.get("nome"),
                "especialidade": item.get("especialidade"),
                "endereco": item.get("endereco"),
                "telefone": item.get("telefone"),
                "email": item.get("email")
            }
            prestadores.append(prestador)

        return prestadores

    except requests.exceptions.RequestException as e:
        print(f"Erro ao buscar dados da API: {e}")
        return []

def main():
    prestadores = buscar_dados_api()
    if prestadores:
        print("Prestadores encontrados:")
        for prestador in prestadores:
            print(prestador)

if __name__ == "__main__":
    main()
