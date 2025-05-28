import java.util.Arrays;
import com.fahmatrix.DataFrame;
import com.fahmatrix.Series;

public class BasicExample {
    /* 
     * Windows Only
     * javac -cp ".\build\libs\fahmatrix-0.1.4.jar" .\examples\BasicExample.java
     * java -cp ".\build\libs\fahmatrix-0.1.4.jar;examples" BasicExample
     *
     */
    
    public static void main(String[] args) {
        DataFrame df = new DataFrame();
		df.addColumn("age", Arrays.asList(25, 30, 35));
		df.addColumn("name", Arrays.asList("Alice", "Bob", "Charlie"));

        Series ages = df.getColumn("age");
        df.print();
        ages.print();

        df = df.transpose();
        df.print();
        
    }
}
