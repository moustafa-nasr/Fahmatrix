import java.util.Arrays;
import com.fahmatrix.DataFrame;
import com.fahmatrix.Series;

public class JsonImportExample {
    /* 
     * Windows Only
     * javac -cp ".\build\libs\fahmatrix-0.1.3.jar" .\examples\JsonImportExample.java
     * java -cp ".\build\libs\fahmatrix-0.1.3.jar;examples" JsonImportExample
     * 
     * for large datasets > 100K rows 
     * java -Xms512M -Xmx1024M -cp ".\build\libs\fahmatrix-0.1.1.jar;examples" CSVImportExample
     */
    
    public static void main(String[] args) {
        // file by Florida State University https://people.sc.fsu.edu/~jburkardt/data/csv/csv.html
        DataFrame json1 = new DataFrame();
		json1.readJson(".\\examples\\exampleFiles\\small_data.json");
		json1.print();
    }
}
