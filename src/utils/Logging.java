package utils;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.logging.StreamHandler;

public class Logging {
	private Logger LOGGER;
	
	public Logging(Logger logger) {
		super();
		LOGGER = logger;
	}

	public void configOutputLogger() {
		LOGGER.setUseParentHandlers(false);
		LOGGER.setLevel(Level.FINER);
        LOGGER.addHandler(new StreamHandler(System.out, new SimpleFormatter()));
	}
	
	public void showOutput(String msg) {
		LOGGER.info(msg);
		LOGGER.getHandlers()[0].flush();
	}
}
