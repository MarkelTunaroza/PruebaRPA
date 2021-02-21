package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.demo.interfaceService.IproductoService;
import com.example.demo.modelo.Producto;


//Controlador para llamar un servicio en este caso el de listar
@Controller
@RequestMapping
public class Controlador {
	
	@Autowired
	private IproductoService service;
	
	@GetMapping("/listar")
	public String listar(Model model) {
		java.util.List<Producto> productos1 = service.listar();	  
		model.addAttribute("productos1", productos1);
		return "index";
   }
	
	

}
