package es.unex.spilab.contigo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import es.unex.spilab.contigo.model.UserLogin;

public interface UserRepository extends JpaRepository<UserLogin, Long> {
	UserLogin findByUsername(String username);
}
