package com.pebblepost.cpp_sync.repository.database;

import com.pebblepost.cpp_sync.domain.CostPerPiece;
import com.pebblepost.cpp_sync.domain.CostPerPiece.Validator;
import com.pebblepost.cpp_sync.utils.FakeCppGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@TestMethodOrder(OrderAnnotation.class)
class CostPerPieceRepositoryIntegrationTest {

  static final int FAKE_LIST_SIZE = 50;
  static final Validator validator = new Validator();
  static FakeCppGenerator generator = new FakeCppGenerator();

  private List<CostPerPiece> fakes;

  @Autowired private CostPerPieceRepository repo;

  @BeforeEach
  void setUp() {
    repo.truncate();
    assertEquals(0, repo.getCount());

    fakes = generator.generateList(FAKE_LIST_SIZE);
    fakes.forEach(cpp -> assertTrue(validator.isValid(cpp)));
  }

  @Test
  @Transactional
  void insertBatch_emptyTable_countShouldBeBatchSize() {
    this.repo.insertBatch(fakes);
    assertEquals(fakes.size(), repo.getCount());
  }

  @Test
  @Transactional
  void insertBatch_emptyTable_fetchedEntitiesMatch() {
    repo.insertBatch(fakes);
    List<CostPerPiece> fromDb = repo.findAllByCreatedOnAsc();
    assertEquals(fakes.size(), fromDb.size());
    IntStream.range(0, fakes.size())
        .forEach(
            i -> {
              assertEquals(fakes.get(i), fromDb.get(i));
              assertTrue(validator.isValid(fromDb.get(i)));
            });
  }

  @Test
  @Transactional
  void truncate_populatedTable_countShouldBeZero() {
    repo.insertBatch(fakes);
    assertEquals(fakes.size(), repo.getCount());
    repo.truncate();
    assertEquals(0, repo.getCount());
  }
}
