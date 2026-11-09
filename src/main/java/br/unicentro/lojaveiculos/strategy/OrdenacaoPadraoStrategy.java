package br.unicentro.lojaveiculos.strategy;

import br.unicentro.lojaveiculos.model.Veiculo;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class OrdenacaoPadraoStrategy implements OrdenacaoVeiculoStrategy {

    @Override
    public List<Veiculo> ordenar(List<Veiculo> veiculos) {
        List<Veiculo> copia = new ArrayList<>(veiculos);
        copia.sort(Comparator.comparingInt(Veiculo::getIdVeiculo));
        return copia;
    }
}