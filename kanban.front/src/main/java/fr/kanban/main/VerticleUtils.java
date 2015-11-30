package fr.kanban.main;

import com.englishtown.vertx.hk2.HK2VerticleFactory;

import fr.bootstrap.BootstrapBinder;
import io.vertx.core.AsyncResult;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

public class VerticleUtils  {
	private final static Logger logger = LoggerFactory.getLogger(VerticleUtils.class);
	
	public static void DeployeVertical(Vertx vertx, Class<?> clazz) {
		DeploymentOptions option = new DeploymentOptions();
		
		//option.setInstances(8);
		JsonObject config = new JsonObject();
		config.put("hk2_binder", BootstrapBinder.class.getCanonicalName());
		//config.put("vertx.disableFileCaching", "true");
		option.setConfig(config);
		option.setWorker(false);
		vertx.deployVerticle(getDeployementName(clazz), option, x -> {
			if (x.succeeded()){
				logger.info(String.format("Verticle %s ... OK", clazz.getName()));				
			}
			else if (x.failed()){
				logger.error(String.format("Verticle %s ... Failed", clazz.getName()));
				logger.error(x.cause());
			}
		});
	}
	
	public static void DeployeVertical(Vertx vertx, Class<?> clazz, Handler<AsyncResult<String>> callback) {
		DeploymentOptions option = new DeploymentOptions();
		
		//option.setInstances(8);
		JsonObject config = new JsonObject();
		config.put("hk2_binder", BootstrapBinder.class.getCanonicalName());
		//config.put("vertx.disableFileCaching", "true");
		option.setConfig(config);
		option.setWorker(false);
		vertx.deployVerticle(getDeployementName(clazz), option,callback);
	}
	
	/***
	 * Renvois le nom à utiliser pour le déployement du Verticle
	 * @param clazz
	 * @return
	 */
	public static String getDeployementName(Class<?> clazz) {
		return String.format("%s:%s",HK2VerticleFactory.PREFIX,clazz.getCanonicalName()); 
	}

}
