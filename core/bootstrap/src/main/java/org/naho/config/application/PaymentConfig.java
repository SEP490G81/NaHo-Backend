package org.naho.config.application;

import org.naho.payment.port.in.ConfirmPaymentInputPort;
import org.naho.payment.port.in.CreatePaymentInputPort;
import org.naho.payment.port.in.GetPaymentInputPort;
import org.naho.payment.port.out.PaymentGatewayResolver;
import org.naho.payment.port.out.PaymentOrderCodeGenerator;
import org.naho.payment.port.out.PaymentOrderRepositoryPort;
import org.naho.payment.port.out.PaymentTransactionRepositoryPort;
import org.naho.payment.usecase.ConfirmPaymentUseCase;
import org.naho.payment.usecase.CreatePaymentUseCase;
import org.naho.payment.usecase.GetPaymentUseCase;
import org.naho.subscription.port.in.GetActiveSubscriptionInputPort;
import org.naho.subscription.port.in.ListActivePlansInputPort;
import org.naho.subscription.port.out.SubscriptionPlanRepositoryPort;
import org.naho.subscription.port.out.UserSubscriptionRepositoryPort;
import org.naho.subscription.usecase.GetActiveSubscriptionUseCase;
import org.naho.subscription.usecase.ListActivePlansUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PaymentConfig {

        @Bean
        public CreatePaymentInputPort createPaymentInputPort(
                        SubscriptionPlanRepositoryPort planRepositoryPort,
                        PaymentOrderRepositoryPort paymentOrderRepositoryPort,
                        PaymentGatewayResolver gatewayResolver,
                        PaymentOrderCodeGenerator orderCodeGenerator) {
                return new CreatePaymentUseCase(
                                planRepositoryPort,
                                paymentOrderRepositoryPort,
                                gatewayResolver,
                                orderCodeGenerator);
        }

        @Bean
        public ConfirmPaymentInputPort confirmPaymentInputPort(
                        PaymentOrderRepositoryPort orderRepositoryPort,
                        PaymentTransactionRepositoryPort transactionRepositoryPort,
                        SubscriptionPlanRepositoryPort planRepositoryPort,
                        UserSubscriptionRepositoryPort subscriptionRepositoryPort) {
                return new ConfirmPaymentUseCase(
                                orderRepositoryPort,
                                transactionRepositoryPort,
                                planRepositoryPort,
                                subscriptionRepositoryPort);
        }

        @Bean
        public GetPaymentInputPort getPaymentInputPort(
                        PaymentOrderRepositoryPort orderRepositoryPort) {
                return new GetPaymentUseCase(orderRepositoryPort);
        }

        @Bean
        public ListActivePlansInputPort listActivePlansInputPort(
                        SubscriptionPlanRepositoryPort planRepositoryPort) {
                return new ListActivePlansUseCase(planRepositoryPort);
        }

        @Bean
        public GetActiveSubscriptionInputPort getActiveSubscriptionInputPort(
                        UserSubscriptionRepositoryPort subscriptionRepositoryPort) {
                return new GetActiveSubscriptionUseCase(subscriptionRepositoryPort);
        }
}
