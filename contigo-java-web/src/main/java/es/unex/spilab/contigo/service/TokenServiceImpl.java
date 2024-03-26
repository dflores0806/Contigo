package es.unex.spilab.contigo.service;

import es.unex.spilab.contigo.model.UserAccess;
import es.unex.spilab.contigo.repository.UserAccessRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TokenServiceImpl implements TokenService {

	@Autowired
	private UserAccessRepository userAccessRepository;

	@Override
	public void save(UserAccess ua) {
		userAccessRepository.save(ua);
	}

	@Override
	public UserAccess findByToken(String token) {
		return userAccessRepository.findByToken(token);
	}

	@Override
	public boolean deleteToken(String token) {
		UserAccess u = userAccessRepository.findByToken(token);
		System.out.println(token + "-->useraccess=" + u);
		userAccessRepository.delete(u);
		return false;
	}
}
