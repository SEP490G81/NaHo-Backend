package org.naho.payment.port.in;

import org.naho.payment.command.CancelPaymentCommand;
import org.naho.payment.result.CancelPaymentResult;

public interface CancelPaymentInputPort {
    CancelPaymentResult cancelPayment(CancelPaymentCommand command);
}
