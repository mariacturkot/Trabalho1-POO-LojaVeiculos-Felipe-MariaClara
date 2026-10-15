package br.unicentro.lojaveiculos.model;

public class Funcionario {
    private int idFuncionario;
    private String nome;
    private String cpf;
    private String cargo;
    private String turno;
    private double salario;

    public Funcionario() {

    }

    public Funcionario(int idFuncionario, String nome, String cpf, String cargo, String turno, double salario) {
        this.idFuncionario = idFuncionario;
        this.nome = nome;
        this.cpf = cpf;
        this.cargo = cargo;
        this.turno = turno;
        this.salario = salario;
    }

    public int getIdFuncionario() {
        return idFuncionario;
    }

    public void setIdFuncionario(int idFuncionario) {
        this.idFuncionario = idFuncionario;
    }

    public String getNome(){
        return nome;
    }

    public void setNome(String nome){
        this.nome = nome;
    }

    public String getCpf(){
        return cpf;
    }

    public void setCpf(String cpf){
        this.cpf = cpf;
    }

    public String getCargo(){
        return cargo;
    }

    public void setCargo(String cargo){
        this.cargo = cargo;
    }

    public String getTurno(){
        return turno;
    }

    public void setTurno(String turno){
        this.turno = turno;
    }

    public double getSalario(){
        return salario;
    }

    public void setSalario(double salario){
        this.salario = salario;
    }

    @Override
    public String toString(){
        return "Funcionário (ID = " + idFuncionario + " | Nome = " + nome +  ")";
    }

}
