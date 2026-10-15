package br.unicentro.lojaveiculos.model;

public class Veiculo {
    private int idVeiculo;
    private String marca;
    private String placa;
    private String cor;
    private double valor;
    private String status;
    private String tipo;
    private String modelo;
    private int ano;
    private String especificacoes;

    public Veiculo() {
    }

    public Veiculo(int idVeiculo, String marca, String placa, String cor, double valor, String status, String tipo, String modelo, int ano, String especificacoes) {
        this.idVeiculo = idVeiculo;
        this.marca = marca;
        this.placa = placa;
        this.cor = cor;
        this.valor = valor;
        this.status = status;
        this.tipo = tipo;
        this.modelo = modelo;
        this.ano = ano;
        this.especificacoes = especificacoes;
    }

    public int getIdVeiculo() {
        return idVeiculo;
    }

    public void setIdVeiculo(int idVeiculo) {
        this.idVeiculo = idVeiculo;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }

    public String getCor() {
        return cor;
    }

    public void setCor(String cor) {
        this.cor = cor;
    }

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTipo(){
       return tipo;
    }

    public void setTipo(String tipo){
        this.tipo = tipo;
    }

    public String getModelo(){
        return modelo;
    }

    public void setModelo(String modelo){
        this.modelo = modelo;
    }

    public int getAno(){
        return ano;
    }

    public void setAno(int ano){
        this.ano = ano;
    }

    public String getEspecificacoes(){
        return especificacoes;
    }

    public void setEspecificacoes(String especificacoes){
        this.especificacoes = especificacoes;
    }

    @Override
    public String toString(){
        return "VEICULO(ID = " + idVeiculo + " | " + marca + "  " + modelo + " | Placa = " + placa;
    }

}
