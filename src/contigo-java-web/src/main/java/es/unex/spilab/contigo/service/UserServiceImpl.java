package es.unex.spilab.contigo.service;

import es.unex.spilab.contigo.model.UserLogin;
import es.unex.spilab.contigo.model.UserRole;
import es.unex.spilab.contigo.repository.RoleRepository;
import es.unex.spilab.contigo.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;

@Service
public class UserServiceImpl implements UserService {
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	@Override
	public void save(UserLogin user) {
		user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
		// user.setRoles(new HashSet<>(roleRepository.findAll()));
		UserRole ur = roleRepository.findByName("ROLE_USER");
		user.setRoles(new HashSet<>());
		user.getRoles().add(ur);

		userRepository.save(user);
	}

	@Override
	public UserLogin findByUsername(String username) {
		return userRepository.findByUsername(username);
	}
}
