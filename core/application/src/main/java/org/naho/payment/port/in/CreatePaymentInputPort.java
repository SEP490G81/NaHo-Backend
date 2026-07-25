package org.naho.payment.port.in;

import org.naho.payment.command.CreatePaymentCommand;
import org.naho.payment.result.CreatePaymentResult;

public interface CreatePaymentInputPort {
    CreatePaymentResult createPayment(CreatePaymentCommand command);
}
