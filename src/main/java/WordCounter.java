import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class WordCounter {

    static ConcurrentMap<String, AtomicInteger> wordsCounter = new ConcurrentHashMap<>();
    private static final Logger logger = LogManager.getLogger(WordCounter.class);

    public static void main (String [] args) {
        WordCounter wc=new WordCounter();

        if (args.length == 0) {
            logger.warn("No file paths were received in system");
        }

        wc.load(args);

        logger.info("Word counting was finished");

        // display words statistics
        wc.displayStatus();

    }

    private void displayStatus() {
        logger.debug("start displaying word statistics");
        wordsCounter.forEach((key, value) -> System.out.println(key + " " + value));
    }

    private void load(String[] filePaths) {
        ExecutorService executor = Executors.newFixedThreadPool(1);
        logger.debug("start loading words from files");
        for (String filePath : filePaths) {
            File currFile = new File(filePath);
            if (currFile.isFile() && currFile.canRead()) {
                executor.submit(() -> loadWordsFromFile(currFile));
            } else {
                logger.warn("The file " + currFile.getName() + " either doesn't " +
                        "exist or doesn't have the right authorizations");
            }
        }
        try {
            executor.shutdown();
            executor.awaitTermination(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            logger.warn("loading executor shut down was not successful");
        }
    }

    private static void loadWordsFromFile(File inputFile) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(inputFile));
            reader.lines().forEach((line) -> {
                String[] words = line.split(" ");
                for (String word : words) {
                    AtomicInteger value = wordsCounter.putIfAbsent(word, new AtomicInteger(1));
                    if (value != null) {
                        value.incrementAndGet();
                    }
                }
            });
            reader.close();
        } catch (FileNotFoundException e) {
            logger.warn("The file " + inputFile.getName() + " wasn't found in the system");
            e.printStackTrace();
        } catch (IOException e) {
            logger.warn("The system couldn't close the file reader");
        }
    }
}
