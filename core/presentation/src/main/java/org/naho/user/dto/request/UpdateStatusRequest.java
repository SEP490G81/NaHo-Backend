package org.naho.user.dto.request;

import org.naho.user.type.UserStatus;

public record UpdateStatusRequest(UserStatus newStatus) {
}
