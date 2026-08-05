package org.naho.config.application;

import org.naho.payment.port.in.CancelPaymentInputPort;
import org.naho.payment.port.in.ConfirmPaymentInputPort;
import org.naho.payment.port.in.CreatePaymentInputPort;
import org.naho.payment.port.in.GetPaymentInputPort;
import org.naho.payment.port.out.*;
import org.naho.payment.usecase.CancelPaymentUseCase;
import org.naho.payment.usecase.ConfirmPaymentUseCase;
import org.naho.payment.usecase.CreatePaymentUseCase;
import org.naho.payment.usecase.GetPaymentUseCase;
import org.naho.shared.port.out.TransactionPort;
import org.naho.subscription.mapper.SubscriptionPlanResultMapper;
import org.naho.subscription.mapper.UserSubscriptionResultMapper;
import org.naho.subscription.port.in.GetActiveSubscriptionInputPort;
import org.naho.subscription.port.in.ListActivePlansInputPort;
import org.naho.subscription.port.out.SubscriptionPlanRepositoryPort;
import org.naho.subscription.port.out.UserSubscriptionRepositoryPort;
import org.naho.subscription.usecase.GetActiveSubscriptionUseCase;
import org.naho.subscription.usecase.ListActivePlansUseCase;
import org.naho.user.port.out.UserRepositoryPort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PaymentConfig {

    @Bean
    public CreatePaymentInputPort createPaymentInputPort(
            SubscriptionPlanRepositoryPort planRepositoryPort,
            PaymentOrderRepositoryPort paymentOrderRepositoryPort,
            UserSubscriptionRepositoryPort userSubscriptionRepositoryPort,
            PaymentGatewayResolver gatewayResolver,
            PaymentOrderCodeGenerator orderCodeGenerator,
            UserRepositoryPort userRepositoryPort,
            PaymentIdempotencyRepositoryPort idempotencyRepositoryPort,
            @Value("${app.vnpay.timeout:5}") int timeoutMinutes,
            TransactionPort transactionPort) {

        return new CreatePaymentUseCase(
                planRepositoryPort,
                paymentOrderRepositoryPort,
                userSubscriptionRepositoryPort,
                gatewayResolver,
                orderCodeGenerator,
                userRepositoryPort,
                idempotencyRepositoryPort,
                timeoutMinutes,
                transactionPort);
    }

    @Bean
    public ConfirmPaymentInputPort confirmPaymentInputPort(
            PaymentOrderRepositoryPort orderRepositoryPort,
            PaymentTransactionRepositoryPort transactionRepositoryPort,
            SubscriptionPlanRepositoryPort planRepositoryPort,
            UserSubscriptionRepositoryPort subscriptionRepositoryPort,
            org.naho.shared.port.out.EventPublisherPort eventPublisherPort,
            TransactionPort transactionPort) {
        return new ConfirmPaymentUseCase(
                orderRepositoryPort,
                transactionRepositoryPort,
                planRepositoryPort,
                subscriptionRepositoryPort,
                eventPublisherPort,
                transactionPort);
    }

    @Bean
    public GetPaymentInputPort getPaymentInputPort(
            PaymentOrderRepositoryPort orderRepositoryPort,
            TransactionPort transactionPort) {
        return new GetPaymentUseCase(orderRepositoryPort, transactionPort);
    }

    @Bean
    public org.naho.payment.port.in.AdminUpgradeSubscriptionInputPort adminUpgradeSubscriptionInputPort(
            org.naho.user.port.out.RoleRepositoryPort roleRepositoryPort,
            UserRepositoryPort userRepositoryPort,
            SubscriptionPlanRepositoryPort planRepositoryPort,
            UserSubscriptionRepositoryPort subscriptionRepositoryPort,
            org.naho.shared.port.out.EventPublisherPort eventPublisherPort,
            TransactionPort transactionPort) {
        return new org.naho.payment.usecase.AdminUpgradeSubscriptionUseCase(
                roleRepositoryPort,
                userRepositoryPort,
                planRepositoryPort,
                subscriptionRepositoryPort,
                eventPublisherPort,
                transactionPort);
    }

    @Bean
    public CancelPaymentInputPort cancelPaymentInputPort(
            PaymentOrderRepositoryPort orderRepositoryPort,
            TransactionPort transactionPort) {
        return new CancelPaymentUseCase(
                orderRepositoryPort,
                transactionPort);
    }

    @Bean
    public ListActivePlansInputPort listActivePlansInputPort(
            SubscriptionPlanRepositoryPort planRepositoryPort) {
        return new ListActivePlansUseCase(planRepositoryPort);
    }

    @Bean
    public SubscriptionPlanResultMapper subscriptionPlanResultMapper() {
        return new SubscriptionPlanResultMapper();
    }

    @Bean
    public UserSubscriptionResultMapper userSubscriptionResultMapper() {
        return new UserSubscriptionResultMapper();
    }

    @Bean
    public GetActiveSubscriptionInputPort getActiveSubscriptionInputPort(
            UserSubscriptionRepositoryPort userSubscriptionRepositoryPort,
            SubscriptionPlanRepositoryPort subscriptionPlanRepositoryPort,
            SubscriptionPlanResultMapper subscriptionPlanResultMapper,
            UserSubscriptionResultMapper userSubscriptionResultMapper
    ) {
        return new GetActiveSubscriptionUseCase(
                userSubscriptionRepositoryPort,
                subscriptionPlanRepositoryPort,
                subscriptionPlanResultMapper,
                userSubscriptionResultMapper
        );
    }
}

