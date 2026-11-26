
package br.unicentro.lojaveiculos.strategy;

import br.unicentro.lojaveiculos.model.Veiculo;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class OrdenacaoPadraoStrategyTest {

    @Test
    void ordenarVeiculosPorId() {

        OrdenacaoPadraoStrategy strategy = new OrdenacaoPadraoStrategy();

        Veiculo veiculo1 = new Veiculo(
                3, "Toyota", "ABC1D23", "Branco",
                85000.0, "DISPONIVEL", "Carro",
                "Corolla", 2022, "Automatico"
        );

        Veiculo veiculo2 = new Veiculo(
                1, "Honda", "DEF4G56", "Preto",
                90000.0, "DISPONIVEL", "Carro",
                "Civic", 2023, "Automatico"
        );

        Veiculo veiculo3 = new Veiculo(
                2, "Chevrolet", "HIJ7K89", "Prata",
                70000.0, "DISPONIVEL", "Carro",
                "Onix", 2021, "Manual"
        );

        List<Veiculo> veiculos = new ArrayList<>();

        veiculos.add(veiculo1);
        veiculos.add(veiculo2);
        veiculos.add(veiculo3);

        List<Veiculo> resultado = strategy.ordenar(veiculos);

        assertEquals(3, resultado.size(), "A lista deve possuir tres veiculos.");
        assertEquals(1, resultado.get(0).getIdVeiculo(), "O primeiro veiculo deve possuir ID 1.");
        assertEquals(2, resultado.get(1).getIdVeiculo(), "O segundo veiculo deve possuir ID 2.");
        assertEquals(3, resultado.get(2).getIdVeiculo(), "O terceiro veiculo deve possuir ID 3.");
        assertEquals(List.of(veiculo1, veiculo2, veiculo3), veiculos, "A lista original nao deve ser modificada.");
    }
}
