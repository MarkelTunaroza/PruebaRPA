package com.example.demo.interfaceService;


import java.util.List;
import java.util.Optional;

import com.example.demo.modelo.Producto;
//Interfaz para consumir los servicios creados para modificar la base de datos y leer datos
public interface IproductoService {
	public List<Producto>listar();
	public Optional<Producto>listarId(int id);
	public int save(Producto p);
	public void delete(int id);
}
