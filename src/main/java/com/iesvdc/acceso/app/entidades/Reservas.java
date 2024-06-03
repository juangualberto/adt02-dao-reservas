package com.iesvdc.acceso.app.entidades;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
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
public class Reservas {
    @XmlElement(name="reserva")
    List<Reserva> reservas;
    
}
