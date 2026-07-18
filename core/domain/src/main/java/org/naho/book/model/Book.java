package org.naho.book.model;

import org.naho.book.exception.BookDomainErrorCode;
import org.naho.book.type.CefrLevel;
import org.naho.i18n.message.book.BookDetailMessageKey;
import org.naho.shared.exception.DomainException;
import org.naho.user.type.JLPTLevel;

public class Book {

    private final Long id;
    private final Long coverImageFileId;
    private final JLPTLevel jlptLevel;
    private final CefrLevel cefrLevel;
    private final Double orderIndex;
    private final Double firstNodeGlobalOrderIndex;
    private final Double lastNodeGlobalOrderIndex;
    private String title;
    private String description;

    private Book(Builder builder) {
        this.id = builder.id;
        this.coverImageFileId = builder.coverImageFileId;
        this.title = builder.title;
        this.description = builder.description;
        this.jlptLevel = builder.jlptLevel;
        this.cefrLevel = builder.cefrLevel;
        this.orderIndex = builder.orderIndex;
        this.firstNodeGlobalOrderIndex = builder.firstNodeGlobalOrderIndex;
        this.lastNodeGlobalOrderIndex = builder.lastNodeGlobalOrderIndex;
    }

    public static Builder builder() {
        return new Builder();
    }

    public Long getId() {
        return id;
    }

    public Long getCoverImageFileId() {
        return coverImageFileId;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public JLPTLevel getJlptLevel() {
        return jlptLevel;
    }

    public CefrLevel getCefrLevel() {
        return cefrLevel;
    }

    public Double getOrderIndex() {
        return orderIndex;
    }

    public Double getFirstNodeGlobalOrderIndex() {
        return firstNodeGlobalOrderIndex;
    }

    public Double getLastNodeGlobalOrderIndex() {
        return lastNodeGlobalOrderIndex;
    }

    public static final class Builder {

        private Long id;
        private Long coverImageFileId;
        private String title;
        private String description;
        private JLPTLevel jlptLevel;
        private CefrLevel cefrLevel;
        private Double orderIndex;
        private Double firstNodeGlobalOrderIndex;
        private Double lastNodeGlobalOrderIndex;

        private Builder() {
        }

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder coverImageFileId(Long coverImageFileId) {
            this.coverImageFileId = coverImageFileId;
            return this;
        }

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder jlptLevel(JLPTLevel jlptLevel) {
            this.jlptLevel = jlptLevel;
            return this;
        }

        public Builder cefrLevel(CefrLevel cefrLevel) {
            this.cefrLevel = cefrLevel;
            return this;
        }

        public Builder orderIndex(Double orderIndex) {
            this.orderIndex = orderIndex;
            return this;
        }

        public Builder firstNodeGlobalOrderIndex(Double firstNodeGlobalOrderIndex) {
            this.firstNodeGlobalOrderIndex = firstNodeGlobalOrderIndex;
            return this;
        }

        public Builder lastNodeGlobalOrderIndex(Double lastNodeGlobalOrderIndex) {
            this.lastNodeGlobalOrderIndex = lastNodeGlobalOrderIndex;
            return this;
        }

        public Book build() {
            if (title == null || title.isBlank()) {
                throw new DomainException(
                        BookDomainErrorCode.BOOK_TITLE_EMPTY,
                        BookDetailMessageKey.BOOK_TITLE_EMPTY
                );
            }

            if (jlptLevel == null) {
                throw new DomainException(
                        BookDomainErrorCode.BOOK_JLPT_LEVEL_EMPTY,
                        BookDetailMessageKey.BOOK_JLPT_LEVEL_EMPTY
                );
            }

            if (cefrLevel == null) {
                throw new DomainException(
                        BookDomainErrorCode.BOOK_CEFR_LEVEL_EMPTY,
                        BookDetailMessageKey.BOOK_CEFR_LEVEL_EMPTY
                );
            }

            if (orderIndex == null) {
                throw new DomainException(
                        BookDomainErrorCode.BOOK_ORDER_INDEX_EMPTY,
                        BookDetailMessageKey.BOOK_ORDER_INDEX_EMPTY
                );
            }

            return new Book(this);
        }
    }
}