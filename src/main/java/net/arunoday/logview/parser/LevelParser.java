package net.arunoday.logview.parser;

import java.util.HashMap;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Level;

public class LevelParser {

	private HashMap<String, Level> levels;

	public LevelParser(Locale locale) {
		levels = new HashMap<String, Level>(8);
		ResourceBundle rb = ResourceBundle.getBundle(
				"net.arunoday.logview.parser.Levels", locale);
		levels.put(rb.getString("FINEST"), Level.FINEST);
		levels.put(rb.getString("FINER"), Level.FINER);
		levels.put(rb.getString("FINE"), Level.FINE);
		levels.put(rb.getString("INFO"), Level.INFO);
		levels.put(rb.getString("CONFIG"), Level.CONFIG);
		levels.put(rb.getString("WARNING"), Level.WARNING);
		levels.put(rb.getString("SEVERE"), Level.SEVERE);
		levels.put(rb.getString("TRACE"), Level.FINEST);
		levels.put(rb.getString("DEBUG"), Level.FINE);
		levels.put(rb.getString("INFO"), Level.INFO);
		levels.put(rb.getString("WARN"), Level.WARNING);
		levels.put(rb.getString("ERROR"), Level.SEVERE);
		levels.put(rb.getString("FATAL"), Level.SEVERE);
	}

	public Level parse(String string) {
		return levels.get(string);
	}

}
