package edu.tanzhou.group.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {
	
	@RequestMapping("/")
	public String index(){
		System.out.println("spring boot is access");
		System.out.println();
		return "spring boot is access";
	}
	
	/**
	 * 给app提供的接口
	 * @return
	 */
	@RequestMapping(value="api/one/list",method={RequestMethod.GET})
	public String sayHello(){
		
		System.out.println("你好tomcat2");
		return "你好tomcat2";
	}
}
