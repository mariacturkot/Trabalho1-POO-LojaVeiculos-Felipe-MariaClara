package br.unicentro.lojaveiculos.service;

import br.unicentro.lojaveiculos.model.Veiculo;
import br.unicentro.lojaveiculos.model.Venda;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class VendaServiceTest {

    @Test
    void rejeitarVendaComValorNegativo() {

        VendaService service = new VendaService();

        Veiculo veiculo = new Veiculo(
                1,
                "Toyota",
                "ABC1D23",
                "Branco",
                85000.0,
                "DISPONIVEL",
                "Carro",
                "Corolla",
                2022,
                "Automatico"
        );

        Venda venda = new Venda(
                1,
                1,
                1,
                LocalDate.of(2026, 11, 25),
                -85000.0,
                LocalDate.of(2026, 11, 25)
        );

        IllegalArgumentException excecao = assertThrows(IllegalArgumentException.class, () -> service.validarCadastro(venda, veiculo), "O sistema deve rejeitar uma venda com valor negativo.");
        assertEquals("O valor da venda deve ser maior que zero!", excecao.getMessage(), "A mensagem de erro do valor da venda deve estar correta.");
    }
}
