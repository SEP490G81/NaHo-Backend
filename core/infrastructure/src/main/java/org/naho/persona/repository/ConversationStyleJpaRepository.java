package org.naho.persona.repository;

import org.naho.persona.entity.ConversationStyleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConversationStyleJpaRepository extends JpaRepository<ConversationStyleEntity, Long> {
}
