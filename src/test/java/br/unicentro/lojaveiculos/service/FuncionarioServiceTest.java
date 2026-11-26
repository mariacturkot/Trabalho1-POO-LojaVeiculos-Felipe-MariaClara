
package br.unicentro.lojaveiculos.service;

import br.unicentro.lojaveiculos.model.Funcionario;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FuncionarioServiceTest {

    @Test
    void aceitarFuncionarioValido() {
        FuncionarioService service = new FuncionarioService();

        Funcionario funcionario = new Funcionario(
                1,
                "João Silva",
                "52998224725",
                "Vendedor",
                "Manhã",
                2500.0
        );

        assertDoesNotThrow(() -> service.validarFuncionario(funcionario), "O sistema deve aceitar um funcionario valido.");
    }

    @Test
    void impedirExclusaoComIdZero() {

        FuncionarioService service = new FuncionarioService();

        int idFuncionario = 0; //método funcionario recebe int, nao um objeto funcionario

        IllegalArgumentException excecao = assertThrows(IllegalArgumentException.class, () -> service.excluir(idFuncionario), "O sistema deve impedir a exclusao com ID zero.");
        assertEquals("Selecione um funcionário válido!", excecao.getMessage(), "A mensagem de erro da exclusao deve estar correta.");
    }

}
