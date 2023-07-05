package com.jainejosiane.servicos;

import com.jainejosiane.builders.LocacaoBuilder;
import com.jainejosiane.daos.LocacaoDAO;
import com.jainejosiane.entidades.Filme;
import com.jainejosiane.entidades.Locacao;
import com.jainejosiane.entidades.Usuario;
import com.jainejosiane.exceptions.FilmeSemEstoqueException;
import com.jainejosiane.exceptions.LocadoraException;
import com.jainejosiane.utils.DataUtils;
import com.jainejosiane.builders.FilmeBuilder;
import com.jainejosiane.builders.UsuarioBuilder;
import com.jainejosiane.matchers.MatchersProprios;
import org.hamcrest.MatcherAssert;
import org.junit.*;
import org.junit.rules.ErrorCollector;
import org.junit.rules.ExpectedException;
import org.mockito.*;

import java.util.*;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assume.assumeFalse;
import static org.mockito.Mockito.*;

public class LocacaoServiceTest {

	@InjectMocks
	private LocacaoService service;
	//private static int contador = 0;

	@Mock
	private LocacaoDAO locacaoDAO;

	@Mock
	private SPCService spcService;

	@Mock
	private EmailService emailService;
	
	@Rule
	public ErrorCollector error = new ErrorCollector();
	
