package es.unex.spilab.contigo.service;

import es.unex.spilab.contigo.model.UserAccess;

public interface TokenService {
	void save(UserAccess ua);

	UserAccess findByToken(String token);

	boolean deleteToken(String token);
}
