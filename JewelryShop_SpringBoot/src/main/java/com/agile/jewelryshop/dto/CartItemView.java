package com.agile.jewelryshop.dto;

import com.agile.jewelryshop.model.Product;
import java.math.BigDecimal;

public record CartItemView(Product product, int quantity, BigDecimal subtotal) {}
