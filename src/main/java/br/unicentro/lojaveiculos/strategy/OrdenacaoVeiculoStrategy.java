package br.unicentro.lojaveiculos.strategy;

import br.unicentro.lojaveiculos.model.Veiculo;
import java.util.List;

public interface OrdenacaoVeiculoStrategy {
    List<Veiculo> ordenar(List<Veiculo> veiculos);
}