package com.iesvdc.acceso.app.entidades;


import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@ToString
@Data
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Cliente {
    private long id;
    private String nombre;
    private Contacto contacto;
    
    // Constructor, getters y setters

    // Métodos adicionales según sea necesario
}

