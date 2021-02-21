package com.example.demo.service;

import java.util.List;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.interfaceService.IproductoService;
import com.example.demo.interfaces.IProducto;
import com.example.demo.modelo.Producto;

//Servicio para aplicar cambios u obtener información de la base de datos
@Service
public class ProductoService implements IproductoService{
	
	@Autowired
	private IProducto data;
	
	//Consulta lo que hay en la base de datos - Tabla Productos
	@Override
	public List<Producto> listar() {
		return (List<Producto>)data.findAll();
	
	}
	
	
	@Override
	public Optional<Producto> listarId(int id) {
		// TODO Auto-generated method stub
		return null;
	}
	
	//Inserta valores en la base de datos
	@Override
	public int save(Producto p) {
		int res=0;
		data.save(p);
		return res;

	}

	@Override
	public void delete(int id) {
		// TODO Auto-generated method stub
		
	}


}
