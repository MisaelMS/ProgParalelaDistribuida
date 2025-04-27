package BuscaPorThreads;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class BuscaSequencial {

    public void runSearch(String nome) {
        // Diretórios a serem pesquisados
        if (repositorio("./ArquivosTexto/dataset_p", nome)) return;
        if (repositorio("./ArquivosTexto/dataset_g", nome)) return;

        // Caso o nome não tenha sido encontrado
        System.out.println("Nome: " + nome + ", não foi encontrado nos arquivos.");
    }

    public boolean repositorio(String diretorio, String nome) {
        File pasta = new File(diretorio);
        File[] arquivos = pasta.listFiles((dir, name) -> name.toLowerCase().endsWith(".txt"));

        if (arquivos == null || arquivos.length == 0) {
            System.out.println("Nenhum arquivo .txt encontrado no diretório: " + diretorio);
            return false;
        }

        for (File arquivo : arquivos) {
            if (buscarNome(arquivo, diretorio, nome)) return true;
        }
        return false;
    }

    private boolean buscarNome(File arquivo, String diretorio, String nome) {
        try (BufferedReader reader = new BufferedReader(new FileReader(arquivo))) {
            String linha;
            int i = 1;

            while ((linha = reader.readLine()) != null) {
                if (linha.contains(nome)) {
                    System.out.printf("Encontrado no diretório '%s', arquivo '%s' - Linha %d: %s%n",
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
