import java.util.Arrays;
import java.util.stream.IntStream;
import com.fahmatrix.DataFrame;
import com.fahmatrix.Series;
import com.fahmatrix.Helpers.DataSelector;

public class BasicSelectExample {
    /* 
     * Windows Only
     * javac -cp ".\build\libs\fahmatrix-0.1.2.jar" .\examples\BasicSelectExample.java
     * java -cp ".\build\libs\fahmatrix-0.1.2.jar;examples" BasicSelectExample
     * 
     */
    
    public static void main(String[] args) {
        
        // file by github user datablist https://github.com/datablist/sample-csv-files
		DataFrame df = new DataFrame();
		df.readCSV(".\\examples\\exampleFiles\\customers-100000.csv");

        // here you will find two Index columns, one of them is hard-coded inside the csv file and the other is auto genreated by the library
        df.head(2).print();
        
        // Step by step Select
        DataFrame result = df.getRowsByPosition(new int[]{1,2,3,5,6,8,110,10000,99});
        result = result.getByPositions(new int[0],new int[]{1,2,5});
        result.print();
        result.writeCSV(".\\examples\\exampleFiles\\customers-edited.csv");
        
        // One line Select using builder pattern
        df.select().rows(new int[]{1,2,3,5,6,8,110,10000,99}).columns(new int[]{1,2,5}).get().print();
    }
}
