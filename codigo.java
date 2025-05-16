/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package mp1_2024114213_2024107621;

import java.util.*;

/**
 * Jogo de Desminagem (Minesweeper) em consola. Projeto MP1 - Fundamentos de
 * Programação Orientada a Objetos 2024/2025
 * Alunos: Iuri Silva, Henrique Milheiro
 * Professor: José Brás
 */
public class MP1_2024114213_2024107621 {

    // Método responsável por registar uma vitória
    private void registarVitoria(String jogador, long segundos) {
        String tempo = formatarTempo(segundos);  // Formata o tempo da vitória
        String entrada = jogador + " - " + tempo;
        vitorias[totalVitorias % 10] = entrada;  // Armazena a vitória no vetor de vitórias
        totalVitorias++;  // Incrementa o total de vitórias
    }

    private static final int TAM = 10;  // Tamanho do tabuleiro (10x10)
    private static final int TOTAL_MINAS = 10;  // Número total de minas no tabuleiro
    private static final char CELULA_OCULTA = '□';  // Representação de uma célula não revelada
    private static final char CELULA_BANDEIRA = 'F';  // Representação de uma célula com bandeira
    private static final char CELULA_MINA = '*';  // Representação de uma célula com mina
    private static final Scanner scanner = new Scanner(System.in);  // Scanner para ler entradas do jogador

    // Estruturas para armazenar o estado do jogo
    private char[][] estadoVisivel;  // O que o jogador vê no tabuleiro
    private boolean[][] minas;  // As posições das minas
    private int[][] minasAdjacentes;  // Número de minas adjacentes a cada célula
    private boolean[][] celulasAbertas;  // Marca as células já abertas
    private int numeroBandeiras = TOTAL_MINAS;  // Contador de bandeiras disponíveis
    private long tempoInicio;  // Tempo de início do jogo
    private String nomeJogador = "";  // Nome do jogador
    private boolean modoCheat = false;  // Modo de trapaça (para mostrar as minas)
    private boolean emJogo = true;  // Estado do jogo (em andamento ou terminado)
    private final String[] vitorias = new String[10];  // Lista das últimas 10 vitórias
    private int totalVitorias = 0;  // Contador do total de vitórias

    // Construtor que chama o menu principal quando o jogo é iniciado
    public MP1_2024114213_2024107621() {
        menuPrincipal();  // Exibe o menu principal
    }

    // Método que exibe o menu principal do jogo
    private void menuPrincipal() {
        while (true) {
            System.out.println("\nDesminagem");
            System.out.println("1 – Novo Jogo");
            System.out.println("2 – 10 Vitórias Recentes");
            System.out.println("3 – Sair do Jogo");
            System.out.print("Opcão:> ");
            String escolha = scanner.nextLine();
            switch (escolha) {
                case "1":
                    iniciarNovoJogo();  // Inicia um novo jogo
                    break;
                case "2":
                    mostrarVitorias();  // Mostra as últimas vitórias
                    break;
                case "3":
                    System.exit(0);  // Encerra o programa
                default:
                    System.out.println("Opção inválida.");
            }
        }
    }

    // Método que inicializa um novo jogo
    private void iniciarNovoJogo() {
        System.out.print("Introduza o nickname (ou Enter para anónimo): ");
        nomeJogador = scanner.nextLine();  // Lê o nome do jogador
        if (nomeJogador.isBlank()) {
            nomeJogador = "Anónimo";  // Caso o nome esteja em branco, define como "Anónimo"
        }

        // Inicializa as matrizes para o tabuleiro e as minas
        estadoVisivel = new char[TAM][TAM];
        minas = new boolean[TAM][TAM];
        minasAdjacentes = new int[TAM][TAM];
        celulasAbertas = new boolean[TAM][TAM];

        // Preenche o tabuleiro com células ocultas
        for (int i = 0; i < TAM; i++) {
            Arrays.fill(estadoVisivel[i], CELULA_OCULTA);
        }

        // Distribui as minas aleatoriamente e calcula as minas adjacentes
        distribuirMinas();
        calcularMinasAdjacentes();

        emJogo = true;  // Define que o jogo está em andamento
        tempoInicio = System.currentTimeMillis();  // Registra o tempo de início do jogo

        // Loop principal do jogo
        while (emJogo) {
            imprimirTabuleiro();  // Imprime o estado atual do tabuleiro
            System.out.print("Comando > ");
            interpretarComando(scanner.nextLine());  // Interpreta o comando do jogador
        }
    }

