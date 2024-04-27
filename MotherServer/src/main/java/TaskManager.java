import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;

import Caller.ReceiverPrx;


public class TaskManager implements Runnable {

    private ReceiverPrx client;
    private String filename;
    private String basePath;
    private Queue<Task> tasks = new LinkedList<>();
    private Map<String, ReceiverPrx> subservers;
    private QuickSort<ComparableInteger> quicksort = new QuickSort<>();

    public TaskManager(ReceiverPrx client, String filename, String basePath, Map<String, ReceiverPrx> workers) {
        this.client = client;
        this.filename = filename;
        this.basePath = basePath;
        this.subservers = workers;
    }

    public void addTask(Task task) {
        tasks.add(task);
    }

    @Override
    public void run() {
        try {
            List<CompletableFuture<Void>> futures = new ArrayList<>();
            for (Map.Entry<String, ReceiverPrx> entry : subservers.entrySet()) {
                ReceiverPrx worker = entry.getValue();
                Task task = tasks.poll();
                if (task != null) {
                    CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                        worker.startSubServer(task.getFrom(), task.getTo(), filename, basePath);
                    });
                    futures.add(future);
                }
            }
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
            handleSorting();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleSorting() throws IOException {
        long startTime = System.currentTimeMillis();
        List<ComparableInteger> result = new ArrayList<>();
        StringBuilder resultSB = new StringBuilder();
        for (Map.Entry<String, ReceiverPrx> entry : subservers.entrySet()) {
            String elements = entry.getValue().getListSorted();
            if (!elements.isEmpty()) {
                String[] dataArray = elements.split(", ");
                List<ComparableInteger> subList = new ArrayList<>();
                for (String data : dataArray) {
                    subList.add(new ComparableInteger(Integer.parseInt(data)));
                }
                result.addAll(subList);
            }
        }
        result = quicksort.quickSort(result);
        for (ComparableInteger element : result) {
            resultSB.append(element).append("\n");
        }
        saveSortedData(resultSB.toString());
        long endTime = System.currentTimeMillis();
        long elapsedTime = endTime - startTime;
        client.receiveMessage("Sorting completed for " + filename + ". Elapsed time: " + elapsedTime + " milliseconds");
    }
    
    private void saveSortedData(String result) throws IOException {
        File file = new File(basePath + "sorted." + filename);
        if (!file.exists()) {
            file.createNewFile();
        }
        FileWriter writer = new FileWriter(file);
        writer.write(result);
        writer.close();
    }

}