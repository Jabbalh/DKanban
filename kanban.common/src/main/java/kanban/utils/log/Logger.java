package kanban.utils.log;


import java.util.function.Supplier;

import io.vertx.core.logging.LoggerFactory;


public class Logger {

	private io.vertx.core.logging.Logger logger;
	
	public static <T> Logger Get(Class<T> clazz){
		Logger result = new Logger();
		result.logger = LoggerFactory.getLogger(clazz);
		return result;
	}
	
	public void info(java.util.function.Supplier<String> message){
		logger.info(message.get());
	}
	
	public void debug(Supplier<String> message) {
		if (logger.isDebugEnabled()){
			logger.debug(message.get());
		}
	}
	
	public void error(Supplier<String> message){
		logger.error(message.get());
	}
	
	public void error(Supplier<String> message, Throwable cause){
		logger.error(message.get(), cause);
	}
	
	
}
