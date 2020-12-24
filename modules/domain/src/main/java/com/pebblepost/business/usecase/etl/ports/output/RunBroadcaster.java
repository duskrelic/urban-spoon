package com.pebblepost.business.usecase.etl.ports.output;

import com.pebblepost.business.entity.RunMetadata;
import com.pebblepost.business.usecase.etl.ports.output.exceptions.RunBroadcastException;

import java.time.Instant;

public interface RunBroadcaster<RunKey, Entity> {

  void broadcastRunStart() throws RunBroadcastException;

  void broadcastSyncInProgress(Instant fromDate) throws RunBroadcastException;

  void broadcastDestWriteSuccess(SourceDto<Entity> dto, Entity entity) throws RunBroadcastException;

  void broadcastSyncException(SourceDto<Entity> dto, Exception e) throws RunBroadcastException;

  void broadcastMetadataQuery() throws RunBroadcastException;

  void broadcastMetadataSave(RunMetadata<RunKey, Entity> metadata) throws RunBroadcastException;

  void broadcastRunCompleted(RunMetadata<RunKey, Entity> metadata) throws RunBroadcastException;

  void broadcastFatalException(Exception e) throws RunBroadcastException;
}
