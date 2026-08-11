package org.naho.user.command;

import org.naho.i18n.message.user.UserDetailMessageKey;
import org.naho.shared.constant.SortDirection;
import org.naho.shared.exception.ApplicationException;
import org.naho.user.constant.UserSortColumn;
import org.naho.user.exception.UserErrorCode;
import org.naho.user.type.Gender;
import org.naho.user.type.UserStatus;

import java.time.LocalDate;

public record UserQueryCommand(
        Integer page,
        Integer size,
        UserSortColumn sortColumn,
        SortDirection sortDirection,
        String searchKeyword,
        Gender gender,
        LocalDate dobFrom,
        LocalDate dobTo,
        UserStatus status,
        Long roleId,
        Boolean isEmailVerified
) {
    public UserQueryCommand {
        if (page < 0) {
            throw new ApplicationException(
                    UserErrorCode.USER_PAGE_INVALID,
                    UserDetailMessageKey.USER_PAGE_INVALID
            );
        }

        if (size < 20 || size > 100) {
            throw new ApplicationException(
                    UserErrorCode.USER_SIZE_INVALID,
                    UserDetailMessageKey.USER_SIZE_INVALID
            );
        }

        if (dobFrom != null && dobTo != null && dobFrom.isAfter(dobTo)) {
            throw new ApplicationException(
                    UserErrorCode.USER_DOB_RANGE_INVALID,
                    UserDetailMessageKey.USER_DOB_RANGE_INVALID
            );
        }
    }
}

