package org.naho.payment.port.in;

import org.naho.payment.command.AdminUpgradeSubscriptionCommand;
import org.naho.subscription.result.UserSubscriptionResult;

public interface AdminUpgradeSubscriptionInputPort {
    UserSubscriptionResult upgradeSubscription(AdminUpgradeSubscriptionCommand command);
}
