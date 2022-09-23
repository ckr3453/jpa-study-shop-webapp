package jpabook.jpashop;

import com.fasterxml.jackson.datatype.hibernate5.Hibernate5Module;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class JpashopApplication {

	public static void main(String[] args) {
		SpringApplication.run(JpashopApplication.class, args);
	}

	// Worst Case (Lazy 로딩에 연관된 객체들에 강제로 Lazy 로딩을 실행하여 성능상 좋지않음)
	// 나중에 좀 더 알아보기
//	@Bean
//	public Hibernate5Module hibernate5Module(){
//		Hibernate5Module hibernate5Module = new Hibernate5Module();
//		hibernate5Module.configure(Hibernate5Module.Feature.FORCE_LAZY_LOADING, true); // json 생성 시점에 강제로 Lazy 로딩 실행
//		return hibernate5Module;
//	}
}
