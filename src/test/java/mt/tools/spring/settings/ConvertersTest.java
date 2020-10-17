package mt.tools.spring.settings;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import mt.tools.spring.settings.Converters.BooleanConverter;
import mt.tools.spring.settings.Converters.ByteConverter;
import mt.tools.spring.settings.Converters.CharacterConverter;
import mt.tools.spring.settings.Converters.DoubleConverter;
import mt.tools.spring.settings.Converters.FloatConverter;
import mt.tools.spring.settings.Converters.IntegerConverter;
import mt.tools.spring.settings.Converters.LongConverter;
import mt.tools.spring.settings.Converters.ShortConverter;
import mt.tools.spring.settings.Converters.StringConverter;

public class ConvertersTest {

	@Test
	public void booleanConverter() {
		BooleanConverter con = new Converters.BooleanConverter();

		assertTrue(con.supports("bool"));
		assertTrue(con.supports("booLean"));
		assertTrue(con.supports("JAVA.LANG.BOOLEAN"));
		assertTrue("true".equalsIgnoreCase(con.toTextValue(Boolean.TRUE)));
		assertTrue("false".equalsIgnoreCase(con.toTextValue(Boolean.FALSE)));

		assertEquals(Boolean.TRUE, con.toObject("true"));
		assertEquals(Boolean.TRUE, con.toObject("TRUE"));
		assertEquals(Boolean.FALSE, con.toObject("false"));
	}

	@Test
	public void byteConverter() {
		ByteConverter con = new Converters.ByteConverter();

		assertTrue(con.supports("byte"));
		assertTrue(con.supports("JAVA.LANG.Byte"));

		Byte expected = (byte) 10;
		assertEquals(expected, con.toObject("10"));
		assertEquals("10", con.toTextValue(expected));
	}

	@Test
	public void shortConverter() {
		ShortConverter con = new Converters.ShortConverter();

		assertTrue(con.supports("short"));
		assertTrue(con.supports("JAVA.LANG.Short"));

		Short expected = (short) -11;
		assertEquals(expected, con.toObject("-11"));
		assertEquals("-11", con.toTextValue(expected));
	}

	@Test
	public void integerConverter() {
		IntegerConverter con = new Converters.IntegerConverter();

		assertTrue(con.supports("int"));
		assertTrue(con.supports("Integer"));
		assertTrue(con.supports("JAVA.LANG.integer"));

		Integer expected = -121;
		assertEquals(expected, con.toObject("-121"));
		assertEquals("-121", con.toTextValue(expected));
	}

	@Test
	public void longConverter() {
		LongConverter con = new Converters.LongConverter();

		assertTrue(con.supports("long"));
		assertTrue(con.supports("JAVA.LANG.long"));

		Long expected = -4593287L;
		assertEquals(expected, con.toObject("-4593287"));
		assertEquals("-4593287", con.toTextValue(expected));
	}

	@Test
	public void floatConverter() {
		FloatConverter con = new Converters.FloatConverter();

		assertTrue(con.supports("float"));
		assertTrue(con.supports("JAVA.LANG.Float"));

		Float expected = 112.131f;
		assertEquals(expected, con.toObject("112.131f"));
		assertEquals("112.131", con.toTextValue(expected));
	}

	@Test
	public void doubleConverter() {
		DoubleConverter con = new Converters.DoubleConverter();

		assertTrue(con.supports("double"));
		assertTrue(con.supports("JAVA.LANG.double"));

		Double expected = 112.131d;
		assertEquals(expected, con.toObject("112.131d"));
		assertEquals("112.131", con.toTextValue(expected));
	}

	@Test
	public void characterConverter() {
		CharacterConverter con = new Converters.CharacterConverter();

		assertTrue(con.supports("char"));
		assertTrue(con.supports("CHARACTER"));
		assertTrue(con.supports("JAVA.LANG.character"));

		Character expected = 'X';
		assertEquals(expected, con.toObject("X"));
		assertEquals("X", con.toTextValue(expected));
	}

	@Test
	public void stringConverter() {
		StringConverter con = new Converters.StringConverter();

		assertTrue(con.supports("string"));
		assertTrue(con.supports("JAVA.LANG.String"));

		String expected = "From a String to the String";
		assertEquals(expected, con.toObject("From a String to the String"));
		assertEquals("From a String to the String", con.toTextValue(expected));
	}

}
