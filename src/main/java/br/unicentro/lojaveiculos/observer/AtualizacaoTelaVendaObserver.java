package br.unicentro.lojaveiculos.observer;

import br.unicentro.lojaveiculos.model.Venda;

public class AtualizacaoTelaVendaObserver implements VendaObserver {

    private final Runnable acaoAtualizarInterface;

    public AtualizacaoTelaVendaObserver(Runnable acaoAtualizarInterface) {
        this.acaoAtualizarInterface = acaoAtualizarInterface;
    }

    @Override
    public void aoConfirmarOperacaoVenda(String tipoOperacao, Venda venda, String detalhes) {
        if (acaoAtualizarInterface != null) {
            acaoAtualizarInterface.run();
        }
    }
}