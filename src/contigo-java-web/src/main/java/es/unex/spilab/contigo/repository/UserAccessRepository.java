package es.unex.spilab.contigo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import es.unex.spilab.contigo.model.UserAccess;

public interface UserAccessRepository extends JpaRepository<UserAccess, Long> {
	UserAccess findByToken(String username);
}
