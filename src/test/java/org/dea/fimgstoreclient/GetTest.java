package org.dea.fimgstoreclient;

import java.io.IOException;

import org.junit.Test;

public class GetTest {

	private static final int TEST_SIZE = 20;
	private static FimgStoreGetClient old = new FimgStoreGetClient("dbis-thure.uibk.ac.at", "/f");
	private static FimgStoreGetClient curr = new FimgStoreGetClient("files-test.transkribus.eu", "/");

	// FIXME file is not on files-test, dbis-thure/f is not running anymore...
	private static String[] keys = {
			"POTANHIPEPGQLFMDTRJHICMX", // image
			"HJKTCFUBRDSDTJGKRDVICRJE", // XML
	};
	
	
	
//	@Test
	public void test1() throws IOException {
		for(int i = 0; i < TEST_SIZE; i++) {
			old.saveFile(keys[0], "/tmp/");
			old.saveFile(keys[1], "/tmp/");
		}
	}
	
//	@Test
	public void test2() throws IOException {
		for(int i = 0; i < TEST_SIZE; i++) {
			curr.saveFile(keys[0], "/tmp/");
			curr.saveFile(keys[1], "/tmp/");
		}
	}

}