    // Método que distribui as minas aleatoriamente no tabuleiro
    private void distribuirMinas() {
        Random aleatorio = new Random();
        int colocadas = 0;
        while (colocadas < TOTAL_MINAS) {
            int i = aleatorio.nextInt(TAM);
            int j = aleatorio.nextInt(TAM);
            if (!minas[i][j]) {  // Só coloca mina onde não há mina
                minas[i][j] = true;
                colocadas++;  // Incrementa o contador de minas colocadas
            }
        }
    }

    // Método que calcula o número de minas adjacentes a cada célula
    private void calcularMinasAdjacentes() {
        for (int i = 0; i < TAM; i++) {
            for (int j = 0; j < TAM; j++) {
                if (minas[i][j]) {
                    continue;  // Pula as células com minas
                }
                int contador = 0;
                // Verifica as 8 células ao redor de cada célula
                for (int di = -1; di <= 1; di++) {
                    for (int dj = -1; dj <= 1; dj++) {
                        int ni = i + di, nj = j + dj;
                        if (ni >= 0 && nj >= 0 && ni < TAM && nj < TAM && minas[ni][nj]) {
                            contador++;  // Conta as minas adjacentes
                        }
                    }
                }
                minasAdjacentes[i][j] = contador;  // Armazena a quantidade de minas adjacentes
            }
        }
    }

    // Método que imprime o tabuleiro atual (o que o jogador vê)
    private void imprimirTabuleiro() {
        System.out.print("  ");
        for (int j = 0; j < TAM; j++) {
            System.out.print(j + " ");  // Imprime as colunas
        }
        System.out.println();
        for (int i = 0; i < TAM; i++) {
            System.out.print((char) ('A' + i) + " ");  // Imprime as linhas
            for (int j = 0; j < TAM; j++) {
                if (modoCheat && minas[i][j]) {
                    System.out.print(CELULA_MINA + " ");  // Se estiver no modo de cheat, mostra as minas
                } else {
                    System.out.print(estadoVisivel[i][j] + " ");  // Caso contrário, mostra o estado da célula
                }
            }
            System.out.println();
        }
        // Exibe informações adicionais
        System.out.println("\nBandeiras disponíveis: " + numeroBandeiras);
        long tempoDecorrido = (System.currentTimeMillis() - tempoInicio) / 1000;
        System.out.println("Tempo Decorrido: " + tempoDecorrido + " segundos");
    }

    // Método que interpreta os comandos do jogador
    private void interpretarComando(String linha) {
        String[] partes = linha.trim().toLowerCase().split(" ");
        if (partes.length == 0) {
            return;
        }

        switch (partes[0]) {
            case "/ajuda":
                mostrarAjuda();  // Exibe os comandos disponíveis
                break;
            case "/batota":
                modoCheat = !modoCheat;  // Alterna o modo de cheat
                break;
            case "/terminar":
                confirmarTermino();  // Confirma o término do jogo
                break;
            case "/abrir":
                if (partes.length == 3) {
                    abrirCelula(partes[1].toUpperCase().charAt(0) - 'A', Integer.parseInt(partes[2]));  // Abre a célula especificada
                } else {
                    System.out.println("Sintaxe correta: /abrir <linha> <coluna>");
                }
                break;
            case "/bandeira":
                if (partes.length == 3) {
                    alternarBandeira(partes[1].toUpperCase().charAt(0) - 'A', Integer.parseInt(partes[2]));  // Alterna a bandeira na célula
                } else {
                    System.out.println("Sintaxe correta: /bandeira <linha> <coluna>");
                }
                break;
            case "/dica":
                sugerirJogada();  // Dá uma sugestão de jogada ao jogador
                break;
            default:
                System.out.println("Comando inválido. Use /ajuda para lista de comandos.");
        }
    }

    // Método que alterna a bandeira em uma célula
    private void alternarBandeira(int linha, int coluna) {
        if (linha < 0 || linha >= TAM || coluna < 0 || coluna >= TAM) {
            System.out.println("Coordenadas inválidas.");
            return;
        }
        if (celulasAbertas[linha][coluna]) {
            return;  // Não pode colocar bandeira em células abertas
        }
        if (estadoVisivel[linha][coluna] == CELULA_BANDEIRA) {
            estadoVisivel[linha][coluna] = CELULA_OCULTA;  // Remove a bandeira
            numeroBandeiras++;
        } else if (numeroBandeiras > 0) {
            estadoVisivel[linha][coluna] = CELULA_BANDEIRA;  // Coloca a bandeira
            numeroBandeiras--;
        } else {
            System.out.println("Não há mais bandeiras disponíveis.");
        }
    }

