package org.naho.payment.usecase;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.naho.i18n.message.payment.PaymentDetailMessageKey;
import org.naho.i18n.message.subscription.SubscriptionDetailMessageKey;
import org.naho.i18n.message.user.UserDetailMessageKey;
import org.naho.payment.command.AdminUpgradeSubscriptionCommand;
import org.naho.payment.exception.PaymentErrorCode;
import org.naho.payment.model.Money;
import org.naho.shared.exception.ApplicationException;
import org.naho.shared.port.out.EventPublisherPort;
import org.naho.shared.port.out.TransactionPort;
import org.naho.subscription.mapper.SubscriptionPlanResultMapper;
import org.naho.subscription.mapper.UserSubscriptionResultMapper;
import org.naho.subscription.model.SubscriptionPlan;
import org.naho.subscription.model.UserSubscription;
import org.naho.subscription.port.out.SubscriptionPlanRepositoryPort;
import org.naho.subscription.port.out.UserSubscriptionRepositoryPort;
import org.naho.subscription.result.SubscriptionPlanResult;
import org.naho.subscription.result.UserSubscriptionResult;
import org.naho.subscription.type.PlanCode;
import org.naho.subscription.type.PlanStatus;
import org.naho.subscription.type.PlanTier;
import org.naho.user.event.UserPlanUpgradedEvent;
import org.naho.user.exception.RoleErrorCode;
import org.naho.user.exception.UserErrorCode;
import org.naho.user.model.Role;
import org.naho.user.model.User;
import org.naho.user.port.out.RoleRepositoryPort;
import org.naho.user.port.out.UserRepositoryPort;
import org.naho.user.type.RoleName;

