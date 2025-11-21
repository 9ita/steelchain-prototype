package com.example.integrationlab.domain;

import java.math.BigDecimal;

public class Order {
    private final BigDecimal amount;
    private final OrderType type;
    private final boolean vip;

    public Order(BigDecimal amount, OrderType type, boolean vip) {
        this.amount = amount;
        this.type = type;
        this.vip = vip;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public OrderType getType() {
        return type;
    }

    public boolean isVip() {
        return vip;
    }
}
