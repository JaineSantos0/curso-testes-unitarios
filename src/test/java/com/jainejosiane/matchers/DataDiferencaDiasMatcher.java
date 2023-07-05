package com.jainejosiane.matchers;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.jainejosiane.utils.DataUtils;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

public class DataDiferencaDiasMatcher extends TypeSafeMatcher<Date> {
	
	private Integer qtdDias;
	
	public DataDiferencaDiasMatcher(Integer qtdDias) {
		this.qtdDias = qtdDias;
	}

	@Override
	public void describeTo(Description description) {

		Date dataEsperada = DataUtils.obterDataComDiferencaDias(qtdDias);
		SimpleDateFormat format = new SimpleDateFormat("dd/MM/YYYY");

		description.appendText(format.format(dataEsperada));
	}

	@Override
	protected boolean matchesSafely(Date data) {
		return DataUtils.isMesmaData(data, DataUtils.obterDataComDiferencaDias(qtdDias));
	}

}
