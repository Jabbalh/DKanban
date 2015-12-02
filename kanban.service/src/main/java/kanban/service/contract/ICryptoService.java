package kanban.service.contract;

public interface ICryptoService {

	Boolean compareWithHash256(String password, String hash256);

	String genHash256(String password);

}
