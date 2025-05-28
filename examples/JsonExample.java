import java.util.Arrays;
import com.fahmatrix.DataFrame;
import com.fahmatrix.Series;

public class JsonExample {
    /* 
     * Windows Only
     * javac -cp ".\build\libs\fahmatrix-0.1.4.jar" .\examples\JsonExample.java
     * java -cp ".\build\libs\fahmatrix-0.1.4.jar;examples" JsonExample
     * 
     * for large datasets > 100K rows 
     * java -Xms512M -Xmx1024M -cp ".\build\libs\fahmatrix-0.1.4.jar;examples" JsonExample
     */
    
    public static void main(String[] args) {
        
        DataFrame json1 = new DataFrame();
		json1.readJson(".\\examples\\exampleFiles\\small_data.json");
		json1.print();


        DataFrame df = new DataFrame();
		df.addColumn("age", Arrays.asList(25, 30, 35));
		df.addColumn("name", Arrays.asList("Alice", "Bob", "Charlie"));
        df.writeJson(".\\examples\\exampleFiles\\small_data_edited.json");
    }
}
