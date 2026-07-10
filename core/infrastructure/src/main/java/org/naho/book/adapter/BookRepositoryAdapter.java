package org.naho.book.adapter;

import org.naho.book.entity.BookEntity;
import org.naho.book.model.Book;
import org.naho.book.port.out.BookRepositoryPort;
import org.naho.book.repository.BookJpaRepository;
import org.naho.book.mapper.BookEntityMapper;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BookRepositoryAdapter implements BookRepositoryPort {

    private final BookJpaRepository bookJpaRepository;
    private final BookEntityMapper bookEntityMapper;

    public BookRepositoryAdapter(BookJpaRepository bookJpaRepository,
                                  BookEntityMapper bookEntityMapper) {
        this.bookJpaRepository = bookJpaRepository;
        this.bookEntityMapper = bookEntityMapper;
    }

    @Override
    public List<Book> findAllBooks() {
        Sort sort = Sort.by(Sort.Direction.ASC, "orderIndex");
        return bookJpaRepository.findAll(sort).stream()
                .map(bookEntityMapper::entityToDomain)
                .toList();
    }
}
