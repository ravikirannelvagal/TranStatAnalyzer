package com.n26.engine;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * Class:		StatsEngineStarter
 * @version:	1.0
 * @author:		Ravikiran Nelvagal
 * Description:	StatsEngineStarter is the entry
 * 				point to the spring boot application.
 * 
 */
@SpringBootApplication
@ComponentScan(basePackages={"com.n26.engine","com.n26.model"})
public class StatsEngineStarter {
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		SpringApplication.run(StatsEngineStarter.class, args);
	}

}
