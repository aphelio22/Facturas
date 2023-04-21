package com.example.facturas;

public class Facturas {
    private String estado;
    private String importe;
    private String fecha;

    public Facturas(String estado, String importe, String fecha) {
        this.estado = estado;
        this.importe = importe;
        this.fecha = fecha;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getImporte() {
        return importe;
    }

    public void setImporte(String importe) {
        this.importe = importe;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }
}
