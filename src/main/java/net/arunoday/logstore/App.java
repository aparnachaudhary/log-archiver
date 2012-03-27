package net.arunoday.logstore;

import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Hello Mongo!
 */
public class App {

	public static void main(String[] args) {
		System.out.println("Bootstrapping Demo");
		new ClassPathXmlApplicationContext("META-INF/spring/applicationContext.xml");
		System.out.println("DONE!");
	}
}
