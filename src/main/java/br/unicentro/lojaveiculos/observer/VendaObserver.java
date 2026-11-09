package br.unicentro.lojaveiculos.observer;

import br.unicentro.lojaveiculos.model.Venda;

public interface VendaObserver {
    void aoConfirmarOperacaoVenda(String tipoOperacao, Venda venda, String detalhes);
}