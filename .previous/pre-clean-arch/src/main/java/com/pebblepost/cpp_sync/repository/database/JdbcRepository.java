package com.pebblepost.cpp_sync.repository.database;

import com.pebblepost.cpp_sync.domain.CostPerPiece;

import java.util.List;

public interface JdbcRepository {

  void truncate();

  void insertBatch(List<CostPerPiece> costPerPieceList);
}
