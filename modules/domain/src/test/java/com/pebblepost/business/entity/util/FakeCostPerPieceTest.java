package com.pebblepost.business.entity.util;

import com.pebblepost.business.entity.CostPerPiece;
import org.javamoney.moneta.FastMoney;
import org.junit.jupiter.api.Test;

import javax.money.Monetary;
import javax.money.MonetaryAmount;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.time.LocalDate;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;

class FakeCostPerPieceTest {

  private static Set<ConstraintViolation<?>> validate(CostPerPiece invalid) {
    return catchThrowableOfType(invalid::validate, ConstraintViolationException.class)
        .getConstraintViolations();
  }

  @Test
  void validFakeShouldNotThrow() {
    assertThatNoException().isThrownBy(() -> FakeCostPerPiece.valid().validate());
  }

  @Test
  void invalidFakeShouldViolateFields() {
    final CostPerPiece invalidFake = FakeCostPerPiece.invalid();
    assertThat(validate(invalidFake).size()).isEqualTo(4);
  }

  @Test
  void brandIdIsZero() {
    final CostPerPiece invalidFake = FakeCostPerPiece.with().brandIdZero().build();
    final Set<ConstraintViolation<?>> violations = validate(invalidFake);
    assertThat(violations.size()).isEqualTo(1);
    assertThat((long) violations.stream().findFirst().orElseThrow().getInvalidValue()).isZero();
  }

  @Test
  void brandIdIsNegative() {
    final CostPerPiece invalidFake = FakeCostPerPiece.with().brandIdNegative().build();
    final Set<ConstraintViolation<?>> violations = validate(invalidFake);
    assertThat(violations.size()).isEqualTo(1);
    assertThat((long) violations.stream().findFirst().orElseThrow().getInvalidValue())
        .isLessThan(0);
  }

  @Test
  void brandIdIsNull() {
    final CostPerPiece invalidFake = FakeCostPerPiece.with().brandIdNull().build();
    final Set<ConstraintViolation<?>> violations = validate(invalidFake);
    assertThat(violations.size()).isEqualTo(1);
    assertThat(violations.stream().findFirst().orElseThrow().getInvalidValue()).isNull();
  }

  @Test
  void effectiveDateInFuture() {
    final CostPerPiece invalidFake = FakeCostPerPiece.with().effectiveDateInFuture().build();
    final Set<ConstraintViolation<?>> violations = validate(invalidFake);
    assertThat(violations.size()).isEqualTo(1);
    assertThat((LocalDate) violations.stream().findFirst().orElseThrow().getInvalidValue())
        .isAfter(LocalDate.now());
  }

  @Test
  void effectiveDateIsNull() {
    final CostPerPiece invalidFake = FakeCostPerPiece.with().effectiveDateNull().build();
    final Set<ConstraintViolation<?>> violations = validate(invalidFake);
    assertThat(violations.size()).isEqualTo(1);
    assertThat(violations.stream().findFirst().orElseThrow().getInvalidValue()).isNull();
  }

  @Test
  void priceIsZero() {
    final CostPerPiece invalidFake = FakeCostPerPiece.with().priceZero().build();
    final Set<ConstraintViolation<?>> violations = validate(invalidFake);
    assertThat(violations.size()).isEqualTo(1);
    assertThat((MonetaryAmount) violations.stream().findFirst().orElseThrow().getInvalidValue())
        .isEqualTo(FastMoney.zero(Monetary.getCurrency("USD")));
  }

  @Test
  void priceIsNegative() {
    final CostPerPiece invalidFake = FakeCostPerPiece.with().priceNegative().build();
    final Set<ConstraintViolation<?>> violations = validate(invalidFake);
    assertThat(violations.size()).isEqualTo(1);
    assertThat((MonetaryAmount) violations.stream().findFirst().orElseThrow().getInvalidValue())
        .isLessThan(FastMoney.zero(Monetary.getCurrency("USD")));
  }

  @Test
  void priceIsNull() {
    final CostPerPiece invalidFake = FakeCostPerPiece.with().priceNull().build();
    final Set<ConstraintViolation<?>> violations = validate(invalidFake);
    assertThat(violations.size()).isEqualTo(1);
    assertThat(violations.stream().findFirst().orElseThrow().getInvalidValue()).isNull();
  }

  @Test
  void productTypeIsNull() {
    final CostPerPiece invalidFake = FakeCostPerPiece.with().productTypeNull().build();
    final Set<ConstraintViolation<?>> violations = validate(invalidFake);
    assertThat(violations.size()).isEqualTo(1);
    assertThat(violations.stream().findFirst().orElseThrow().getInvalidValue()).isNull();
  }
}
