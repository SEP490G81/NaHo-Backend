package org.naho.payment.port.in;

import org.naho.payment.command.ConfirmPaymentCommand;
import org.naho.payment.result.ConfirmPaymentResult;

public interface ConfirmPaymentInputPort {
    ConfirmPaymentResult confirmPayment(ConfirmPaymentCommand command);
}
