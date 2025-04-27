package BuscaPorThreads;

import java.io.File;
import java.util.List;
import java.util.concurrent.RecursiveTask;

public class ForkJoinSearchTask extends RecursiveTask<Result> {
    private final List<File> files;
    private final String pattern;
    private static final int THRESHOLD = 10; // Número de arquivos para dividir

    public ForkJoinSearchTask(List<File> files, String pattern) {
        this.files = files;
        this.pattern = pattern;
    }

    @Override
    protected Result compute() {
        if (files.size() <= THRESHOLD) {
            for (File file : files) {
                if (file.isFile() && file.getName().endsWith(".txt")) {
                    SequentialSearch search = new SequentialSearch();
                    Result result = search.runSearchOnFile(file, pattern);
                    if (result.getFound()) {
                        return result;
                    }
                }
            }
            return null;
        } else {
            int mid = files.size() / 2;
            ForkJoinSearchTask task1 = new ForkJoinSearchTask(files.subList(0, mid), pattern);
            ForkJoinSearchTask task2 = new ForkJoinSearchTask(files.subList(mid, files.size()), pattern);

            // Executa em paralelo
            task1.fork();
            Result result2 = task2.compute(); // processa task2 no mesmo thread

            Result result1 = task1.join();     // espera task1 terminar

            if (result1 != null && result1.getFound()) {
                return result1;
            }
            return result2;
        }
    }
}
