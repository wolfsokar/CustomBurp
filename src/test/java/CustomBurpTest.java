import java.lang.reflect.Method;

public class CustomBurpTest {
    public static void main(String[] args) {
        try {
            Method main = Class.forName("burp.StartBurp").getMethod("main", String[].class);
            main.invoke(null, (Object) args);
        }catch (Exception e){
            System.err.println("Can't find Burp in that path.");
        }
    }
}