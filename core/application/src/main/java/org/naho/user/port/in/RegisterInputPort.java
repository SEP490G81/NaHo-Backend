package org.naho.user.port.in;

import org.naho.user.command.RegisterCommand;

public interface RegisterInputPort {
    void register(RegisterCommand registerCommand);
}
