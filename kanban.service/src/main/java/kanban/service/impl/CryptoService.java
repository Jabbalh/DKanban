package kanban.service.impl;

import com.google.common.base.Charsets;
import com.google.common.hash.Hashing;

import kanban.service.contract.ICryptoService;

/**
 * Service permettant de générer un hash
 * @author Nico
 *
 */
public class CryptoService implements ICryptoService {

	/**
	 * Génère un hash avec l'algo SHA256
	 */
	@Override
	public String genHash256(String password){
		return Hashing.sha256()
		        .hashString(password, Charsets.UTF_8)
		        .toString();
	}
	
	/**
	 * Compare une chaine avec un hash (algo utilisé SHA256)
	 */
	@Override
	public Boolean compareWithHash256(String password, String hash256){
		String passwordHash = genHash256(password);
		return passwordHash.equals(hash256);
	}
	
	
	
	
}
