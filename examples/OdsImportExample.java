import java.util.Arrays;
import com.fahmatrix.DataFrame;
import com.fahmatrix.Series;

public class OdsImportExample {
    /* 
     * Windows Only
     * javac -cp ".\build\libs\fahmatrix-0.1.5.jar" .\examples\OdsImportExample.java
     * java -cp ".\build\libs\fahmatrix-0.1.5.jar;examples" OdsImportExample
     *
     */
    
    public static void main(String[] args) {

        // simple ODS file
        DataFrame ods1 = new DataFrame();
		ods1.readOds(".\\examples\\exampleFiles\\small_data-google.ods");
		ods1.print();

    }
}
