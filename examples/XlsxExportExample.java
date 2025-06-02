import java.util.*;
import com.fahmatrix.DataFrame;
import com.fahmatrix.Series;

public class XlsxExportExample {
    /* 
     * Windows Only
     * javac -cp ".\build\libs\fahmatrix-0.1.5.jar" .\examples\XlsxExportExample.java
     * java -cp ".\build\libs\fahmatrix-0.1.5.jar;examples" XlsxExportExample
     *
     */
    
    public static void main(String[] args) {

        // Create a sample DataFrame
        DataFrame df = new DataFrame();
        
        // Add some sample data
        List<Object> names = Arrays.asList("Alice", "Bob", "Charlie", "Diana");
        List<Object> ages = Arrays.asList(25, 30, 35, 28);
        List<Object> salaries = Arrays.asList(50000.0, 60000.0, 75000.0, 55000.0);
        List<Object> active = Arrays.asList("Yes", "No", "Yes", "Yes");
        
        // Assuming you have methods to add columns to your DataFrame
        df.addColumn("Name", names);
        df.addColumn("Age", ages);
        df.addColumn("Salary", salaries);
        df.addColumn("Active", active);
        
        // Export to XLSX
        df.writeXlsx("output.xlsx");
        System.out.println("DataFrame exported to output.xlsx successfully!");

    }
}
