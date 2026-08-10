package org.naho.user.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.naho.shared.constant.SortDirection;
import org.naho.user.constant.UserSortColumn;
import org.naho.user.type.Gender;
import org.naho.user.type.UserStatus;

import java.time.LocalDate;

@Getter
@Setter
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
public class UserQueryRequest {
    Integer page = 0;
    Integer size = 20;
    UserSortColumn sortColumn = UserSortColumn.ID;
    SortDirection sortDirection = SortDirection.ASC;
    String searchKeyword;
    Gender gender;
    LocalDate dobFrom;
    LocalDate dobTo;
    UserStatus status;
    Long roleId;
    Boolean isEmailVerified;
}
