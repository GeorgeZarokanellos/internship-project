package gr.knowledge.internship.introduction;

import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class IntroductionApplication {

	public static void main(String[] args) {SpringApplication.run(IntroductionApplication.class, args);}

		@Bean
		public ModelMapper modelMapper(){	//allows us to enter data from employee dto to employee entity
			return new ModelMapper();
		}

}
