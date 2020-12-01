package org.stathissideris.ascii2image.test;

import org.apache.commons.cli.*;
import org.junit.Before;
import org.junit.Test;
import org.stathissideris.ascii2image.core.ConversionOptions;
import org.stathissideris.ascii2image.core.ProcessingOptions;
import org.stathissideris.ascii2image.core.RenderingOptions;
import org.stathissideris.ascii2image.text.AbstractionGrid;
import org.stathissideris.ascii2image.text.CellSet;
import org.stathissideris.ascii2image.text.TextGrid;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import static org.junit.Assert.assertEquals;

public class ConversionOptionsTest {

	CommandLineParser parser = new PosixParser();

	@Before public void setUp() {
	}

	@Test
	public void renderingOpParseTest() throws ParseException, UnsupportedEncodingException {
		Options cmdLnOptions = new Options();
		cmdLnOptions.addOption(
				OptionBuilder.withLongOpt("scale")
						.withDescription("A natural number that determines the size of the rendered image. The units are fractions of the default size (2.5 renders 1.5 times bigger than the default).")
						.hasArg()
						.withArgName("SCALE")
						.create('s')
		);
		String[] args = new String[]{"-scale","3"};
		CommandLine cmdLine = parser.parse(cmdLnOptions, args);

		ConversionOptions conversionOptions = new ConversionOptions(cmdLine);

		assertEquals(3, conversionOptions.renderingOptions.getScale(), 0.0);
	}

	@Test
	public void conversionOpParseTest() throws ParseException, UnsupportedEncodingException {
		Options cmdLnOptions = new Options();
		cmdLnOptions.addOption(
				OptionBuilder.withLongOpt("encoding")
						.withDescription("The encoding of the input file.")
						.hasArg()
						.withArgName("ENCODING")
						.create('e')
		);
		String[] args = new String[]{"-encoding","ASCII"};
		CommandLine cmdLine = parser.parse(cmdLnOptions, args);

		ConversionOptions conversionOptions = new ConversionOptions(cmdLine);

		assertEquals("ASCII",conversionOptions.processingOptions.getCharacterEncoding());
	}

}
