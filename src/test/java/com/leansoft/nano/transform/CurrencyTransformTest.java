package com.leansoft.nano.transform;

import java.util.Currency;
import java.util.Locale;

import com.leansoft.nano.transform.CurrencyTransform;

import junit.framework.TestCase;

public class CurrencyTransformTest extends TestCase {
	public void testCurrency() throws Exception {
	      Currency currency = Currency.getInstance(Locale.UK);
	      CurrencyTransform format = new CurrencyTransform();
	      String value = format.write(currency);
	      Currency copy = format.read(value);
	      
	      assertEquals(currency, copy);
	}
}
