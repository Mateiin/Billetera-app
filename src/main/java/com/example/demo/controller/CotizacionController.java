package com.example.demo.controller;

import com.example.demo.model.DolarValores;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/api/cotizacion")
public class CotizacionController {

    @GetMapping
    public DolarValores obtenerPrecioDolar() {
        // 1. Definimos la URL de la API externa
        String url = "https://dolarapi.com/v1/dolares/blue";
        
        // 2. Usamos RestTemplate para "viajar" a esa URL y traer los datos
        RestTemplate restTemplate = new RestTemplate();
        DolarValores valores = restTemplate.getForObject(url, DolarValores.class);
        
        return valores;
    }
}