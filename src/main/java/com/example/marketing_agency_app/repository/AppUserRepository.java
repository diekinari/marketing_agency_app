package com.example.marketing_agency_app.repository;

import com.example.marketing_agency_app.model.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AppUserRepository extends JpaRepository<AppUser, Long> {
    AppUser findByUsername(String username);
    AppUser findByEmail(String email);

    @Query("SELECT u FROM AppUser u ORDER BY u.username")
    List<AppUser> findAllOrdered();
}
