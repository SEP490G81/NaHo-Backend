package org.naho.user.port.in;

import org.naho.user.command.RegisterCommand;
import org.naho.user.result.RegisterResult;

public interface RegisterInputPort {
    RegisterResult register(RegisterCommand registerCommand);
}
