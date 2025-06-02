import java.util.Arrays;
import com.fahmatrix.DataFrame;
import com.fahmatrix.Series;

public class HeadAndTail {
    /* 
     * Windows Only
     * javac -cp ".\build\libs\fahmatrix-0.1.5.jar" .\examples\HeadAndTail.java
     * java -cp ".\build\libs\fahmatrix-0.1.5.jar;examples" HeadAndTail
     *
     */
    
    public static void main(String[] args) {
        // file by Florida State University https://people.sc.fsu.edu/~jburkardt/data/csv/csv.html
        DataFrame csv1 = new DataFrame();
		csv1.readCSV(".\\examples\\exampleFiles\\addresses.csv");
		csv1.head().print();
		
        DataFrame csv2 = new DataFrame();
        csv2.readCSV(".\\examples\\exampleFiles\\small_data.csv");
        csv2.tail().print();

    }
}
