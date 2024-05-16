package es.unex.spilab.contigo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import es.unex.spilab.contigo.model.UserRole;

public interface RoleRepository extends JpaRepository<UserRole, Long> {
	UserRole findByName(String name);
}
