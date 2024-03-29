package net.arunoday.logstore.parser;

import org.springframework.stereotype.Component;

/**
 * @author Aparna Chaudhary
 * 
 */
@Component("parserDescription")
public class ParserDescription {

	private String displayName;
	private String description;
	private int menmonic;
	private String keyStrokeAccelelator;
	private String charset;
	private String file;

	public String getFile() {
		return file;
	}

	public void setFile(String file) {
		this.file = file;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getMenmonic() {
		return menmonic;
	}

	public void setMenmonic(int menmonic) {
		this.menmonic = menmonic;
	}

	public String getKeyStrokeAccelelator() {
		return keyStrokeAccelelator;
	}

	public void setKeyStrokeAccelelator(String keyStrokeAccelelator) {
		this.keyStrokeAccelelator = keyStrokeAccelelator;
	}

	public String getCharset() {
		return charset;
	}

	public void setCharset(String charset) {
		this.charset = charset;
	}

}
