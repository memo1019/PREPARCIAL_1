package edu.eci.arsw.moneylaundering;

import edu.eci.arsw.moneylaundering.Thread.TransactionThread;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MoneyLaundering
{
    private static LinkedList<TransactionThread> threads = new LinkedList<>();
    private static TransactionAnalyzer transactionAnalyzer;
    private static TransactionReader transactionReader;
    private int amountOfFilesTotal;
    private static AtomicInteger amountOfFilesProcessed;

    public MoneyLaundering()
    {
        transactionAnalyzer = new TransactionAnalyzer();
        transactionReader = new TransactionReader();
        amountOfFilesProcessed = new AtomicInteger();
    }

    public void processTransactionData()
    {
        amountOfFilesProcessed.set(0);
        List<File> transactionFiles = getTransactionFileList();
        amountOfFilesTotal = transactionFiles.size();
        for(File transactionFile : transactionFiles)
        {
            List<Transaction> transactions = transactionReader.readTransactionsFromFile(transactionFile);
            for(Transaction transaction : transactions)
            {
                transactionAnalyzer.addTransaction(transaction);
            }
            amountOfFilesProcessed.incrementAndGet();
        }
    }

    public List<String> getOffendingAccounts()
    {
        return transactionAnalyzer.listOffendingAccounts();
    }

    public List<File> getTransactionFileList()
    {
        List<File> csvFiles = new ArrayList<>();
        try (Stream<Path> csvFilePaths = Files.walk(Paths.get("src/main/resources/")).filter(path -> path.getFileName().toString().endsWith(".csv"))) {
            csvFiles = csvFilePaths.map(Path::toFile).collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return csvFiles;
    }
    /**
     * Método que suspende la ejecución de todos los hilos que se tienen
     * @param hilos Lista con todos los hilos creados para resolver el problema
     */
    public static void pararHilos(LinkedList<TransactionThread> hilos){
        for (TransactionThread hilo: threads){
            hilo.suspenderHilo();
        }
    }

    /**
     * Método que reanuda la ejecución de todos los hilos que se tienen
     * @param hilos Lista con todos los hilos creados para resolver el problema
     */
    public static void reanudarHilos(LinkedList<TransactionThread> hilos){
        for (TransactionThread hilo: threads){
            hilo.reanudarHilo();
        }
    }


    public static void main(String[] args)
    {
        int n=3;
        for (int i = 0; i < n; i++){
            threads.add(new TransactionThread(transactionAnalyzer,amountOfFilesProcessed,transactionReader));
        }
        for (TransactionThread hilo: threads){
            hilo.start();
        }
        Scanner waitForKeypress = new Scanner(System.in);
        waitForKeypress.nextLine();
        reanudarHilos(threads);
        for (TransactionThread hilo: threads){
            try {
                hilo.join();
            } catch (InterruptedException interruptedException) {
                interruptedException.printStackTrace();
            }
        }
        System.out.print("cuentas falsas: "+ amountOfFilesProcessed );
        while(true) {
            Scanner scanner = new Scanner(System.in);
            String line = scanner.nextLine();
            if(line.contains("exit"))
                break;
            String message = "Processed %d out of %d files.\nFound %d suspect accounts:\n%s";
            MoneyLaundering moneyLaundering = new MoneyLaundering();
            List<String> offendingAccounts = moneyLaundering.getOffendingAccounts();
            String suspectAccounts = offendingAccounts.stream().reduce("", (s1, s2)-> s1 + "\n"+s2);
            message = String.format(message, moneyLaundering.amountOfFilesProcessed.get(), moneyLaundering.amountOfFilesTotal, offendingAccounts.size(), suspectAccounts);
            System.out.println(message);
        }

    }
}
