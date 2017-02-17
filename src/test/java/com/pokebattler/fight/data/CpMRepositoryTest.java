package com.pokebattler.fight.data;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class CpMRepositoryTest {
	CpMRepository cpm;
	@Before
	public void setup() {
		cpm = new CpMRepository();
	}
	
	@Test
	public void testGetCpM() {
		assertEquals(79,cpm.cpMap.size());
	}

}
