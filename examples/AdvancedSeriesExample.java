import java.util.*;
import com.fahmatrix.DataFrame;
import com.fahmatrix.Series;

public class AdvancedSeriesExample {
    /* 
     * Windows Only
     * javac -cp ".\build\libs\fahmatrix-0.1.4.jar" .\examples\AdvancedSeriesExample.java
     * java -cp ".\build\libs\fahmatrix-0.1.4.jar;examples" AdvancedSeriesExample
     *
     */
    
    public static void main(String[] args) {

        // file by Florida State University https://people.sc.fsu.edu/~jburkardt/data/csv/csv.html
        DataFrame csv1 = new DataFrame();
        csv1.readCSV(".\\examples\\exampleFiles\\snakes_count_10000.csv");
        Series series = csv1.getColumn("Game Length");

        System.out.println("Count: " + series.count());
        System.out.println("Average: " + series.mean().orElse(0));
        System.out.println("Std Dev: " + series.stdDev().orElse(0));

        // Custom parallel processing
        double[] normalized = series.processInParallel(x -> (x - series.mean().orElse(0)) / series.stdDev().orElse(1));
        
        // file by github user datablist https://github.com/datablist/sample-csv-files
        DataFrame csv2 = new DataFrame();
        csv2.readCSV(".\\examples\\exampleFiles\\customers-100000.csv");
        Series series2 = csv2.getColumn("Phone 1");
        System.out.println("Count: " + series2.count());
        System.out.println("Average: " + series2.mean().orElse(0));
        System.out.println("Std Dev: " + series2.stdDev().orElse(0));
        System.out.println("Mean: " + series2.mean().orElse(0));
        System.out.println("Median: " + series2.median().orElse(0));

        // Get default percentiles (25%, 50%, 75%)
        Map<Double, Double> defaultPercentiles = series2.percentiles();
        System.out.println("Q1: " + defaultPercentiles.get(25.0));
        System.out.println("Median: " + defaultPercentiles.get(50.0));
        System.out.println("Q3: " + defaultPercentiles.get(75.0));

        // Get specific percentile
        OptionalDouble p99 = series2.percentile(99.0);
        p99.ifPresent(val -> System.out.println("99th percentile: " + val));

        // Get custom percentiles
        // Map<Double, Double> custom = series2.percentiles(new double[]{10.0, 90.0});
    }
}
