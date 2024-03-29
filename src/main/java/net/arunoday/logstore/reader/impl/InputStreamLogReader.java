package net.arunoday.logstore.reader.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import net.arunoday.logstore.reader.LogReader;

/**
 * @author Aparna Chaudhary
 * 
 */
public class InputStreamLogReader implements LogReader {

	BufferedReader bin;

	public void close() throws IOException {
		bin.close();
	}

	public void init(InputStream in) throws Exception {
		bin = new BufferedReader(new InputStreamReader(in));

	}

	public String readLine() throws IOException {
		String line = bin.readLine();
		return line;
	}

	public InputStreamLogReader(InputStream in) throws Exception {
		init(in);
	}
}
