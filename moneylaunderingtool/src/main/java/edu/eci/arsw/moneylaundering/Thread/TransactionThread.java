package edu.eci.arsw.moneylaundering.Thread;

import edu.eci.arsw.moneylaundering.MoneyLaundering;
import edu.eci.arsw.moneylaundering.Transaction;
import edu.eci.arsw.moneylaundering.TransactionAnalyzer;
import edu.eci.arsw.moneylaundering.TransactionReader;

import java.io.File;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class TransactionThread extends Thread {
    private boolean suspender;
    private List<File> transactionFiles;
    private TransactionReader transactionReader;

    private TransactionAnalyzer transactionAnalyzer;
    private AtomicInteger amountOfFilesProcessed;


    public TransactionThread( TransactionAnalyzer transactionAnalyzer,AtomicInteger amountOfFilesProcessed, TransactionReader transactionReader) {
            MoneyLaundering money = new MoneyLaundering();
            this.transactionFiles = money.getTransactionFileList();
            this.transactionReader=transactionReader;
            this.transactionAnalyzer=transactionAnalyzer;
            this.amountOfFilesProcessed=amountOfFilesProcessed;

            this.suspender = false;
        }

        @Override
        public void run() {
            for(File transactionFile : transactionFiles){
                synchronized(this){
                    while(suspender){
                        try {
                            wait();
                        } catch (InterruptedException ex) {
                            ex.printStackTrace();
                        }
                    }
                }
                List<Transaction> transactions = transactionReader.readTransactionsFromFile(transactionFile);
                for(Transaction transaction : transactions)
                {
                    transactionAnalyzer.addTransaction(transaction);
                }
                amountOfFilesProcessed.incrementAndGet();
            }
        }


        public synchronized void suspenderHilo() {
            suspender = true;
        }

        /**
         * Método que reanuda el hilo
         */
        public synchronized void reanudarHilo() {
            suspender = false;
            notify();
        }
}
