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
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

@Configuration
public class PaymentConfig {

        @Bean
        public CreatePaymentInputPort createPaymentInputPort(
                        SubscriptionPlanRepositoryPort planRepositoryPort,
                        PaymentOrderRepositoryPort paymentOrderRepositoryPort,
                        UserSubscriptionRepositoryPort userSubscriptionRepositoryPort,
                        PaymentGatewayResolver gatewayResolver,
                        PaymentOrderCodeGenerator orderCodeGenerator,
                        PlatformTransactionManager transactionManager) {
                CreatePaymentUseCase target = new CreatePaymentUseCase(
                                planRepositoryPort,
                                paymentOrderRepositoryPort,
                                userSubscriptionRepositoryPort,
                                gatewayResolver,
                                orderCodeGenerator);
                TransactionTemplate template = new TransactionTemplate(transactionManager);
                return command -> template.execute(status -> target.createPayment(command));
        }

        @Bean
        public ConfirmPaymentInputPort confirmPaymentInputPort(
                        PaymentOrderRepositoryPort orderRepositoryPort,
                        PaymentTransactionRepositoryPort transactionRepositoryPort,
                        SubscriptionPlanRepositoryPort planRepositoryPort,
                        UserSubscriptionRepositoryPort subscriptionRepositoryPort,
                        PlatformTransactionManager transactionManager) {
                ConfirmPaymentUseCase target = new ConfirmPaymentUseCase(
                                orderRepositoryPort,
                                transactionRepositoryPort,
                                planRepositoryPort,
                                subscriptionRepositoryPort);
                TransactionTemplate template = new TransactionTemplate(transactionManager);
                return command -> template.execute(status -> target.confirmPayment(command));
        }

        @Bean
        public GetPaymentInputPort getPaymentInputPort(
                        PaymentOrderRepositoryPort orderRepositoryPort,
                        PlatformTransactionManager transactionManager) {
                GetPaymentUseCase target = new GetPaymentUseCase(orderRepositoryPort);
                TransactionTemplate template = new TransactionTemplate(transactionManager);
                return orderCode -> template.execute(status -> target.getPaymentByOrderCode(orderCode));
        }

        @Bean
        public org.naho.payment.port.in.CancelPaymentInputPort cancelPaymentInputPort(
                        PaymentOrderRepositoryPort orderRepositoryPort,
                        PlatformTransactionManager transactionManager) {
                org.naho.payment.usecase.CancelPaymentUseCase target = new org.naho.payment.usecase.CancelPaymentUseCase(
                                orderRepositoryPort);
                TransactionTemplate template = new TransactionTemplate(transactionManager);
                return command -> template.execute(status -> target.cancelPayment(command));
        }

        @Bean
        public ListActivePlansInputPort listActivePlansInputPort(
                        SubscriptionPlanRepositoryPort planRepositoryPort) {
                return new ListActivePlansUseCase(planRepositoryPort);
        }

        @Bean
        public GetActiveSubscriptionInputPort getActiveSubscriptionInputPort(
                        UserSubscriptionRepositoryPort subscriptionRepositoryPort,
                        PlatformTransactionManager transactionManager) {
                GetActiveSubscriptionUseCase target = new GetActiveSubscriptionUseCase(subscriptionRepositoryPort);
                TransactionTemplate template = new TransactionTemplate(transactionManager);
                return userId -> template.execute(status -> target.getActiveSubscription(userId));
        }
}
