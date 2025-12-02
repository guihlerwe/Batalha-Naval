package com.batalha.cliente;

public class GerenciadorCliente {
    
    private static GerenciadorCliente instancia;
    private ClienteRMI cliente;
    
    private GerenciadorCliente() {
        this.cliente = new ClienteRMI();
    }
    
    public static GerenciadorCliente getInstancia() {
        if (instancia == null) {
            instancia = new GerenciadorCliente();
        }
        return instancia;
    }
    
    public ClienteRMI getCliente() {
        return cliente;
    }
    
    public void resetar() {
        if (cliente != null) {
            cliente.desconectar();
        }
        cliente = new ClienteRMI();
    }
}
