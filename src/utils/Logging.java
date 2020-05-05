package utils;

import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Logging {
	private Logger log;
	
	public Logging(Logger logger) {
		super();
		log = logger;
	}

	public void configOutputLogger() {
		log.setUseParentHandlers(false);
		log.setLevel(Level.FINER);
		ConsoleHandler handler = new ConsoleHandler();
		handler.setLevel(Level.FINER);
        log.addHandler(handler);
	}
	
	public void showOutput(String msg) {
		log.info(msg);
		log.getHandlers()[0].flush();
	}
}
