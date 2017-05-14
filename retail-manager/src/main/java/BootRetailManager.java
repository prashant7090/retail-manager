import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * Created by prashant on 5/2/17.
 */
@SpringBootApplication
@ComponentScan("com.retailmanager")
public class BootRetailManager {
    public static void main(String[] args) {
        SpringApplication.run(BootRetailManager.class, args);
    }
}
