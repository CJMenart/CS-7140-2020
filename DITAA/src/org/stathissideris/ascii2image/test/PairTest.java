package org.stathissideris.ascii2image.test;

import org.junit.Before;
import org.junit.Test;
import org.stathissideris.ascii2image.core.DebugUtils;
import org.stathissideris.ascii2image.core.Pair;

import static org.junit.Assert.assertEquals;

public class PairTest {
	
	@Before public void setUp() {
	}

	@Test
	public void pairTest() {
		Integer x = 5;
		String y = "Hello World";
		Pair<Integer, String> pair = new Pair<>(x, y);
		assertEquals(x, pair.first);
		assertEquals(y, pair.second);
	}
}
