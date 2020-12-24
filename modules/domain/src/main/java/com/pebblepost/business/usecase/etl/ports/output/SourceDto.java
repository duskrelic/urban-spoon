package com.pebblepost.business.usecase.etl.ports.output;

import com.pebblepost.business.usecase.etl.ports.output.exceptions.MappingException;

public interface SourceDto<T> {
  T toEntity() throws MappingException;
}
