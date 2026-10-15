package br.unicentro.lojaveiculos.model;

import java.time.LocalDate;

public class Venda {
    private int idVenda;
    private int idFuncionario;
    private int idVeiculo;
    private LocalDate dataVenda;
    private double valorTotal;
    private LocalDate dataPagamento;

    public Venda(){
    }

    public Venda(int idVenda, int idFuncionario, int idVeiculo, LocalDate dataVenda, double valorTotal, LocalDate dataPagamento){
        this.idVenda = idVenda;
        this.idFuncionario = idFuncionario;
        this.idVeiculo = idVeiculo;
        this.dataVenda = dataVenda;
        this.valorTotal = valorTotal;
        this.dataPagamento = dataPagamento;
    }

    public int getIdVenda(){
        return idVenda;
    }

    public void setIdVenda(int idVenda){
        this.idVenda = idVenda;
    }

    public int getIdFuncionario(){
        return idFuncionario;
    }

    public void setIdFuncionario(int idFuncionario){
        this.idFuncionario = idFuncionario;
    }

    public int getIdVeiculo(){
        return idVeiculo;
    }

    public void setIdVeiculo(int idVeiculo){
        this.idVeiculo = idVeiculo;
    }

    public LocalDate getDataVenda(){
        return dataVenda;
    }

    public void setDataVenda(LocalDate dataVenda){
        this.dataVenda = dataVenda;
    }

    public double getValorTotal(){
        return valorTotal;
    }

    public void setValorTotal(double valorTotal){
        this.valorTotal = valorTotal;
    }

    public LocalDate getDataPagamento(){
        return dataPagamento;
    }

    public void setDataPagamento(LocalDate dataPagamento){
        this.dataPagamento = dataPagamento;
    }

    @Override
    public String toString(){
        return "Venda ( ID = " + idVenda +
                " | ID Funcionario = " + idFuncionario +
                " | ID Veiculo = " + idVeiculo +
                " | Data Venda = " + dataVenda +
                " | Valor Total = R$ " + valorTotal +
                " | Data Pagamento = " + dataPagamento + " )";
    }
}