import java.time.Instant;
import java.util.Optional;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminUpgradeSubscriptionTest {

    @Mock
    private RoleRepositoryPort roleRepositoryPort;

    @Mock
    private UserRepositoryPort userRepositoryPort;

    @Mock
    private SubscriptionPlanRepositoryPort planRepositoryPort;

    @Mock
    private UserSubscriptionRepositoryPort subscriptionRepositoryPort;

    @Mock
    private SubscriptionPlanResultMapper planResultMapper;

    @Mock
    private UserSubscriptionResultMapper userSubscriptionResultMapper;

    @Mock
    private EventPublisherPort eventPublisherPort;

    @Mock
    private TransactionPort transactionPort;

    @InjectMocks
    private AdminUpgradeSubscriptionUseCase adminUpgradeSubscriptionUseCase;

    @BeforeEach
    void setUp() {
        lenient().when(transactionPort.execute(any(Supplier.class))).thenAnswer(invocation -> {
            Supplier<?> supplier = invocation.getArgument(0);
            return supplier.get();
        });
    }

    @Test
    @DisplayName("UTCID01 - Admin nâng cấp gói cho user thành công")
    void UTCID01_UpgradeSubscription_Success() {
        // Arrange
        Long adminId = 1L;
        Long targetUserId = 100L;
        AdminUpgradeSubscriptionCommand command = new AdminUpgradeSubscriptionCommand(
                adminId, targetUserId, PlanCode.BASIC, null);

        Role adminRole = Role.builder().id(1L).roleName(RoleName.ADMIN).build();
        User targetUser = mock(User.class);

        SubscriptionPlan targetPlan = SubscriptionPlan.builder()
                .id(1L)
                .code(PlanCode.BASIC)
                .description("Gói cơ bản")
                .tier(PlanTier.BASIC)
                .price(Money.vnd(99000L))
                .durationDays(30)
                .status(PlanStatus.ACTIVE)
                .build();

        SubscriptionPlan freePlan = SubscriptionPlan.builder()
                .id(0L)
                .code(PlanCode.FREE)
                .description("Gói miễn phí")
                .tier(PlanTier.FREE)
                .price(Money.vnd(0L))
                .status(PlanStatus.ACTIVE)
                .build();

        UserSubscription savedSub = mock(UserSubscription.class);
        SubscriptionPlanResult planResult = mock(SubscriptionPlanResult.class);
        UserSubscriptionResult expectedResult = mock(UserSubscriptionResult.class);

        when(roleRepositoryPort.findByUserId(adminId)).thenReturn(Optional.of(adminRole));
        when(userRepositoryPort.findById(targetUserId)).thenReturn(Optional.of(targetUser));
        when(planRepositoryPort.findActiveByCode(PlanCode.BASIC)).thenReturn(Optional.of(targetPlan));
        when(subscriptionRepositoryPort.findActiveByUserId(eq(targetUserId), any(Instant.class))).thenReturn(Optional.empty());
        when(planRepositoryPort.findActiveByCode(PlanCode.FREE)).thenReturn(Optional.of(freePlan));
        when(subscriptionRepositoryPort.save(any(UserSubscription.class))).thenReturn(savedSub);
        when(planResultMapper.mapToPlanResult(targetPlan)).thenReturn(planResult);
        when(userSubscriptionResultMapper.mapToUserSubscriptionResult(savedSub, planResult)).thenReturn(expectedResult);

        // Act
        UserSubscriptionResult result = adminUpgradeSubscriptionUseCase.upgradeSubscription(command);

        // Assert
        assertNotNull(result);
        assertEquals(expectedResult, result);
        verify(eventPublisherPort, times(1)).publish(any(UserPlanUpgradedEvent.class));
        verify(subscriptionRepositoryPort, times(1)).save(any(UserSubscription.class));
    }

    @Test
    @DisplayName("UTCID02 - Ném ngoại lệ khi không tìm thấy quyền của Admin")
    void UTCID02_UpgradeSubscription_AdminRoleNotFound() {
        // Arrange
        AdminUpgradeSubscriptionCommand command = new AdminUpgradeSubscriptionCommand(
                1L, 100L, PlanCode.BASIC, null);
        when(roleRepositoryPort.findByUserId(1L)).thenReturn(Optional.empty());

        // Act & Assert
        ApplicationException exception = assertThrows(ApplicationException.class,
                () -> adminUpgradeSubscriptionUseCase.upgradeSubscription(command));

        assertEquals(RoleErrorCode.ROLE_NOT_FOUND, exception.getErrorCode());
        assertEquals(UserDetailMessageKey.USER_ROLE_NOT_FOUND, exception.getMessage());
    }

    @Test
    @DisplayName("UTCID03 - Ném ngoại lệ khi người thực hiện không có quyền Admin")
    void UTCID03_UpgradeSubscription_AccessDenied() {
        // Arrange
        AdminUpgradeSubscriptionCommand command = new AdminUpgradeSubscriptionCommand(
                1L, 100L, PlanCode.BASIC, null);
        Role userRole = Role.builder().id(1L).roleName(RoleName.LEARNER).build();
        when(roleRepositoryPort.findByUserId(1L)).thenReturn(Optional.of(userRole));

        // Act & Assert
        ApplicationException exception = assertThrows(ApplicationException.class,
                () -> adminUpgradeSubscriptionUseCase.upgradeSubscription(command));

        assertEquals(UserErrorCode.USER_ACCESS_DENIED, exception.getErrorCode());
        assertEquals(UserDetailMessageKey.USER_ACCESS_DENIED, exception.getMessage());
    }

    @Test
    @DisplayName("UTCID04 - Ném ngoại lệ khi không tìm thấy người dùng mục tiêu")
    void UTCID04_UpgradeSubscription_TargetUserNotFound() {
        // Arrange
        AdminUpgradeSubscriptionCommand command = new AdminUpgradeSubscriptionCommand(
                1L, 100L, PlanCode.BASIC, null);
        Role adminRole = Role.builder().id(1L).roleName(RoleName.ADMIN).build();
        when(roleRepositoryPort.findByUserId(1L)).thenReturn(Optional.of(adminRole));
        when(userRepositoryPort.findById(100L)).thenReturn(Optional.empty());

        // Act & Assert
        ApplicationException exception = assertThrows(ApplicationException.class,
                () -> adminUpgradeSubscriptionUseCase.upgradeSubscription(command));

        assertEquals(UserErrorCode.USER_NOT_FOUND, exception.getErrorCode());
        assertEquals(UserDetailMessageKey.USER_NOT_FOUND, exception.getMessage());
    }

    @Test
    @DisplayName("UTCID05 - Ném ngoại lệ khi không tìm thấy gói cước mục tiêu")
    void UTCID05_UpgradeSubscription_TargetPlanNotFound() {
        // Arrange
        AdminUpgradeSubscriptionCommand command = new AdminUpgradeSubscriptionCommand(
                1L, 100L, PlanCode.BASIC, null);
        Role adminRole = Role.builder().id(1L).roleName(RoleName.ADMIN).build();
        User targetUser = mock(User.class);

        when(roleRepositoryPort.findByUserId(1L)).thenReturn(Optional.of(adminRole));
        when(userRepositoryPort.findById(100L)).thenReturn(Optional.of(targetUser));
        when(planRepositoryPort.findActiveByCode(PlanCode.BASIC)).thenReturn(Optional.empty());

        // Act & Assert
        ApplicationException exception = assertThrows(ApplicationException.class,
                () -> adminUpgradeSubscriptionUseCase.upgradeSubscription(command));

        assertEquals(PaymentErrorCode.PLAN_NOT_FOUND, exception.getErrorCode());
        assertEquals(SubscriptionDetailMessageKey.PLAN_NOT_FOUND, exception.getMessage());
    }

    @Test
    @DisplayName("UTCID06 - Ném ngoại lệ khi gói cước hiện tại của user đã là gói tối đa (PREMIUM)")
    void UTCID06_UpgradeSubscription_CurrentTierMax() {
        // Arrange
        AdminUpgradeSubscriptionCommand command = new AdminUpgradeSubscriptionCommand(
                1L, 100L, PlanCode.BASIC, null);
        Role adminRole = Role.builder().id(1L).roleName(RoleName.ADMIN).build();
        User targetUser = mock(User.class);

        SubscriptionPlan targetPlan = SubscriptionPlan.builder()
                .id(1L)
                .code(PlanCode.BASIC)
                .description("Gói tháng")
                .tier(PlanTier.BASIC)
                .price(Money.vnd(99000L))
                .durationDays(30)
                .status(PlanStatus.ACTIVE)
                .build();

        SubscriptionPlan premiumPlan = SubscriptionPlan.builder()
                .id(10L)
                .code(PlanCode.PREMIUM)
                .description("Gói năm")
                .tier(PlanTier.PREMIUM)
                .price(Money.vnd(999000L))
                .durationDays(365)
                .status(PlanStatus.ACTIVE)
                .build();

        UserSubscription currentActiveSub = mock(UserSubscription.class);
        when(currentActiveSub.getSubscriptionPlanId()).thenReturn(10L);

        when(roleRepositoryPort.findByUserId(1L)).thenReturn(Optional.of(adminRole));
        when(userRepositoryPort.findById(100L)).thenReturn(Optional.of(targetUser));
        when(planRepositoryPort.findActiveByCode(PlanCode.BASIC)).thenReturn(Optional.of(targetPlan));
        when(subscriptionRepositoryPort.findActiveByUserId(eq(100L), any(Instant.class))).thenReturn(Optional.of(currentActiveSub));
        when(planRepositoryPort.findById(10L)).thenReturn(Optional.of(premiumPlan));

        // Act & Assert
        ApplicationException exception = assertThrows(ApplicationException.class,
                () -> adminUpgradeSubscriptionUseCase.upgradeSubscription(command));

        assertEquals(PaymentErrorCode.MAXIMUM_SUBSCRIPTION_TIER_REACHED, exception.getErrorCode());
        assertEquals(PaymentDetailMessageKey.PAYMENT_SUBSCRIPTION_MAX_TIER_REACHED, exception.getMessage());
    }

    @Test
    @DisplayName("UTCID07 - Ném ngoại lệ khi nâng cấp lên gói có cùng hoặc thấp hơn bậc gói hiện tại")
    void UTCID07_UpgradeSubscription_TargetTierSameOrLower() {
        // Arrange
        AdminUpgradeSubscriptionCommand command = new AdminUpgradeSubscriptionCommand(
                1L, 100L, PlanCode.BASIC, null);
        Role adminRole = Role.builder().id(1L).roleName(RoleName.ADMIN).build();
        User targetUser = mock(User.class);

        SubscriptionPlan currentPlan = SubscriptionPlan.builder()
                .id(5L)
                .code(PlanCode.BASIC)
                .description("Gói tháng BASIC")
                .tier(PlanTier.BASIC)
                .price(Money.vnd(99000L))
                .durationDays(30)
                .status(PlanStatus.ACTIVE)
                .build();

        SubscriptionPlan targetPlan = SubscriptionPlan.builder()
                .id(1L)
                .code(PlanCode.BASIC)
                .description("Gói tháng BASIC 2")
                .tier(PlanTier.BASIC)
                .price(Money.vnd(99000L))
                .durationDays(30)
                .status(PlanStatus.ACTIVE)
                .build();

        UserSubscription currentActiveSub = mock(UserSubscription.class);
        when(currentActiveSub.getSubscriptionPlanId()).thenReturn(5L);

        when(roleRepositoryPort.findByUserId(1L)).thenReturn(Optional.of(adminRole));
        when(userRepositoryPort.findById(100L)).thenReturn(Optional.of(targetUser));
        when(planRepositoryPort.findActiveByCode(PlanCode.BASIC)).thenReturn(Optional.of(targetPlan));
        when(subscriptionRepositoryPort.findActiveByUserId(eq(100L), any(Instant.class))).thenReturn(Optional.of(currentActiveSub));
        when(planRepositoryPort.findById(5L)).thenReturn(Optional.of(currentPlan));

        // Act & Assert
        ApplicationException exception = assertThrows(ApplicationException.class,
                () -> adminUpgradeSubscriptionUseCase.upgradeSubscription(command));

        assertEquals(PaymentErrorCode.CANNOT_UPGRADE_SAME_OR_LOWER_TIER, exception.getErrorCode());
        assertEquals(PaymentDetailMessageKey.PAYMENT_SUBSCRIPTION_SAME_OR_LOWER_TIER, exception.getMessage());
    }

    @Test
    @DisplayName("UTCID08 - Nâng cấp gói thành công với số ngày tuỳ chỉnh (customDurationDays)")
    void UTCID08_UpgradeSubscription_WithCustomDurationDays() {
        // Arrange
        Long adminId = 1L;
        Long targetUserId = 100L;
        AdminUpgradeSubscriptionCommand command = new AdminUpgradeSubscriptionCommand(
                adminId, targetUserId, PlanCode.BASIC, 60);

        Role adminRole = Role.builder().id(1L).roleName(RoleName.ADMIN).build();
        User targetUser = mock(User.class);

        SubscriptionPlan targetPlan = SubscriptionPlan.builder()
                .id(1L)
                .code(PlanCode.BASIC)
                .description("Gói cơ bản")
                .tier(PlanTier.BASIC)
                .price(Money.vnd(99000L))
                .durationDays(30)
                .status(PlanStatus.ACTIVE)
                .build();

        UserSubscription previousSub = mock(UserSubscription.class);
        when(previousSub.getSubscriptionPlanId()).thenReturn(1L);

        SubscriptionPlan freePlan = SubscriptionPlan.builder()
                .id(0L)
                .code(PlanCode.FREE)
                .description("Gói miễn phí")
                .tier(PlanTier.FREE)
                .price(Money.vnd(0L))
                .status(PlanStatus.ACTIVE)
                .build();

        UserSubscription savedSub = mock(UserSubscription.class);
        SubscriptionPlanResult planResult = mock(SubscriptionPlanResult.class);
        UserSubscriptionResult expectedResult = mock(UserSubscriptionResult.class);

        when(roleRepositoryPort.findByUserId(adminId)).thenReturn(Optional.of(adminRole));
        when(userRepositoryPort.findById(targetUserId)).thenReturn(Optional.of(targetUser));
        when(planRepositoryPort.findActiveByCode(PlanCode.BASIC)).thenReturn(Optional.of(targetPlan));
        when(subscriptionRepositoryPort.findActiveByUserId(eq(targetUserId), any(Instant.class))).thenReturn(Optional.of(previousSub));
        when(planRepositoryPort.findById(1L)).thenReturn(Optional.of(freePlan));
        when(subscriptionRepositoryPort.save(any(UserSubscription.class))).thenReturn(savedSub);
        when(planResultMapper.mapToPlanResult(targetPlan)).thenReturn(planResult);
        when(userSubscriptionResultMapper.mapToUserSubscriptionResult(savedSub, planResult)).thenReturn(expectedResult);

        // Act
        UserSubscriptionResult result = adminUpgradeSubscriptionUseCase.upgradeSubscription(command);

        // Assert
        assertNotNull(result);
        assertEquals(expectedResult, result);
        verify(previousSub, times(1)).cancel(any(Instant.class));
        verify(subscriptionRepositoryPort, times(2)).save(any(UserSubscription.class));
    }
}
