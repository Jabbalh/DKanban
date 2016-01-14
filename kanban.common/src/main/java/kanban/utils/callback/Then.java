package kanban.utils.callback;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

public class Then<T> {
		
	private final static Logger logger = LoggerFactory.getLogger(Then.class);
	
	/**
	 * Liste des règles de passage à appliquer
	 */
	private List<Function<T, Boolean>> rules = new LinkedList<>();
	/**
	 * Fonction à appeler à la fin
	 */
	private Consumer<T> consumer;
	/**
	 * Fonction à appliquer en cas de non respect des règles
	 */
	private Consumer<T> otherwise;
	
	/**
	 * La fonction d'entrée, on attendra la fin de celle-ci pour executer les règles et le consumer
	 */
	private RunnableFunction<MongoCallBack<T>> toDo = null;
	
	
	/**
	 * On lui passe une fonction qui renvois un callback
	 * @param toDo
	 */
	public Then(RunnableFunction<MongoCallBack<T>> toDo) {
		this.toDo = toDo;
	}
	
	/**
	 * Réinitialisation du composant When avec une nouvelle règle de départ
	 * @param toDo
	 * @return
	 */
	public <R> Then<R> When(RunnableFunction<MongoCallBack<R>> toDo){
		return new Then<>(toDo);
	}

	
	
	/**
	 * Ajout d'une règle de passage
	 * @param rule
	 * @return
	 */
	public Then<T> Rule(Function<T, Boolean> rule){
		this.rules.add(rule);
		return this;
	}
	
	/**
	 * Fonction a ex"cuter en cas de non respect des règles
	 * @param other
	 * @return
	 */
	public Then<T> Otherwise(Consumer<T> other){
		this.otherwise = other;
		return this;
	}
	
	/**
	 * Fonciton finale à executer
	 * @param callback
	 */
	public void doThat(Consumer<T> callback){		
		this.consumer = callback;
		toDo.apply().finishHandler(this::apply);
	}

	/**
	 * Execution réelle de l'enchainement de fonction
	 * @param value
	 */
	private void apply(T value)  {
		if (this.consumer != null){
			boolean ruleOk = true;
			for (Function<T, Boolean> rule : rules){
				ruleOk = rule.apply(value);
				if (!ruleOk) break;
			}
			
			if (!ruleOk && otherwise != null){
				otherwise.accept(value);
			} else {			
				this.consumer.accept(value);
			}
		} else {
			logger.error("consumer is null !!");
		}
	}
	
	
	
}
