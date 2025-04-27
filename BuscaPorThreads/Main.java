package BuscaPorThreads;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.*;

public class Main {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        System.out.println("Digite o nome a ser procurado\n");
        Scanner sc = new Scanner(System.in);
        String nome = sc.nextLine();

        File datasetP = new File("./ArquivosTexto/dataset_p");
        File datasetG = new File("./ArquivosTexto/dataset_g");

        List<File> allFiles = new ArrayList<>(Arrays.asList(datasetP.listFiles()));
        allFiles.addAll(Arrays.asList(datasetG.listFiles()));

        System.out.println("\n==================== INICIANDO BUSCAS ====================\n");

        // ------------------ CHAMANDO A BUSCA SEQUENCIAL ------------------
        long inicioBuscaSequencial = System.nanoTime();

        BuscaSequencial buscaSequencial = new BuscaSequencial();
        buscaSequencial.runSearch(nome);  // Chamando o método de busca sequencial

        double tempoBuscaSequencial = (System.nanoTime() - inicioBuscaSequencial) / 1_000_000_000.0;
        System.out.printf("Tempo de execução: %.4fs%n%n", tempoBuscaSequencial);

        // ------------------ FUTURE TASK (Thread manual) ------------------
        long inicioThreads = System.nanoTime();

        Callable<Result> sequential1 = () -> {
            SequentialSearch sequential = new SequentialSearch();
            return sequential.runSearch("./ArquivosTexto/dataset_p", nome);
        };

        Callable<Result> sequential2 = () -> {
            SequentialSearch sequential = new SequentialSearch();
            return sequential.runSearch("./ArquivosTexto/dataset_g", nome);
        };

        FutureTask<Result> future1 = new FutureTask<>(sequential1);
        FutureTask<Result> future2 = new FutureTask<>(sequential2);

        Thread thread1 = new Thread(future1);
        Thread thread2 = new Thread(future2);

        thread1.start();
        thread2.start();

        Result resultThread1 = future1.get();
        Result resultThread2 = future2.get();

        double tempoThreads = (System.nanoTime() - inicioThreads) / 1_000_000_000.0;

        if (resultThread1.getFound()) {
            System.out.printf("[THREADS] O nome %s foi encontrado no diretorio %s, arquivo %s, linha %d em %.4fS.\n",
                    nome, resultThread1.getDirectory(), resultThread1.getFile(), resultThread1.getLine(), tempoThreads);
        }
        if (resultThread2.getFound()) {
            System.out.printf("[THREADS] O nome %s foi encontrado no diretorio %s, arquivo %s, linha %d em %.4fS.\n",
                    nome, resultThread2.getDirectory(), resultThread2.getFile(), resultThread2.getLine(), tempoThreads);
        }

        // ------------------ FORK/JOIN ------------------
        ForkJoinPool pool = new ForkJoinPool();
        ForkJoinSearchTask forkJoinTask = new ForkJoinSearchTask(allFiles, nome);

        long inicioForkJoin = System.nanoTime();
        Result resultForkJoin = pool.invoke(forkJoinTask);
        double tempoForkJoin = (System.nanoTime() - inicioForkJoin) / 1_000_000_000.0;

        if (resultForkJoin != null && resultForkJoin.getFound()) {
            System.out.printf("[FORKJOIN] O nome %s foi encontrado no diretorio %s, arquivo %s, linha %d em %.4fS.\n",
                    nome, resultForkJoin.getDirectory(), resultForkJoin.getFile(), resultForkJoin.getLine(), tempoForkJoin);
        }

        // ------------------ EXECUTOR SERVICE ------------------
        ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        List<Callable<Result>> tasks = new ArrayList<>();

        for (File file : allFiles) {
            if (file.isFile() && file.getName().endsWith(".txt")) {
                tasks.add(() -> {
                    SequentialSearch sequential = new SequentialSearch();
                    return sequential.runSearchOnFile(file, nome);
                });
            }
        }

        long inicioExecutor = System.nanoTime();
        List<Future<Result>> futures = executor.invokeAll(tasks);

        Result resultExecutor = null;
        for (Future<Result> future : futures) {
            Result r = future.get();
            if (r.getFound()) {
                resultExecutor = r;
                break;
            }
        }
        executor.shutdown();
        double tempoExecutor = (System.nanoTime() - inicioExecutor) / 1_000_000_000.0;

        if (resultExecutor != null && resultExecutor.getFound()) {
            System.out.printf("[EXECUTOR] O nome %s foi encontrado no diretorio %s, arquivo %s, linha %d em %.4fS.\n",
                    nome, resultExecutor.getDirectory(), resultExecutor.getFile(), resultExecutor.getLine(), tempoExecutor);
        }

        // ------------------ CÁLCULO DE SPEEDUP ------------------
        double speedupThreads = tempoBuscaSequencial / tempoThreads;
        double speedupForkJoin = tempoBuscaSequencial / tempoForkJoin;
        double speedupExecutor = tempoBuscaSequencial / tempoExecutor;

        System.out.printf("Speedup com Threads: %.4fs%n", speedupThreads);
        System.out.printf("Speedup com ForkJoin: %.4fs%n", speedupForkJoin);
        System.out.printf("Speedup com ExecutorService: %.4fs%n", speedupExecutor);

        System.out.println("\n==================== BUSCAS FINALIZADAS ====================\n");
    }
}
