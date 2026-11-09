package br.unicentro.lojaveiculos.observer;

import br.unicentro.lojaveiculos.model.Venda;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class AuditoriaVendaObserver implements VendaObserver {

    private static final DateTimeFormatter FORMATADOR_DATA_HORA =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    @Override
    public void aoConfirmarOperacaoVenda(String tipoOperacao, Venda venda, String detalhes) {
        String horario = LocalDateTime.now().format(FORMATADOR_DATA_HORA);
        int idVenda = venda != null ? venda.getIdVenda() : 0;
        double valor = venda != null ? venda.getValorTotal() : 0.0;

        String linhaLog = String.format(
                "[%s] OPERAÇÃO: %-10s | ID Venda: %d | Valor: R$ %.2f | %s%n",
                horario, tipoOperacao, idVenda, valor, detalhes
        );

        System.out.print("[AUDITORIA OBSERVER] " + linhaLog);

        try {
            File diretorio = new File("Auditoria");
            if (!diretorio.exists()) {
                diretorio.mkdir();
            }

            File arquivoLog = new File(diretorio, "auditoria_vendas.txt");
            try (FileWriter writer = new FileWriter(arquivoLog, true)) {
                writer.write(linhaLog);
            }
        } catch (IOException e) {
            System.err.println("Erro ao registrar log de auditoria da venda: " + e.getMessage());
        }
    }
}