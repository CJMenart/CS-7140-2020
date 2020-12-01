package org.stathissideris.ascii2image.test;

import org.junit.Before;
import org.junit.Test;
import org.stathissideris.ascii2image.core.DebugUtils;
import org.stathissideris.ascii2image.text.AbstractionGrid;
import org.stathissideris.ascii2image.text.CellSet;
import org.stathissideris.ascii2image.text.TextGrid;

import java.io.FileNotFoundException;
import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class DebugUtilsTest {
	
	@Before public void setUp() {
	}

	@Test
	public void lineNumberTest() {
		int lnum = DebugUtils.getLineNumber();
		assertEquals(22, lnum);
	}
}
