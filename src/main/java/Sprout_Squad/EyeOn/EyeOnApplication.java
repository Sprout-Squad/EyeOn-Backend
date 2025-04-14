package Sprout_Squad.EyeOn;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
@EntityScan(basePackages = "Sprout_Squad.EyeOn.domain")
public class EyeOnApplication {

	public static void main(String[] args) {
		SpringApplication.run(EyeOnApplication.class, args);
	}

}
