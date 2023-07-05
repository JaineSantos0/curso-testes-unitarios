package com.jainejosiane.servicos;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.*;

import static org.junit.Assert.assertEquals;

public class CalculadoraMockTeste {

    @Mock
    private Calculadora calcMock;

    @Spy
    private Calculadora calcSpy;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void devoMostrarDiferencaEntreMockSpy() {

        Mockito.when(calcMock.somar(1,2)).thenReturn(5);
        Mockito.when(calcSpy.somar(1,2)).thenReturn(5);
        //tem como o mock chamar a implementação real do método
        //quando somar 1, 2 entao retorne o método real
        Mockito.when(calcMock.somar(1, 2)).thenCallRealMethod();

        //fique atento com o spy e o mock pois o java executa por partes a linha
        //iniciando pelos parenteses logo se no metodo somar tivesse uma impressao
        //ele executaria a impressao e depois apareceria o retorn
        //para resolver isso usamos o
        Mockito.doReturn(5).when(calcSpy).somar(1,2);

        Mockito.doNothing().when(calcSpy).imprime();
        //no caso de um método void para que o spy não execute usamos o
        //doNothing informando o método que não quero que execute

        System.out.println(calcMock.somar(1,2));
        System.out.println(calcSpy.somar(1,2));
        //quando você grava uma expectativa com mock e os valores passados
        //sao diferentes dos informados o mock returna o valor padrao de acordo
        //com o tipo de variavel como no exemplo é inteiro ele returna 0 quando
        //você passa valores diferentes
        System.out.println(calcMock.somar(1,6));
        //diferente do mock o spy quando não recebe os valores passados
        //ele executa a operação real do método logo o valor será a soma correta
        System.out.println(calcSpy.somar(1,3));

        //tenha muita atenção pois o spy diferente do mock não pode
        //ser implementado em interfaces somente em classes

        System.out.println("Mock: ");
        //padrao do mock quando tem retorno ele executa o retorno e quando é o método
        //é void ele não faz nada
        calcMock.imprime();
        System.out.println("Spy: ");
        calcSpy.imprime();
    }

    @Test
    public void teste() {

        Calculadora calculadora = Mockito.mock(Calculadora.class);
        ArgumentCaptor<Integer> argCapt = ArgumentCaptor.forClass(Integer.class);
        Mockito.when(calculadora.somar(argCapt.capture(), argCapt.capture())).thenReturn(5);
        //Mockito.when(calculadora.somar(Mockito.anyInt(), Mockito.anyInt())).thenReturn(5);
        //Mockito.when(calculadora.somar(Mockito.eq(1), Mockito.anyInt())).thenReturn(5);
        //quando eu quero que o primeiro parametro seja um numero especifico(fixar valor) eu uso Mockito.eq
        //Mockito.anyInt significa qualquer numero inteiro
        //Mockito.when(calculadora.somar(1, Mockito.anyInt())).thenReturn(5);
        //quando usamos o matcher Mockito.anyInt no exemplo como temos dois valores
        //não posso usar um valor com matcher e outro sem

        assertEquals(5, calculadora.somar(1, 8));
        System.out.println(argCapt.getAllValues());
    }
}
