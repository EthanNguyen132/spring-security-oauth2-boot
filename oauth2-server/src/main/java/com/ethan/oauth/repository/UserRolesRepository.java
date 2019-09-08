package com.ethan.oauth.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ethan.oauth.model.UserRoles;

@Repository
public interface UserRolesRepository extends JpaRepository<UserRoles, Long> {

	List<UserRoles> findByUserUsername(String username);

}
