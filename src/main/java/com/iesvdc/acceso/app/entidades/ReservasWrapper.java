package com.iesvdc.acceso.app.entidades;


import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@ToString
public class ReservasWrapper {
    private List<ReservaWrapper> reservas;

    // Constructor, getters, setters, etc.
}
