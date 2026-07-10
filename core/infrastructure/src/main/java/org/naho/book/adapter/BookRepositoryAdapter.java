package org.naho.book.adapter;

import lombok.RequiredArgsConstructor;
import org.naho.book.port.out.BookRepositoryPort;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BookRepositoryAdapter implements BookRepositoryPort {

}
