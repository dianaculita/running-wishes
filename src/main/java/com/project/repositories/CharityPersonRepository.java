package com.project.repositories;

import com.project.models.CharityPerson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CharityPersonRepository extends JpaRepository<CharityPerson, Long> {

    Optional<CharityPerson> findByPersonCnp(String cnp);
}
