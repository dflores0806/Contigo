package es.unex.spilab.contigo.service;

import es.unex.spilab.contigo.model.UserLogin;

public interface UserService {
	void save(UserLogin user);

	UserLogin findByUsername(String username);
}
