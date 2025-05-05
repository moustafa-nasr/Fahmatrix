import java.util.Arrays;
import java.util.List;
import com.fahmatrix.DataFrame;
import com.fahmatrix.Series;

public class BasicSeriesExample {
    /* 
     * Windows Only
     * javac -cp ".\build\libs\fahmatrix-0.1.0.jar" .\examples\BasicSeriesExample.java
     * java -cp ".\build\libs\fahmatrix-0.1.0.jar;examples" BasicSeriesExample
     *
     */
    
    public static void main(String[] args) {
        List<Object> data = Arrays.asList(10, 20, null, 30, 40.5, 15);
        List<String> idx = Arrays.asList("a", "b", "c", "d", "e", "f");
        
        Series s = new Series(data, idx);
        
        System.out.println("Count: " + s.count());  // 5
        System.out.println("Min: " + s.min().orElse(Double.NaN));  // 10.0
        System.out.println("Max: " + s.max().orElse(Double.NaN));  // 40.5

    }
}
