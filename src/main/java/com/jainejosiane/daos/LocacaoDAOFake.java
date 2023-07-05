package com.jainejosiane.daos;

import java.util.List;

import com.jainejosiane.entidades.Locacao;

public class LocacaoDAOFake implements LocacaoDAO{

	@Override
	public void salvar(Locacao locacao) {
		
	}

	@Override
	public List<Locacao> obterLocacoesPendentes() {
		return null;
	}

}
