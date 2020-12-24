package com.pebblepost.business.usecase.etl;

import com.pebblepost.business.entity.RunMetadata;
import com.pebblepost.business.entity.SelfValidating;
import com.pebblepost.business.usecase.etl.ports.output.*;
import com.pebblepost.business.usecase.etl.ports.output.exceptions.MetadataReadException;
import com.pebblepost.business.usecase.etl.ports.output.exceptions.MetadataWriteException;
import com.pebblepost.business.usecase.etl.ports.output.exceptions.RunBroadcastException;
import com.pebblepost.business.usecase.etl.ports.output.exceptions.SourceReadException;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.time.Instant;
import java.util.Optional;

@RequiredArgsConstructor
public class BatchEtlService<RunKey, Entity extends SelfValidating<Entity>> {

  @NonNull private final SourceReadService<Entity> srcService;
  @NonNull private final DestinationWriteService<Entity> destService;

  @NonNull private final RunBroadcaster<RunKey, Entity> runBroadcaster;
  @NonNull private final RunMetadata<RunKey, Entity> currentRunMetadata;
  @NonNull private final RunMetadataService<RunKey, Entity> metadataService;

  public void run()
      throws MetadataWriteException, MetadataReadException, RunBroadcastException,
          SourceReadException {

    try {

      currentRunMetadata.markRunStarted();
      runBroadcaster.broadcastRunStart();

      runBroadcaster.broadcastMetadataQuery();
      final RunMetadata<RunKey, Entity> lastRun = metadataService.findLatest();

      final Instant fromDate =
          Optional.ofNullable(lastRun).orElse(currentRunMetadata).getStartTime();
      runBroadcaster.broadcastSyncInProgress(fromDate);

      for (SourceDto<Entity> dto : srcService.getIterable(fromDate)) {
        sync(dto);
      }

      currentRunMetadata.markRunCompleted();
      runBroadcaster.broadcastMetadataSave(currentRunMetadata);
      metadataService.save(currentRunMetadata);
      runBroadcaster.broadcastRunCompleted(currentRunMetadata);

    } catch (Exception e) {
      runBroadcaster.broadcastFatalException(e);
      throw e;
    }
  }

  private void sync(SourceDto<Entity> dto) throws RunBroadcastException {
    try {
      Entity entity = dto.toEntity();
      entity.validate();
      destService.createOrUpdate(entity);
      runBroadcaster.broadcastDestWriteSuccess(dto, entity);
    } catch (Exception recoverableException) {
      currentRunMetadata.incrementExceptionCount();
      runBroadcaster.broadcastSyncException(dto, recoverableException);
    } finally {
      currentRunMetadata.incrementTotal();
    }
  }
}
