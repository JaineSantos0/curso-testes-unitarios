package com.jainejosiane.builders;

import com.jainejosiane.entidades.Filme;

public class FilmeBuilder {

	private Filme filme;
	
	private FilmeBuilder() {}
	
	public static FilmeBuilder umFilme() {
		FilmeBuilder builder = new FilmeBuilder();
		builder.filme = new Filme();
		builder.filme.setNome("Filme 1");
		builder.filme.setEstoque(1);
		builder.filme.setPrecoLocacao(4.0);
		return builder;
	}
	
	public static FilmeBuilder umFilmeSemEstoque() {
		FilmeBuilder builder = new FilmeBuilder();
		builder.filme = new Filme();
		builder.filme.setNome("Filme 1");
		builder.filme.setEstoque(0);
		builder.filme.setPrecoLocacao(4.0);
		return builder;
	}
	
	public FilmeBuilder filmeSemEstoque() {
		filme.setEstoque(0);
		return this;
	}
	
	public FilmeBuilder comValor(Double valor) {
		filme.setPrecoLocacao(valor);
		return this;
	}
	
	public Filme agora() {
		return filme;
	}
	
}
