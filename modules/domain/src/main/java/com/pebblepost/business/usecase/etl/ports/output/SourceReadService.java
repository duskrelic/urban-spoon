package com.pebblepost.business.usecase.etl.ports.output;

import com.pebblepost.business.usecase.etl.ports.output.exceptions.SourceReadException;

import java.time.Instant;

public interface SourceReadService<T> {
  Iterable<SourceDto<T>> getIterable(Instant fromDate) throws SourceReadException;
}
