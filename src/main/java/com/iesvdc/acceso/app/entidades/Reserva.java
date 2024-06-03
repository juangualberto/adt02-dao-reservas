
package com.iesvdc.acceso.app.entidades;

import java.util.Date;

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
public class Reserva {
    private long id;
    private Cliente cliente;
    private Alojamiento alojamiento;
    private Date entrada;
    private Date salida;
    private String pension;

    // Constructor, getters y setters

    // Métodos adicionales según sea necesario
}
