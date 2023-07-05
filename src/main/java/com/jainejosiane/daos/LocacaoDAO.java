package com.jainejosiane.daos;

import java.util.List;

import com.jainejosiane.entidades.Locacao;

public interface LocacaoDAO {

	public void salvar(Locacao locacao);

	public List<Locacao> obterLocacoesPendentes();
}
