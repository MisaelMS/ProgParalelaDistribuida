import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

public class BuscaSequencial {

    public static void main(String[] args) {
        System.out.println("Digite o nome a ser procurado\n");
        Scanner sc = new Scanner(System.in);
        String nome = sc.nextLine();
        long inicio = System.nanoTime();

        if (repositorio("./ArquivosTexto/dataset_p", nome)) {
            long fim = System.nanoTime();
            double tempoSegundos = (fim - inicio) / 1_000_000_000.0;
            System.out.printf("Tempo de execução: %.4f segundos%n", tempoSegundos);
            return;
        }

        if (repositorio("./ArquivosTexto/dataset_g", nome)) {
            long fim = System.nanoTime();
            double tempoSegundos = (fim - inicio) / 1_000_000_000.0;
            System.out.printf("Tempo de execução: %.4f segundos%n", tempoSegundos);
            return;
        }

        System.out.println("Nome: " + nome + ", não foi encontrado nos arquivos.");
    }

    public static boolean repositorio(String diretorio, String nome) {
        File pasta = new File(diretorio);
        File[] arquivos = pasta.listFiles((dir, name) -> name.toLowerCase().endsWith(".txt"));

        if (arquivos == null || arquivos.length == 0) {
            System.out.println("Nenhum arquivo .txt encontrado no diretório.");
            return false;
        }

        for (File arquivo : arquivos) {
            if (buscarNome(arquivo, diretorio, nome))
                return true;
        }
        return false;
    }

    private static boolean buscarNome(File arquivo, String diretorio, String nome) {
        try (BufferedReader reader = new BufferedReader(new FileReader(arquivo))) {
            String linha;
            int i = 1;

            while ((linha = reader.readLine()) != null) {
                if (linha.contains(nome)) {
                    System.out.printf("Encontrado no diretorio '%s', arquivo '%s' e - Linha %d: %s%n",
                            diretorio, arquivo.getName(), i, linha);
                    return true;
                }
                i++;
            }
        } catch (IOException e) {
            System.out.println("Erro ao ler o arquivo: " + arquivo.getName());
            e.printStackTrace();
        }
        return false;
    }
}