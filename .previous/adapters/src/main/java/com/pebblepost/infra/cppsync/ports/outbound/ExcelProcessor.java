package com.pebblepost.infra.cppsync.ports.outbound;

import com.pebblepost.infra.cppsync.domain.CostPerPiece;

import java.io.File;
import java.util.Iterator;

public class ExcelProcessor implements FileProcessor {

  @Override
  public Iterator<CostPerPiece> getIterator(File cppFile) {
    return null;
  }
}
