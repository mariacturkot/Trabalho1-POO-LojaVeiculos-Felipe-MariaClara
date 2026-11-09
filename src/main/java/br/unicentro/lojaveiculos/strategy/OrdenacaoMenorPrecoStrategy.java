package br.unicentro.lojaveiculos.strategy;

import br.unicentro.lojaveiculos.model.Veiculo;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class OrdenacaoMenorPrecoStrategy implements OrdenacaoVeiculoStrategy {

    @Override
    public List<Veiculo> ordenar(List<Veiculo> veiculos) {
        List<Veiculo> copia = new ArrayList<>(veiculos);
        copia.sort(Comparator.comparingDouble(Veiculo::getValor)
                .thenComparingInt(Veiculo::getIdVeiculo));
        return copia;
    }
}