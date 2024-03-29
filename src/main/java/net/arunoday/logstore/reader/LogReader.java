package net.arunoday.logstore.reader;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author Aparna Chaudhary
 * 
 */
public interface LogReader {

	public void init(InputStream in) throws Exception;

	public void close() throws IOException;

	public String readLine() throws IOException;
}
