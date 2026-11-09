package br.unicentro.lojaveiculos.strategy;

import br.unicentro.lojaveiculos.model.Veiculo;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class OrdenacaoAnoMaisRecenteStrategy implements OrdenacaoVeiculoStrategy {

    @Override
    public List<Veiculo> ordenar(List<Veiculo> veiculos) {
        List<Veiculo> copia = new ArrayList<>(veiculos);
        copia.sort(Comparator.comparingInt(Veiculo::getAno).reversed()
                .thenComparingInt(Veiculo::getIdVeiculo));
        return copia;
    }
}