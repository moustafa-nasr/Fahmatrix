import java.util.Arrays;
import com.fahmatrix.DataFrame;
import com.fahmatrix.Series;

public class XlsxImportExample {
    /* 
     * Windows Only
     * javac -cp ".\build\libs\fahmatrix-0.1.3.jar" .\examples\XlsxImportExample.java
     * java -cp ".\build\libs\fahmatrix-0.1.3.jar;examples" XlsxImportExample
     *
     */
    
    public static void main(String[] args) {

        // simple excel file
        DataFrame xlsx1 = new DataFrame();
		xlsx1.readXlsx(".\\examples\\exampleFiles\\small_data.xlsx");
		xlsx1.print();
		
        // same excel sheet but saved by Google Sheets
		DataFrame xlsx2 = new DataFrame();
		xlsx2.readXlsx(".\\examples\\exampleFiles\\small_data-google.xlsx");
		xlsx2.print();

    }
}
