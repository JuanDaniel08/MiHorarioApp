package co.edu.uco.mihorario.infrastructure;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
// Le indicamos a Spring que escanee absolutamente todos tus paquetes desde la raíz de la UCO
@ComponentScan(basePackages = "co.edu.uco.mihorario") 
public class DemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }
}