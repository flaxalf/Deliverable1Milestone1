package utils;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.logging.StreamHandler;

public class Logging {
	private Logger log;
	
	public Logging(Logger logger) {
		super();
		log = logger;
	}

	public void configOutputLogger() {
		log.setUseParentHandlers(false);
		log.setLevel(Level.FINER);
        log.addHandler(new StreamHandler(System.out, new SimpleFormatter()));
	}
	
	public void showOutput(String msg) {
		log.info(msg);
		log.getHandlers()[0].flush();
	}
}
