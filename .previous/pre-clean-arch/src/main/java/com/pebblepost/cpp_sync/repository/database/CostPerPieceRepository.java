package com.pebblepost.cpp_sync.repository.database;

import com.pebblepost.cpp_sync.domain.CostPerPiece;
import com.pebblepost.cpp_sync.domain.enumeration.ProductType;
import com.pebblepost.cpp_sync.repository.database.CostPerPieceRepository.Constants.CostPerPiece.Select;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class CostPerPieceRepository {

  private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
  private final JdbcTemplate jdbcTemplate;

  public CostPerPieceRepository(
      NamedParameterJdbcTemplate namedParameterJdbcTemplate, JdbcTemplate jdbcTemplate) {
    this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    this.jdbcTemplate = jdbcTemplate;
  }

  public void truncate() {
    jdbcTemplate.execute(Constants.CostPerPiece.TRUNCATE);
  }

  public void insertBatch(List<CostPerPiece> cppList) {
    SqlParameterSource[] batch = SqlParameterSourceUtils.createBatch(cppList);
    namedParameterJdbcTemplate.batchUpdate(Constants.CostPerPiece.INSERT, batch);
  }

  Integer getCount() {
    return jdbcTemplate.queryForObject(Select.COUNT, Integer.class);
  }

  List<CostPerPiece> findAllByCreatedOnAsc() {
    return namedParameterJdbcTemplate.query(Select.ALL_BY_CREATED_ON_ASC, new CppJdbcRowMapper());
  }

  static class CppJdbcRowMapper implements RowMapper<CostPerPiece> {

    @Override
    public CostPerPiece mapRow(ResultSet rs, int rowNum) throws SQLException {
      return new CostPerPiece(
          rs.getLong("brand_id"),
          ProductType.valueOf(rs.getString("product_type")),
          rs.getDate("effective_date").toLocalDate(),
          rs.getBigDecimal("price"));
    }
  }

  static class Constants {

    static class CostPerPiece {

      static final String INSERT =
          "insert into reporting.cost_per_piece "
              + "(brand_id,"
              + "product_type,"
              + "effective_date,"
              + "effective_date_year,"
              + "effective_date_month,"
              + "price"
              + ")"
              + "values "
              + "("
              + ":brandId,"
              + ":productTypeStr::reporting"
              + ".product_type,"
              + ":effectiveDate,"
              + "extract(year from :effectiveDate),"
              + "extract"
              + "(month from :effectiveDate),"
              + ":price"
              + ");";

      static final String TRUNCATE = "truncate table reporting.cost_per_piece;\n";

      static class Select {

        static final String ALL =
            "select brand_id, product_type, effective_date, price from "
                + "reporting.cost_per_piece";

        static final String ALL_BY_CREATED_ON_ASC = ALL + " " + "order by created_on";

        static final String COUNT = "select count(*) from reporting.cost_per_piece";
      }
    }
  }
}
