package org.naho.book.repository;

import org.naho.book.entity.BookEntity;
import org.naho.shared.persistence.BaseJpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookJpaRepository extends BaseJpaRepository<BookEntity> {
}
