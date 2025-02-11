package au.com.projects.tender;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.CrossOrigin;

@SpringBootApplication
@CrossOrigin(origins = "*")
public class TenderApplication {

	public static void main(String[] args) {
		SpringApplication.run(TenderApplication.class, args);
	}

}
