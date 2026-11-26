
package br.unicentro.lojaveiculos.service;

import br.unicentro.lojaveiculos.model.Veiculo;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class VeiculoServiceTest {

    @Test
    void RejeitarPlacaInvalida() {
        VeiculoService service = new VeiculoService();

        Veiculo veiculo = new Veiculo(
                1,
                "Toyota",
                "ABC",
                "Branco",
                85000.0,
                "DISPONIVEL",
                "Carro",
                "Corolla",
                2022,
                "Automatico"
        );

        IllegalArgumentException excecao = assertThrows(IllegalArgumentException.class, () -> service.validarVeiculo(veiculo), "O sistema deve rejeitar uma placa invalida.");

        assertEquals("A placa deve possuir 7 caracteres!", excecao.getMessage(), "A mensagem de erro da placa deve estar correta.");
    }



    @Test
    void impedirCadastroComoVendido() {
        VeiculoService service = new VeiculoService();

        Veiculo veiculo = new Veiculo(
                1,
                "Toyota",
                "ABC1D23",
                "Branco",
                85000.0,
                "VENDIDO",
                "Carro",
                "Corolla",
                2022,
                "Automatico"
        );

        IllegalArgumentException excecao = assertThrows(IllegalArgumentException.class, () -> service.cadastrar(veiculo), "O sistema deve impedir o cadastro de um veiculo vendido.");

        assertEquals(
                "Não é permitido cadastrar um veículo diretamente como VENDIDO! Cadastre-o como DISPONIVEL e registre a venda na tela de Vendas.",
                excecao.getMessage(), "A mensagem de erro do cadastro deve estar correta.");
    }


}
