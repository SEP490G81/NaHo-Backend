package org.naho.persona.repository;

import org.naho.persona.entity.PersonaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PersonaJpaRepository extends JpaRepository<PersonaEntity, Long> {
}
