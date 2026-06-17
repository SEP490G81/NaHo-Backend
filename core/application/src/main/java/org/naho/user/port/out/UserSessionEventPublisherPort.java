package org.naho.user.port.out;

import org.naho.user.command.ForceLogoutCommand;

public interface UserSessionEventPublisherPort {
    void publishForceLogoutEvent(ForceLogoutCommand command);
}
