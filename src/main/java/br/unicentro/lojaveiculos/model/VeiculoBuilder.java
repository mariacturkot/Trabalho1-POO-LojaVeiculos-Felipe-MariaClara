package br.unicentro.lojaveiculos.model;

public class VeiculoBuilder {

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

    public static VeiculoBuilder builder() {
        return new VeiculoBuilder();
    }

    public VeiculoBuilder comIdVeiculo(int idVeiculo) {
        this.idVeiculo = idVeiculo;
        return this;
    }

    public VeiculoBuilder comMarca(String marca) {
        this.marca = marca;
        return this;
    }

    public VeiculoBuilder comModelo(String modelo) {
        this.modelo = modelo;
        return this;
    }

    public VeiculoBuilder comPlaca(String placa) {
        this.placa = placa;
        return this;
    }

    public VeiculoBuilder comAno(int ano) {
        this.ano = ano;
        return this;
    }

    public VeiculoBuilder comCor(String cor) {
        this.cor = cor;
        return this;
    }

    public VeiculoBuilder comTipo(String tipo) {
        this.tipo = tipo;
        return this;
    }

    public VeiculoBuilder comValor(double valor) {
        this.valor = valor;
        return this;
    }

    public VeiculoBuilder comStatus(String status) {
        this.status = status;
        return this;
    }

    public VeiculoBuilder comEspecificacoes(String especificacoes) {
        this.especificacoes = especificacoes;
        return this;
    }

    public Veiculo build() {
        return new Veiculo(
                idVeiculo,
                marca,
                placa,
                cor,
                valor,
                status,
                tipo,
                modelo,
                ano,
                especificacoes
        );
    }
}