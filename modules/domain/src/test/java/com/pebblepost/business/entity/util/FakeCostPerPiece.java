package com.pebblepost.business.entity.util;

import com.pebblepost.business.entity.CostPerPiece;
import com.pebblepost.business.entity.CostPerPiece.ProductType;
import org.javamoney.moneta.FastMoney;

import javax.money.Monetary;
import javax.money.MonetaryAmount;
import java.time.LocalDate;

public class FakeCostPerPiece {

  private FakeCostPerPiece() {}

  public static CostPerPiece valid() {
    return new Builder().build();
  }

  public static CostPerPiece invalid() {
    return new Builder()
        .brandIdNegative()
        .effectiveDateInFuture()
        .priceHasMoreThanTwoFactionDigits()
        .productTypeNull()
        .build();
  }

  public static Builder with() {
    return new Builder();
  }

  public static class Builder {

    private Long brandId = 1L;
    private LocalDate effectiveDate = LocalDate.now();
    private MonetaryAmount price = FastMoney.ofMinor(Monetary.getCurrency("USD"), 123, 2);
    private ProductType productType = ProductType.POSTCARD;

    public Builder brandIdZero() {
      this.brandId = 0L;
      return this;
    }

    public Builder brandIdNegative() {
      this.brandId = -1L;
      return this;
    }

    public Builder brandIdNull() {
      this.brandId = null;
      return this;
    }

    public Builder effectiveDateNull() {
      this.effectiveDate = null;
      return this;
    }

    public Builder effectiveDateInFuture() {
      this.effectiveDate = LocalDate.now().plusDays(1);
      return this;
    }

    public Builder priceZero() {
      this.price = FastMoney.ofMinor(Monetary.getCurrency("USD"), 0, 2);
      return this;
    }

    public Builder priceNegative() {
      this.price = FastMoney.ofMinor(Monetary.getCurrency("USD"), -123, 2);
      return this;
    }

    public Builder priceHasMoreThanTwoFactionDigits() {
      this.price = FastMoney.ofMinor(Monetary.getCurrency("USD"), 123, 3);
      return this;
    }

    public Builder priceNull() {
      this.price = null;
      return this;
    }

    public Builder productTypeNull() {
      this.productType = null;
      return this;
    }

    public CostPerPiece build() {
      return CostPerPiece.builder()
          .brandId(brandId)
          .effectiveDate(effectiveDate)
          .price(price)
          .productType(productType)
          .build();
    }
  }
}
