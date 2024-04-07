package com.example.donfranorders;
public class MesaUsuario {
    private String usuario;
    private String numeroMesa;

    public MesaUsuario(String usuario, String numeroMesa) {
        this.usuario = usuario;
        this.numeroMesa = numeroMesa;
    }

    public String getUsuario() {
        return usuario;
    }

    public String getNumeroMesa() {
        return numeroMesa;
    }
}