	@Rule
	public ExpectedException exception = ExpectedException.none();
	
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		/*
		service = new LocacaoService();
		locacaoDAO = Mockito.mock(LocacaoDAO.class);
		service.setLocacaoDAO(locacaoDAO);
		spcService = Mockito.mock(SPCService.class);
		service.setSPCService(spcService);
		emailService = Mockito.mock(EmailService.class);
		service.setEmailService(emailService);
		*/
	}
	
	@Test
	public void deveAlugarFilme() throws Exception {
		
		assumeFalse(DataUtils.verificarDiaSemana(new Date(), Calendar.SATURDAY));
	       
		//cenario
			Usuario usuario = UsuarioBuilder.umUsuario().agora();
			Filme filme = FilmeBuilder.umFilme().comValor(5.0).agora();
			List<Filme> filmes = new ArrayList<>();
			filmes.add(filme);
	   
	    //acao		
			Locacao locacao = service.alugarFilme(usuario, filmes);
			
	    //verificacao	
			error.checkThat(locacao.getValor(), is(equalTo(5.0))); //fluid interface CoreMatchers.() deixa o c�digo mais entendivel
			//error.checkThat(locacao.getValor(), is(not(6.0)));
			error.checkThat(DataUtils.isMesmaData(locacao.getDataLocacao(), new Date()), is(true));
			error.checkThat(locacao.getDataLocacao(), MatchersProprios.ehHoje());
			error.checkThat(DataUtils.isMesmaData(locacao.getDataRetorno(), DataUtils.obterDataComDiferencaDias(1)), is(true));
			error.checkThat(locacao.getDataRetorno(), MatchersProprios.ehHojeComDiferencaDeDias(1));
	}	
	
	//forma elegante (Quando apenas a exce��o importa para voc�)
	@Test(expected= FilmeSemEstoqueException.class)
	public void deveLancarExcecaoAoAlugarFilmeSemEstoque( ) throws Exception {
	       
		//cenario
			Usuario usuario = UsuarioBuilder.umUsuario().agora();
			Filme filme = FilmeBuilder.umFilmeSemEstoque().agora();
			List<Filme> filmes = new ArrayList<>();
			filmes.add(filme);
	   
	    //acao		
			service.alugarFilme(usuario, filmes);
			
	}
	
	//forma robusta (Mais completa que as outras pois mostra a mensagem e consigo seguir o fluxo logo abaixo normalmente)
	@Test
	public void naoDeveAlugarFilmeSemUsuario( ) throws FilmeSemEstoqueException {
		
		//cenario
		Filme filme = FilmeBuilder.umFilme().agora();
		List<Filme> filmes = new ArrayList<>();
		filmes.add(filme);
		
		//acao
		try {
			service.alugarFilme(null, filmes);
			Assert.fail();
		} catch (LocadoraException e) {
			assertThat(e.getMessage(), is("Usuario vazio"));
		}
	}
	
	//forma nova (Se precisar que informe mensagem � mais completa que a elegante)
	@Test
	public void naoDeveAlugarFilmeSemFilme() throws FilmeSemEstoqueException, LocadoraException {
		
		//cenario
		Usuario usuario = UsuarioBuilder.umUsuario().agora();
		exception.expect(LocadoraException.class);
		exception.expectMessage("Filme vazio");
		
		//acao
		service.alugarFilme(usuario, null);
	}
	
	
	@Test
	public void deveDevolverNasegundaAoAlugarNoSabado() throws FilmeSemEstoqueException, LocadoraException {
		
		Assume.assumeTrue(DataUtils.verificarDiaSemana(new Date(), Calendar.SATURDAY));
		
		//cenario
		Usuario usuario = UsuarioBuilder.umUsuario().agora();
		List<Filme> filmes = Arrays.asList(FilmeBuilder.umFilme().agora());
		
		//acao
		Locacao retorno = service.alugarFilme(usuario, filmes);
		
		//verificacao
		//boolean ehSegunda = DataUtils.verificarDiaSemana(retorno.getDataRetorno(), Calendar.MONDAY);
		//assertTrue(ehSegunda);
		
		//assertThat(retorno.getDataRetorno(), new DiaSemanaMatcher(Calendar.MONDAY));
		//assertThat(retorno.getDataRetorno(), caiEm(Calendar.MONDAY));
		MatcherAssert.assertThat(retorno.getDataRetorno(), MatchersProprios.caiNumaSegunda());
	}
	
	@Test
	public void naoDeveAlugarFilmeParaNegativadoSPC() throws Exception {
		
		//cenario
		Usuario usuario = UsuarioBuilder.umUsuario().agora();
		Usuario usuario2 = UsuarioBuilder.umUsuario().comNome("Usuario 2").agora();
		List<Filme> filmes = Arrays.asList(FilmeBuilder.umFilme().agora());
		
		//Mockito.when(spcService.possuiNegativacao(usuario)).thenReturn(true);
		when(spcService.possuiNegativacao(usuario)).thenReturn(true);

		//acao
		try {
			service.alugarFilme(usuario, filmes);
			//verificacao // import estatico alt + enter
			Assert.fail();
		} catch (LocadoraException e) {
			assertThat(e.getMessage(), is("Usuario Negativado"));
		}

		verify(spcService).possuiNegativacao(usuario);
	}
	
	@Test
	public void deveEnviarEmailParaLocacoesAtrasadas() {
		
		//cenario
		Usuario usuario = UsuarioBuilder.umUsuario().agora();
		Usuario usuario2 = UsuarioBuilder.umUsuario().comNome("Usuario 2").agora();
		Usuario usuario3 = UsuarioBuilder.umUsuario().comNome("Outro atrasado").agora();
		List<Locacao> locacoes = Arrays.asList(
				LocacaoBuilder.umLocacao().atrasada().comUsuario(usuario).agora(),
				LocacaoBuilder.umLocacao().comUsuario(usuario2).agora(),
				LocacaoBuilder.umLocacao().atrasada().comUsuario(usuario3).agora(),
				LocacaoBuilder.umLocacao().atrasada().comUsuario(usuario3).agora());
		
		when(locacaoDAO.obterLocacoesPendentes()).thenReturn(locacoes);

		//acao
		service.notificarAtrasos();

		//verificacao
		//verficacao generica, verifique se foi enviado 2 emails passando essa classe
		verify(emailService, Mockito.times(3)).notificarAtraso(Mockito.any(Usuario.class));
		verify(emailService).notificarAtraso(usuario);
		verify(emailService, Mockito.times(2)).notificarAtraso(usuario3);
		//Mockito.time verifica quantas vezes foi enviado o email
		// (uso esse quando eu sei a quantidade de emails enviados)
		//verify(emailService, Mockito.atLeast(1)).notificarAtraso(usuario3);
		//Mockito.atLest foi enviado pelo menos x emails
		//Mockito.atLestOnce foi enviado pelo menos 1 email
		//verify(emailService, Mockito.atMost(3)).notificarAtraso(usuario3);
		//Mockito.atMost foi enviado no m�ximo x emails
		verify(emailService, Mockito.never()).notificarAtraso(usuario2);
		verifyNoMoreInteractions(emailService);
		//verifica se houve mais algum envio de email fora os informados
		//Mockito.verifyZeroInteractions(spcService); // verifica se nunca foi chamado
	}

	@Test
	public void deveTratarErroNoSpc() throws Exception {

		//cenario
		Usuario usuario = UsuarioBuilder.umUsuario().agora();
		List<Filme> filmes = Arrays.asList(FilmeBuilder.umFilme().agora());

		Mockito.when(spcService.possuiNegativacao(usuario)).thenThrow(new Exception("Falha catastr�fica"));

		//verificacao
		exception.expect(LocadoraException.class);
		exception.expectMessage("Problemas no SPC, tente novamente");

		//acao
		service.alugarFilme(usuario, filmes);
	}

	@Test
	public void deveProrrogarLocacao() {

		//cenario
		Locacao locacao = LocacaoBuilder.umLocacao().agora();

		//acao
		service.prorrogarUmaLocacao(locacao, 3);

		//verificacao
		//captura os valores que est�o dentro de locacao
		ArgumentCaptor<Locacao> argCapt = ArgumentCaptor.forClass(Locacao.class);
		Mockito.verify(locacaoDAO).salvar(argCapt.capture());
		Locacao locacaoRetornada = argCapt.getValue();

		error.checkThat(locacaoRetornada.getValor(), is(12.0));
		error.checkThat(locacaoRetornada.getDataLocacao(), MatchersProprios.ehHoje());
		error.checkThat(locacaoRetornada.getDataRetorno(), MatchersProprios.ehHojeComDiferencaDeDias(3));
	}
}