import java.util.Arrays;
import com.fahmatrix.DataFrame;
import com.fahmatrix.Series;

public class CSVImportExample {
    /* 
     * Windows Only
     * javac -cp ".\build\libs\fahmatrix-0.1.0.jar" .\examples\CSVImportExample.java
     * java -cp ".\build\libs\fahmatrix-0.1.0.jar;examples" CSVImportExample
     *
     */
    
    public static void main(String[] args) {
        // file by Florida State University https://people.sc.fsu.edu/~jburkardt/data/csv/csv.html
        DataFrame csv1 = new DataFrame();
		csv1.readCSV(".\\examples\\addresses.csv");
		csv1.print();
		
        // file by Florida State University https://people.sc.fsu.edu/~jburkardt/data/csv/csv.html
		DataFrame csv2 = new DataFrame();
		csv2.readCSV(".\\examples\\snakes_count_10000.csv");
		csv2.print();

        // file by github user datablist https://github.com/datablist/sample-csv-files
		DataFrame csv3 = new DataFrame();
		csv3.readCSV(".\\examples\\customers-100000.csv");
		csv3.print();
    }
}
