package org.naho.user.port.in;

import org.naho.user.command.ResendOtpCommand;

public interface ResendOtpInputPort {
    void resendOtp(ResendOtpCommand command);
}
