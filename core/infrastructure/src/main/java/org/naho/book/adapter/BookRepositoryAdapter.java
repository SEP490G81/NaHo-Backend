package org.naho.book.adapter;

import org.naho.book.entity.BookEntity;
import org.naho.book.mapper.BookEntityMapper;
import org.naho.book.model.Book;
import org.naho.book.mybatis.BookQueryMapper;
import org.naho.book.port.out.BookRepositoryPort;
import org.naho.book.repository.BookJpaRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class BookRepositoryAdapter implements BookRepositoryPort {

    private final BookJpaRepository bookJpaRepository;
    private final BookEntityMapper bookEntityMapper;
    private final BookQueryMapper bookQueryMapper;

    public BookRepositoryAdapter(BookJpaRepository bookJpaRepository,
                                 BookEntityMapper bookEntityMapper, BookQueryMapper bookQueryMapper) {
        this.bookJpaRepository = bookJpaRepository;
        this.bookEntityMapper = bookEntityMapper;
        this.bookQueryMapper = bookQueryMapper;
    }

    @Override
    public List<Book> findAllBooks() {
        Sort sort = Sort.by(Sort.Direction.ASC, "orderIndex");
        return bookJpaRepository.findAll(sort).stream()
                .map(bookEntityMapper::entityToDomain)
                .toList();
    }

    @Override
    public Optional<Book> findById(Long bookId) {
        return bookJpaRepository.findById(bookId)
                .map(bookEntityMapper::entityToDomain);
    }

    @Override
    public Optional<Book> findBySpeakingQuestionId(Long speakingQuestionId) {
        return bookQueryMapper
                .findBySpeakingQuestionId(speakingQuestionId)
                .map(bookEntityMapper::entityToDomain);
    }

    @Override
    public void save(Book book) {
        BookEntity entity;
        if (book.getId() != null) {
            entity = bookJpaRepository.findById(book.getId()).orElse(new BookEntity());
            bookEntityMapper.updateEntityFromDomain(book, entity);
        } else {
            entity = bookEntityMapper.domainToEntity(book);
        }
        
        if (book.getCoverImageFileId() != null) {
            org.naho.file.entity.FileEntity fileEntity = new org.naho.file.entity.FileEntity();
            fileEntity.setId(book.getCoverImageFileId());
            entity.setCoverImageFile(fileEntity);
        } else {
            entity.setCoverImageFile(null);
        }
        
        bookJpaRepository.save(entity);
    }
}
