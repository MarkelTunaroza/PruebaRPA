package com.example.demo.modelo;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

//Descripcion de la entidad producto con su constructor y sus gets y sets
@Entity
@Table(name = "producto")
public class Producto {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String nameproduct;
	private String preciomercadolibre;
	private String precioamazon;
		

	
	
	public Producto() {
		// TODO Auto-generated constructor stub
	}




	public Producto(Long id, String nameproduct, String preciomercadolibre, String precioamazon) {
		super();
		this.id = id;
		this.nameproduct = nameproduct;
		this.preciomercadolibre = preciomercadolibre;
		this.precioamazon = precioamazon;
	}




	public Long getId() {
		return id;
	}




	public void setId(Long id) {
		this.id = id;
	}




	public String getNameproduct() {
		return nameproduct;
	}




	public void setNameproduct(String nameproduct) {
		this.nameproduct = nameproduct;
	}




	public String getPreciomercadolibre() {
		return preciomercadolibre;
	}




	public void setPreciomercadolibre(String preciomercadolibre) {
		this.preciomercadolibre = preciomercadolibre;
	}




	public String getPrecioamazon() {
		return precioamazon;
	}




	public void setPrecioamazon(String precioamazon) {
		this.precioamazon = precioamazon;
	}
	
	
	

}
