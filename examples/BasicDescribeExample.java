import java.util.Arrays;
import com.fahmatrix.DataFrame;
import com.fahmatrix.Series;

public class BasicDescribeExample {
    /* 
     * Windows Only
     * javac -cp ".\build\libs\fahmatrix-0.1.1.jar" .\examples\BasicDescribeExample.java
     * java -cp ".\build\libs\fahmatrix-0.1.1.jar;examples" BasicDescribeExample
     *
     */
    
    public static void main(String[] args) {
        DataFrame df = new DataFrame();
		df.addColumn("age", Arrays.asList(25, 30, 35));
		df.addColumn("name", Arrays.asList("Alice", "Bob", "Charlie"));

        df.print();
        df.describe();
    }
}
