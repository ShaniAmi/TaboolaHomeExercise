import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class InvoiceParser {

    private static final Logger logger = LogManager.getLogger(InvoiceParser.class);

    final static Map<String, Integer> asciiToDigit = Map.ofEntries(
            Map.entry(" _ | ||_|", 0),
            Map.entry("     |  |", 1),
            Map.entry(" _  _||_ ", 2),
            Map.entry(" _  _| _|", 3),
            Map.entry("   |_|  |", 4),
            Map.entry(" _ |_  _|", 5),
            Map.entry(" _ |_ |_|", 6),
            Map.entry(" _   |  |", 7),
            Map.entry(" _ |_||_|", 8),
            Map.entry(" _ |_| _|", 9)
    );

    public static void main(String[] args) {
        if (args.length < 2) {
            logger.error("input and output file paths weren't received. exiting...");
        } else {
            File inputFile = new File(args[0]);
            File outputFile = new File(args[1]);
            try {
                outputFile.createNewFile();
                if (inputFile.isFile() && inputFile.canRead() && outputFile.canWrite()) {
                    writeASCIIAsDigits(inputFile, outputFile);
                    logger.info("Parsing invoice numbers to " +
                            outputFile.getName() + "is complete");
                } else {
                    logger.error("File paths are not valid or " +
                            "does not have the right permissions");
                }
            } catch (IOException e) {
                logger.error("File path " + outputFile.getName() + "is invalid, " +
                        "or the system does not have the right permissions to create a new file");
            }
        }
    }

    private static void writeASCIIAsDigits(File inputFile, File outputFile) {
        try {
            logger.info("Starts parsing the input file " + inputFile.getName());
            BufferedReader reader = new BufferedReader(new FileReader(inputFile));
            BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile));
            long numOfLines = Files.lines(Paths.get(inputFile.getPath()), Charset.defaultCharset()).count();
            List<String> invoiceLines = new ArrayList<>();
            String invoiceResult;
            while (numOfLines >= 3) {
                for (int index = 0; index < 3; index++) {
                    String line;
                    if ((line = reader.readLine()) == null) {
                        break;
                    } else {
                        invoiceLines.add(line);
                    }
                }
                reader.readLine();
                invoiceResult = readDigitsFromSingleInvoice(invoiceLines);
                invoiceLines = new ArrayList<>();
                writer.write(invoiceResult);
                writer.newLine();
                writer.flush();
                numOfLines -= 4;
            }
            reader.close();
            writer.close();
        } catch (IOException ex) {
            logger.error("The system doesn't have the right permissions to work on the files, " +
                    "or one of them does not exist");
        }
    }

    private static String readDigitsFromSingleInvoice(List<String> invoiceLines) {
        String[] digitsInASCII = new String[9];
        Arrays.fill(digitsInASCII, "");
        StringBuilder invoiceResult = new StringBuilder();
        boolean isLegal = true;
        invoiceLines.forEach(line -> {
            for (int charIndex = 0; charIndex < line.length(); charIndex++) {
                digitsInASCII[charIndex / 3] += (line.charAt(charIndex));
            }
        });
        for (String digitASCIIOutput : digitsInASCII) {
            if (asciiToDigit.containsKey(digitASCIIOutput)) {
                invoiceResult.append(asciiToDigit.get(digitASCIIOutput));
            } else {
                invoiceResult.append('?');
                isLegal = false;
            }
        }
        if (!isLegal) {
            invoiceResult.append("ILLEGAL");
        }
        return invoiceResult.toString();
    }
}
