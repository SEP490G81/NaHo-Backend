package org.naho.chest.model;

import org.naho.i18n.message.chest.ChestDetailMessageKey;
import org.naho.chest.exception.ChestDomainErrorCode;
import org.naho.shared.exception.DomainException;

public class Chest {

    private final Long id;
    private String title;
    private String description;
    private Double point;

    private Chest(Builder builder) {
        this.id = builder.id;
        this.title = builder.title;
        this.description = builder.description;
        this.point = builder.point;
    }

    public static Builder builder() {
        return new Builder();
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getPoint() {
        return point;
    }

    public void setPoint(Double point) {
        this.point = point;
    }

    public static final class Builder {

        private Long id;
        private String title;
        private String description;
        private Double point;

        private Builder() {
        }

        public Builder id(Long id) {
            this.id = id;
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

        public Builder point(Double point) {
            this.point = point;
            return this;
        }

        public Chest build() {
            if (title == null || title.isBlank()) {
                throw new DomainException(
                        ChestDomainErrorCode.CHEST_TITLE_EMPTY,
                        ChestDetailMessageKey.CHEST_TITLE_EMPTY
                );
            }

            if (point == null || point <= 0) {
                throw new DomainException(
                        ChestDomainErrorCode.CHEST_POINT_INVALID,
                        ChestDetailMessageKey.CHEST_POINT_INVALID
                );
            }
            return new Chest(this);
        }
    }
}
