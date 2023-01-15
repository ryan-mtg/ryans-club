package club.ryans;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;

@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @PostConstruct
    public void startApplication() throws Exception {
    }

    public static boolean isTesting() {
        // Since the production server is Linux and its developed on Windows, this hack is close enough.
        String osName = System.getProperty("os.name").toLowerCase();
        return osName.contains("win");
    }
}