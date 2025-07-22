## Sistema de Gestão de Espaços Acadêmicos

Este é um sistema cliente-servidor para gerenciar espaços acadêmicos (salas de aula, laboratórios, auditórios, etc.) com funcionalidades de login, cadastro de usuários e espaços, reserva de espaços e relatórios.

### Funcionalidades:

- **Login e Registro de Usuários:**
  - Usuários podem se registrar como `ADMIN` ou `COMUM`.
  - Autenticação de usuários.
- **Gestão de Espaços (apenas para ADMINs):**
  - Cadastro de diferentes tipos de espaços (Sala de Aula, Laboratório, Sala de Reunião, Quadra Esportiva, Auditório).
  - Remoção de espaços (com validação para reservas ativas).
  - Listagem de espaços.
- **Gestão de Reservas:**
  - Fazer novas reservas para espaços disponíveis.
  - Cancelar reservas existentes.
  - Verificação de disponibilidade de espaços.
  - Listagem de minhas reservas.
- **Relatórios (apenas para ADMINs):**
  - Geração de relatórios sobre espaços e reservas.
- **Arquitetura Cliente-Servidor:**
  - Múltiplos clientes podem se conectar ao servidor simultaneamente.
  - Comunicação via Sockets e Threads.
- **Persistência de Dados:**
  - Todos os dados (usuários, espaços, reservas) são armazenados em arquivos `.txt` no diretório `data/` do servidor.

### Como Compilar e Executar:

**Pré-requisitos:**
- Java Development Kit (JDK) 17 ou superior instalado.

**1. Descompacte o Projeto:**
Descompacte o arquivo `sistema_espacos_funcional_final.zip` em uma pasta de sua preferência.

**2. Abra o Terminal/Prompt de Comando:**
Navegue até a pasta raiz do projeto (onde está a pasta `sistema-espacos`).

**3. Compile o Código:**
Utilize o seguinte comando para compilar o projeto. Este comando garante compatibilidade e habilita recursos de preview se necessário:

```bash
javac --release 17 --enable-preview -d build/classes $(find sistema-espacos/src/main/java -name '*.java')
```

*   `--release 17`: Garante que o código seja compilado para a versão 17 do Java, que é uma versão LTS (Long Term Support) e amplamente compatível.
*   `--enable-preview`: Habilita recursos de preview do Java, caso alguma funcionalidade mais recente esteja sendo utilizada (embora o código atual não deva depender disso, é uma boa prática para evitar erros em JDKs muito novos).
*   `-d build/classes`: Define o diretório de saída para os arquivos `.class`.
*   `$(find sistema-espacos/src/main/java -name '*.java')`: Encontra todos os arquivos `.java` dentro da estrutura de pastas do projeto.

**4. Inicie o Servidor:**
Em um terminal separado, execute o servidor:

```bash
java --enable-preview -cp build/classes br.edu.espacos.server.EspacosServer
```

*   `--enable-preview`: Necessário se o servidor usar alguma funcionalidade de preview.
*   `-cp build/classes`: Adiciona o diretório `build/classes` ao classpath.
*   `br.edu.espacos.server.EspacosServer`: Classe principal do servidor.

**5. Inicie o Cliente (Interface Gráfica):**
Em outro terminal, execute o cliente:

```bash
java --enable-preview -cp build/classes br.edu.espacos.App
```

*   `--enable-preview`: Necessário se o cliente usar alguma funcionalidade de preview.
*   `-cp build/classes`: Adiciona o diretório `build/classes` ao classpath.
*   `br.edu.espacos.App`: Classe principal da aplicação cliente.

### Credenciais Padrão:

- **Email:** `admin@sistema.com`
- **Senha:** `admin123`

Este usuário é do tipo `ADMIN` e pode cadastrar/remover espaços e gerar relatórios.

### Estrutura de Pastas:

```
sistema-espacos/
├── src/
│   └── main/
│       └── java/
│           └── br/
│               └── edu/
│                   └── espacos/
│                       ├── auth/             # Classes de autenticação
│                       ├── client/           # Classes do cliente (comunicação com servidor)
│                       ├── model/            # Classes de modelo de dados (Espaço, Usuário, Reserva, etc.)
│                       ├── server/           # Classes do servidor (lógica de negócio, handlers)
│                       ├── storage/          # Classes de persistência de dados (TXT)
│                       └── view/             # Classes da interface gráfica (JFrame)
├── data/               # Diretório onde os arquivos TXT de dados são armazenados (criado automaticamente)
└── README.md           # Este arquivo
```

### Solução de Problemas:

- **`java.lang.ArrayIndexOutOfBoundsException`:** Verifique se o servidor está rodando antes de iniciar o cliente. Certifique-se de que os dados nos arquivos `.txt` (em `data/`) não estão corrompidos. Se sim, você pode tentar apagar os arquivos `.txt` para que sejam recriados vazios na próxima inicialização do servidor.
- **Problemas de Conexão:** Verifique se o firewall não está bloqueando a porta `12346`. Certifique-se de que o servidor está realmente ativo e escutando na porta correta.
- **Erros de Compilação `implicitly declared classes`:** Este erro é tratado pelo comando de compilação fornecido. Certifique-se de estar usando um JDK 17+ e o comando `javac` exatamente como especificado.