    // Método que abre uma célula do tabuleiro
    private void abrirCelula(int linha, int coluna) {
        if (linha < 0 || linha >= TAM || coluna < 0 || coluna >= TAM) {
            System.out.println("Coordenadas inválidas.");
            return;
        }
        if (estadoVisivel[linha][coluna] == CELULA_BANDEIRA || celulasAbertas[linha][coluna]) {
            return;  // Não abre células já abertas ou com bandeira
        }

        if (minas[linha][coluna]) {
            estadoVisivel[linha][coluna] = CELULA_MINA;  // Revela a mina
            System.out.println("\nPisaste uma mina!");
            revelarTodasMinas(linha, coluna);  // Revela todas as minas após o jogador perder
            emJogo = false;  // Termina o jogo
            long duracao = (System.currentTimeMillis() - tempoInicio) / 1000;
            registarVitoria(nomeJogador, duracao);  // Registra a vitória (mesmo que tenha perdido)
            return;
        }

        abrirCascata(linha, coluna);  // Abre células adjacentes se necessário
        verificarVitoria();  // Verifica se o jogador venceu
    }

    // Método que abre células adjacentes recursivamente (cascata)
    private void abrirCascata(int i, int j) {
        if (i < 0 || i >= TAM || j < 0 || j >= TAM || celulasAbertas[i][j] || estadoVisivel[i][j] == CELULA_BANDEIRA) {
            return;  // Se a célula não puder ser aberta, retorna
        }
        celulasAbertas[i][j] = true;
        estadoVisivel[i][j] = minasAdjacentes[i][j] == 0 ? ' ' : (char) ('0' + minasAdjacentes[i][j]);  // Revela a célula

        // Se a célula não tem minas adjacentes, abre as células adjacentes
        if (minasAdjacentes[i][j] == 0) {
            for (int di = -1; di <= 1; di++) {
                for (int dj = -1; dj <= 1; dj++) {
                    abrirCascata(i + di, j + dj);
                }
            }
        }
    }

    // Método que revela todas as minas
    private void revelarTodasMinas(int linhaPerdida, int colunaPerdida) {
        for (int i = 0; i < TAM; i++) {
            for (int j = 0; j < TAM; j++) {
                if (minas[i][j]) {
                    estadoVisivel[i][j] = CELULA_MINA;  // Revela todas as minas
                }
            }
        }
    }

    // Método que verifica se o jogador venceu
    private void verificarVitoria() {
        boolean ganhou = true;
        for (int i = 0; i < TAM; i++) {
            for (int j = 0; j < TAM; j++) {
                if (!minas[i][j] && !celulasAbertas[i][j]) {
                    ganhou = false;  // Se houver células não abertas, o jogador não venceu
                    break;
                }
            }
        }

        if (ganhou) {
            long duracao = (System.currentTimeMillis() - tempoInicio) / 1000;
            System.out.println("\nParabéns, ganhaste!");
            registarVitoria(nomeJogador, duracao);  // Registra a vitória
            emJogo = false;  // Termina o jogo
        }
    }

    // Método que exibe o comando de ajuda
    private void mostrarAjuda() {
        System.out.println("\nComandos Disponíveis:");
        System.out.println("/ajuda - Exibe esta ajuda");
        System.out.println("/batota - Ativa/desativa o modo de trapaça (mostra minas)");
        System.out.println("/terminar - Termina o jogo");
        System.out.println("/abrir <linha> <coluna> - Abre a célula na posição especificada");
        System.out.println("/bandeira <linha> <coluna> - Coloca ou remove uma bandeira");
        System.out.println("/dica - Mostra uma dica (uma célula segura para abrir)");
    }

    // Método que sugere uma célula segura para abrir (dica)
    private void sugerirJogada() {
        outerLoop:
        for (int i = 0; i < TAM; i++) {
            for (int j = 0; j < TAM; j++) {
                if (!minas[i][j] && !celulasAbertas[i][j]) {
                    System.out.println("Dica: Tenta abrir a célula " + (char) ('A' + i) + " " + j);
                    break outerLoop;
                }
            }
        }
    }

    // Método que formata o tempo decorrido para uma string
    private String formatarTempo(long segundos) {
        long minutos = segundos / 60;
        segundos = segundos % 60;
        return String.format("%02d:%02d", minutos, segundos);
    }

    // Método que mostra as últimas 10 vitórias
    private void mostrarVitorias() {
        System.out.println("\nÚltimas 10 vitórias:");
        for (int i = 0; i < 10; i++) {
            if (vitorias[i] != null) {
                System.out.println(vitorias[i]);
            }
        }
    }

    // Método que pede confirmação para terminar o jogo
    private void confirmarTermino() {
        System.out.print("Tem a certeza que quer terminar o jogo (s/n)? ");
        if (scanner.nextLine().trim().equalsIgnoreCase("s")) {
            System.exit(0);  // Termina o jogo
        }
    }

    public static void main(String[] args) {
        new MP1_2024114213_2024107621();  // Inicia o jogo
    }
}
