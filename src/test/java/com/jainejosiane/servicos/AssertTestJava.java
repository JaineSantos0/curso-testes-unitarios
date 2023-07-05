package com.jainejosiane.servicos;

import org.junit.Assert;
import org.junit.Test;

import com.jainejosiane.entidades.Usuario;

public class AssertTestJava {

	@Test
	public void  test() {
		
		Assert.assertTrue(true); // poderia usar a nega��o com ! mas n�o � o indicado pois temos o assert false
		Assert.assertFalse(false);
		
		Assert.assertEquals(5.0, 5.0, 0.01); // primeiro o valor esperado, depois o atual, depois a margem de erro por ser decimal
		//Assert.assertNotEquals(5.0, 5.0, 0.01);
		
		int i = 5;
		Integer i2 = 5;
		
		Assert.assertEquals(Integer.valueOf(i), i2);
		Assert.assertEquals(i, i2.intValue());
		
		Assert.assertEquals("morango", "morango");
		Assert.assertTrue("morango".equalsIgnoreCase("Morango"));
		Assert.assertTrue("morango".startsWith("mo"));
		
		Usuario us1 = new Usuario("Usuario 1");
		Usuario us2 = new Usuario("Usuario 1");
		
		Assert.assertEquals(us1, us2);
		
		//Assert.assertSame(us1, us2);
	}
}
