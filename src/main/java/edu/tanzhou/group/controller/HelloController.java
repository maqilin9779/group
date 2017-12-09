package edu.tanzhou.group.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {
	
	@RequestMapping("/")
	public String index(){
		System.out.println("spring boot is access");
		System.out.println("this is maqilin2015第一次提交");
		return "spring boot is access";
	}
	
	/**
	 * 缁檃pp鎻愪緵鐨勬帴鍙�
	 * @return
	 */
	@RequestMapping(value="api/one/list",method={RequestMethod.GET})
	public String sayHello(){
		
		System.out.println("浣犲ソtomcat2");
		return "浣犲ソtomcat2";
	}
}
