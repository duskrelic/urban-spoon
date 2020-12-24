package com.pebblepost.business.usecase.etl.ports.output;

import com.pebblepost.business.usecase.etl.ports.output.exceptions.DestinationWriteException;

public interface DestinationWriteService<T> {
  void createOrUpdate(T t) throws DestinationWriteException;
}
