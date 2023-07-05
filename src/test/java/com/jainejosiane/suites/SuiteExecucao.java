package com.jainejosiane.suites;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.jainejosiane.servicos.CalculadoraTest;
import com.jainejosiane.servicos.CalculoValorLocacaoTeste;
import com.jainejosiane.servicos.LocacaoServiceTest;

@RunWith(Suite.class)                      //roda todos os m�todos de todas as classes informadas no SuiteClasses
@SuiteClasses({
		CalculadoraTest.class,
		CalculoValorLocacaoTeste.class,
		LocacaoServiceTest.class
})
public class SuiteExecucao {
	//remova se puder!
}
