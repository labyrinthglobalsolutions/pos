package admin_user;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

//@ComponentScan(basePackages = {"admin_user.service.CompositeUserDetailsService"})
@SpringBootApplication
public class PointOfSale {

	public static void main(String[] args) {

		SpringApplication.run(PointOfSale.class, args);
	}

}
