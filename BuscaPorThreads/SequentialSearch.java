package BuscaPorThreads;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class SequentialSearch {

    public Result runSearch(String diretorio, String nome) {
        File pasta = new File(diretorio);
        File[] arquivos = pasta.listFiles((dir, name) -> name.toLowerCase().endsWith(".txt"));

        if (arquivos == null || arquivos.length == 0) {
            return new Result().throwError();
        }

        for (File arquivo : arquivos) {
            Result result = buscarNome(arquivo, diretorio, nome);
            if (result.getFound()){
                return result;
            }
        }
        return new Result();
    }

    public Result runSearchOnFile(File arquivo, String nome) {
        return buscarNome(arquivo, arquivo.getParent(), nome);
    }

    private static Result buscarNome(File arquivo, String diretorio, String nome) {
        try (BufferedReader reader = new BufferedReader(new FileReader(arquivo))) {
            String linha;
            int i = 1;

            while ((linha = reader.readLine()) != null) {
                if (linha.contains(nome)) {
                    return new Result(true,diretorio, arquivo.getName(), i,null );
                }
                i++;
            }
        } catch (IOException e) {
            System.out.println("Erro ao ler o arquivo: " + arquivo.getName());
            e.printStackTrace();
        }
        return new Result();
    }
}
