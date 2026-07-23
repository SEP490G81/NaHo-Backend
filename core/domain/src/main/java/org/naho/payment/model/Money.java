package org.naho.payment.model;

import org.naho.i18n.message.payment.PaymentDetailMessageKey;
import org.naho.payment.exception.PaymentDomainErrorCode;
import org.naho.payment.type.CurrencyCode;
import org.naho.shared.exception.DomainException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Currency;
import java.util.Objects;

//Khóa an toàn dữ liệu tài chính (Money): Sử dụng kiểu dữ liệu BigDecimal
// có cấu hình làm tròn HALF_UP cho số tiền để loại bỏ hoàn toàn các lỗi
// sai lệch số thập phân trong các phép so sánh số dư.


public record Money(
        BigDecimal amount,
        Currency currency) {
    public Money {
        Objects.requireNonNull(amount);
        Objects.requireNonNull(currency);

        if (amount.signum() < 0) {
            throw new DomainException(
                    PaymentDomainErrorCode.PAYMENT_AMOUNT_EMPTY,
                    PaymentDetailMessageKey.PAYMENT_AMOUNT_EMPTY);
        }

        amount = amount.setScale(
                currency.getDefaultFractionDigits(),
                RoundingMode.HALF_UP);
    }

    public static Money vnd(long amount) {
        return new Money(
                BigDecimal.valueOf(amount),
                Currency.getInstance(String.valueOf(CurrencyCode.VND)));
    }

    public boolean isZero() {
        return amount.signum() == 0;
    }

    public boolean hasSameValue(Money other) {
        return currency.equals(other.currency)
                && amount.compareTo(other.amount) == 0;
    }
}
