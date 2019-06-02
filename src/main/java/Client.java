import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * Sample Class to demonstrate
 * couple of file traversing exercises.
 *
 * @author Jagadish D
 */
public class Client {

    public static void main(String[] args) throws Exception {
        getLatestCUSIPPrices().forEach((id, price) -> System.out.println(id + "=>" + "Closing Price::"+ price));
        mergeSortedFiles();
    }

    /**
     *
     * @return Map of CUSIP's and
     * it's associated closing prices
     * @throws IOException
     */
    private static Map<String, Double> getLatestCUSIPPrices() throws Exception {
        Map<String, Double> cusipPrices = new HashMap<>();
        Stack<String> csuips = new Stack<>();

        try (Stream<String> records = Files.lines(Paths.get(ClassLoader.getSystemResource("sample.txt").toURI()))) {

            // Here file content is not loaded into any collection
            // and rather its traversed as Streams which are lazy
            // and on forEach terminal operation
            // the file content is iterated by record
            // to aggregate CSUIPs and prices
            records.forEach(record -> {
                if (isAlphaNumeric.test(record)) {
                    csuips.push(record);
                } else {
                    //replace the latest price per iteration.
                    cusipPrices.put(csuips.lastElement(), Double.parseDouble(record));
                }
            });
        }
        return cusipPrices;

    }

    /**
     * Merge 2 sorted Files
     * and maintain the sort order of merged files.
     * @throws IOException
     */
    private static void mergeSortedFiles() throws Exception {
        Path animalFilePath = Paths.get(ClassLoader.getSystemResource("fruits.txt").toURI());
        Path veggieFilePath = Paths.get(ClassLoader.getSystemResource("veggies.txt").toURI());

        // Merge 2 file streams and then apply sort operation
        // on the stream pipeline to maintain the order
        // Then pass the resulting Stream iterable to IO write operation
        try (Stream<String> mergedFiles = Stream.concat(Files.lines(animalFilePath),
                Files.lines(veggieFilePath)).sorted()) {
            Files.write(Paths.get("src/main/resources/merged.txt"), (Iterable<String>) mergedFiles::iterator);
        }
    }

    static Predicate<String> isAlphaNumeric = e -> e.matches(".*[a-zA-Z].*") && e.matches(".*[0-9].*") ;
}
