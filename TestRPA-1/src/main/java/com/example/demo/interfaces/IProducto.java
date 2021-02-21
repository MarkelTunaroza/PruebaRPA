package com.example.demo.interfaces;

import org.springframework.data.repository.CrudRepository;

import org.springframework.stereotype.Repository;


//Repositorio empleado para el HTML index y sus extensiones graficas
import com.example.demo.modelo.Producto;
@Repository
public interface IProducto extends CrudRepository<Producto, Integer>{
}
