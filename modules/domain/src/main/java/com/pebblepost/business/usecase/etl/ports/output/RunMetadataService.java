package com.pebblepost.business.usecase.etl.ports.output;

import com.pebblepost.business.entity.RunMetadata;
import com.pebblepost.business.usecase.etl.ports.output.exceptions.MetadataReadException;
import com.pebblepost.business.usecase.etl.ports.output.exceptions.MetadataWriteException;

public interface RunMetadataService<KeyType, Entity> {
  RunMetadata<KeyType, Entity> findLatest() throws MetadataReadException;

  void save(RunMetadata<KeyType, Entity> metadata) throws MetadataWriteException;
}
