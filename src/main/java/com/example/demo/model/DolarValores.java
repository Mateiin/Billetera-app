package com.example.demo.model;

public class DolarValores {
    // Estos nombres deben coincidir con lo que devuelve la API externa
    public String moneda;
    public String casa;
    public String nombre;
    public double compra;
    public double venta;
    public String fechaUpdate;
    
    // Getters y Setters básicos (si no usas Lombok)
    public double getVenta() { return venta; }
    public double getCompra() { return compra; }
}