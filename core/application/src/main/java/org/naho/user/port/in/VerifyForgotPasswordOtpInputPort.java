package org.naho.user.port.in;

import org.naho.user.command.VerifyForgotPasswordOtpCommand;

public interface VerifyForgotPasswordOtpInputPort {
    String verifyOtp(VerifyForgotPasswordOtpCommand command);
}
