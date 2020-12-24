package com.pebblepost.business.usecase.etl.ports.output;

import com.pebblepost.business.entity.CostPerPiece;
import com.pebblepost.business.entity.CostPerPiece.ProductType;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.javamoney.moneta.FastMoney;

import java.time.LocalDate;

// TODO: Add Google formatting and adjust it to your linking (again)
// TODO: Consider adding validation to in the DTO; investigate approaches
@ToString
@RequiredArgsConstructor
public class CostPerPieceDto implements SourceDto<CostPerPiece> {

  @NonNull private final String metadata;

  @NonNull private final Long brandId;

  @NonNull private final String price;

  @NonNull private final String effectiveDate;

  @NonNull private final String productType;

  @Override
  public CostPerPiece toEntity() {
    return CostPerPiece.builder()
        .brandId(brandId)
        .effectiveDate(LocalDate.parse(effectiveDate))
        .price(FastMoney.parse(price))
        .productType(ProductType.valueOf(productType))
        .build();
  }
}
