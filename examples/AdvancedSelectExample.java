import java.util.Arrays;
import java.util.stream.IntStream;
import com.fahmatrix.DataFrame;
import com.fahmatrix.Series;
import com.fahmatrix.Helpers.DataSelector;

public class AdvancedSelectExample {
    /* 
     * Windows Only
     * javac -cp ".\build\libs\fahmatrix-0.1.5.jar" .\examples\AdvancedSelectExample.java
     * java -cp ".\build\libs\fahmatrix-0.1.5.jar;examples" AdvancedSelectExample
     * 
     */
    
    public static void main(String[] args) {
        
        // file by github user datablist https://github.com/datablist/sample-csv-files
		DataFrame df = new DataFrame();
		df.readCSV(".\\examples\\exampleFiles\\customers-100000.csv");
        
        // here you will find two Index columns, one of them is hard-coded inside the csv file and the other is auto genreated by the library
        df.head(2).print();
        
        // Step by step Select
        DataFrame result = df.filterEquals("First Name","Marie");
        result = result.filterContains("City", "West");
        result = result.getColumnsByLabel("Company","First Name","City");
        result.head().print();

        // One line Select using builder pattern
        df.select().columns("Company","First Name","City").filterEquals("First Name","Marie").filterContains("City", "West").get().head().print();
    
        System.out.println("=====================");
        // Using simple data to make advanced select
        DataFrame df2 = new DataFrame();
		df2.addColumn("age", Arrays.asList(25, 30, 35,null));
		df2.addColumn("name", Arrays.asList("Alice", "Bob", "Charlie", "Martha"));
        df2.print();

        // Select Integer by it's string representative
        System.out.println("filterContains(\"age\", \"30\")");
        System.out.println("=====================");
        df2.filterContains("age", "30").print();
        System.out.println("filterEquals(\"age\", \"30\")");
        System.out.println("=====================");
        df2.filterEquals("age", "30").print();
        System.out.println("filterEqualsIgnoreCase(\"age\", \"30\")");
        System.out.println("=====================");
        df2.filterEqualsIgnoreCase("age", "30").print();

        // Select String
        System.out.println("filterEquals(\"name\", \"Martha\")");
        System.out.println("=====================");
        df2.filterEquals("name", "Martha").print();
        System.out.println("filterEquals(\"name\", \"martha\")");
        System.out.println("=====================");
        df2.filterEquals("name", "martha").print();
        System.out.println("filterEqualsIgnoreCase(\"name\", \"martha\")");
        System.out.println("=====================");
        df2.filterEqualsIgnoreCase("name", "martha").print();

        // Remove Empty data by column name
        System.out.println("filterNotEmpty(\"age\")");
        System.out.println("=====================");
        df2.filterNotEmpty("age").print();
    }
}
