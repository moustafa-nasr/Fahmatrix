import java.util.Arrays;
import com.fahmatrix.DataFrame;
import com.fahmatrix.Series;

public class XlsxImportExample {
    /* 
     * Windows Only
     * javac -cp ".\build\libs\fahmatrix-0.1.1.jar" .\examples\XlsxImportExample.java
     * java -cp ".\build\libs\fahmatrix-0.1.1.jar;examples" XlsxImportExample
     *
     */
    
    public static void main(String[] args) {

        DataFrame csv1 = new DataFrame();
		csv1.readXlsx(".\\examples\\exampleFiles\\small_data.xlsx");
		csv1.print();
		
        // same excel sheet but saved by Google Sheets
		DataFrame csv2 = new DataFrame();
		csv2.readXlsx(".\\examples\\exampleFiles\\small_data-google.xlsx");
		csv2.print();

    }
}
