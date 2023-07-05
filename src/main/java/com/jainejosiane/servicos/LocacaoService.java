package com.jainejosiane.servicos;

import com.jainejosiane.daos.LocacaoDAO;
import com.jainejosiane.entidades.Filme;
import com.jainejosiane.entidades.Locacao;
import com.jainejosiane.entidades.Usuario;
import com.jainejosiane.exceptions.FilmeSemEstoqueException;
import com.jainejosiane.exceptions.LocadoraException;
import com.jainejosiane.utils.DataUtils;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.jainejosiane.utils.DataUtils.adicionarDias;

public class LocacaoService {
	
	private LocacaoDAO locacaoDao;

	private SPCService spcService;

	private EmailService emailService;
	
	public Locacao alugarFilme(Usuario usuario, List<Filme> filmes) throws FilmeSemEstoqueException, LocadoraException {
		
		if(usuario == null) {
			throw new LocadoraException("Usuario vazio");
		}
		if(filmes == null || filmes.isEmpty()) {
			throw new LocadoraException("Filme vazio");
		}
		
		for(Filme filme : filmes) {
			if(filme.getEstoque() == 0) {
				throw new FilmeSemEstoqueException();
			}
		}

		boolean negativado;
		try {
			negativado = spcService.possuiNegativacao(usuario);
		} catch (Exception e) {
			throw new LocadoraException("Problemas no SPC, tente novamente");
		}

		if (negativado) {
			throw new LocadoraException("Usuario Negativado");
		}

		Locacao locacao = new Locacao();
		locacao.setFilme(filmes);
		locacao.setUsuario(usuario);
		locacao.setDataLocacao(new Date());
		
		double precoLocacao = 0;
		for(int i = 0; i < filmes.size(); i++) {
			Filme filme = filmes.get(i);
			Double valorFilme = filme.getPrecoLocacao();
			switch (i) {
			case 2: valorFilme = valorFilme * 0.75; break;
			case 3: valorFilme = valorFilme * 0.50; break;
			case 4: valorFilme = valorFilme * 0.25; break;
			case 5: valorFilme = 0.00; break;
			default: break;
			}
			precoLocacao += valorFilme;
		}
		
		locacao.setValor(precoLocacao);
		
		Date dataEntrega = new Date();
		dataEntrega = adicionarDias(dataEntrega, 1);
		if(DataUtils.verificarDiaSemana(dataEntrega, Calendar.SUNDAY)) {
			dataEntrega = adicionarDias(dataEntrega, 1);
		}
		locacao.setDataRetorno(dataEntrega);
		
		locacaoDao.salvar(locacao);
		
		return locacao;
	}
	
	public void notificarAtrasos() {
		List<Locacao> locacoes = locacaoDao.obterLocacoesPendentes();
		
		for(Locacao locacao : locacoes) {
			if (locacao.getDataRetorno().before(new Date())) {
				emailService.notificarAtraso(locacao.getUsuario());
			}
		}
	}

	public void prorrogarUmaLocacao(Locacao locacao, int dias) {
		Locacao novaLocacao = new Locacao();
		novaLocacao.setUsuario(locacao.getUsuario());
		novaLocacao.setFilme(locacao.getFilme());
		novaLocacao.setDataLocacao(new Date());
		novaLocacao.setDataRetorno(DataUtils.obterDataComDiferencaDias(dias));
		novaLocacao.setValor(locacao.getValor() * dias);
		locacaoDao.salvar(novaLocacao);
	}
	
	/* agora trabalhando com mocks nao preciso mais só pra satisfazer o teste injetar o mock nelas
	public void setSPCService(SPCService spcService) {
		this.spcService = spcService;
	}

	public void setLocacaoDAO(LocacaoDAO locacaoDao) {
		this.locacaoDao = locacaoDao;
	}
	
	public void setEmailService(EmailService emailService) {
		this.emailService = emailService;
	}

	 */
}