package org.naho.user.mapper;

import org.mapstruct.Mapper;
import org.naho.user.valueobject.Dob;
import org.naho.user.valueobject.Email;
import org.naho.user.valueobject.Username;

import java.time.LocalDate;

@Mapper(componentModel = "spring")
public interface UserValueObjectMapper {
    default Username mapUsername(String value) {
        return value == null ? null : Username.of(value);
    }

    default String mapUsername(Username value) {
        return value == null ? null : value.getValue();
    }

    default Email mapEmail(String value) {
        return value == null ? null : Email.of(value);
    }

    default String mapEmail(Email value) {
        return value == null ? null : value.getValue();
    }

    default Dob mapDob(LocalDate value) {
        return value == null ? null : Dob.of(value);
    }

    default LocalDate mapDob(Dob value) {
        return value == null ? null : value.getValue();
    }
}
