package com.pebblepost.business.entity;

import lombok.*;

import javax.money.MonetaryAmount;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import javax.validation.constraints.Positive;
import java.time.LocalDate;

@Getter
@Builder
@RequiredArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ToString
public class CostPerPiece extends SelfValidating<CostPerPiece> {

  @NotNull private final ProductType productType;

  @NotNull @Positive private final Long brandId;

  @NotNull @PastOrPresent private final LocalDate effectiveDate;

  @NotNull
  @Positive
  @Digits(integer = 2, fraction = 2)
  private final MonetaryAmount price;

  public enum ProductType {
    POSTCARD,
    CATALOG
  }
}
