package com.pebblepost.business.usecase.etl;

import com.pebblepost.business.entity.CostPerPiece;
import com.pebblepost.business.entity.RunMetadata;
import com.pebblepost.business.usecase.etl.ports.output.*;
import com.pebblepost.business.usecase.etl.ports.output.exceptions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Iterator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BatchEtlServiceTest {

  @Mock private RunBroadcaster<String, CostPerPiece> runBroadcaster;
  @Mock private RunMetadata<String, CostPerPiece> currentRunMetadata;
  @Mock private RunMetadataService<String, CostPerPiece> metadataService;
  @Mock private SourceReadService<CostPerPiece> srcService;
  @Mock private DestinationWriteService<CostPerPiece> destService;
  @InjectMocks private BatchEtlService<String, CostPerPiece> etlService;

  @Test
  void run_expectedBehavior(
      @Mock RunMetadata<String, CostPerPiece> lastRunMetadata,
      @Mock Iterable<SourceDto<CostPerPiece>> srcIterable,
      @Mock Iterator<SourceDto<CostPerPiece>> srcIterator,
      @Mock SourceDto<CostPerPiece> srcDto,
      @Mock CostPerPiece entity)
      throws MappingException, DestinationWriteException, RunBroadcastException,
          MetadataWriteException, MetadataReadException, SourceReadException {

    final Instant YESTERDAY = Instant.now().minus(1, ChronoUnit.DAYS);

    when(metadataService.findLatest()).thenReturn(lastRunMetadata);
    when(lastRunMetadata.getStartTime()).thenReturn(YESTERDAY);
    when(srcService.getIterable(YESTERDAY)).thenReturn(srcIterable);
    when(srcIterable.iterator()).thenReturn(srcIterator);
    when(srcIterator.hasNext()).thenReturn(Boolean.TRUE, Boolean.FALSE);
    when(srcIterator.next()).thenReturn(srcDto);
    when(srcDto.toEntity()).thenReturn(entity);

    etlService.run();

    InOrder inOrder =
        inOrder(
            currentRunMetadata,
            runBroadcaster,
            metadataService,
            srcService,
            destService,
            srcIterable,
            srcIterator,
            srcDto,
            entity);

    inOrder.verify(currentRunMetadata).markRunStarted();
    inOrder.verify(runBroadcaster).broadcastRunStart();

    inOrder.verify(runBroadcaster).broadcastMetadataQuery();
    inOrder.verify(metadataService).findLatest();

    inOrder.verify(runBroadcaster).broadcastSyncInProgress(YESTERDAY);
    inOrder.verify(srcService).getIterable(YESTERDAY);
    inOrder.verify(srcIterable).iterator();

    inOrder.verify(srcIterator).hasNext();
    inOrder.verify(srcIterator).next();
    inOrder.verify(srcDto).toEntity();
    inOrder.verify(entity).validate();
    inOrder.verify(destService).createOrUpdate(entity);
    inOrder.verify(runBroadcaster).broadcastDestWriteSuccess(srcDto, entity);
    inOrder.verify(currentRunMetadata).incrementTotal();
    inOrder.verify(srcIterator).hasNext();

    inOrder.verify(currentRunMetadata).markRunCompleted();
    inOrder.verify(runBroadcaster).broadcastMetadataSave(currentRunMetadata);
    inOrder.verify(metadataService).save(currentRunMetadata);
    inOrder.verify(runBroadcaster).broadcastRunCompleted(currentRunMetadata);

    inOrder.verifyNoMoreInteractions();
  }

  @Test
  void run_whenNoMetadataExists_currentRunMetadataIsUsed(
      @Mock RunMetadata<String, CostPerPiece> lastRunMetadata,
      @Mock Iterable<SourceDto<CostPerPiece>> srcIterable,
      @Mock Iterator<SourceDto<CostPerPiece>> srcIterator,
      @Mock SourceDto<CostPerPiece> srcDto,
      @Mock CostPerPiece entity)
      throws MappingException, DestinationWriteException, RunBroadcastException,
          MetadataWriteException, MetadataReadException, SourceReadException {

    final Instant TEST_START_TIME = Instant.now();

    when(metadataService.findLatest()).thenReturn(null);
    when(currentRunMetadata.getStartTime()).thenReturn(TEST_START_TIME);
    when(srcService.getIterable(currentRunMetadata.getStartTime())).thenReturn(srcIterable);
    when(srcIterable.iterator()).thenReturn(srcIterator);
    when(srcIterator.hasNext()).thenReturn(Boolean.TRUE, Boolean.FALSE);
    when(srcIterator.next()).thenReturn(srcDto);
    when(srcDto.toEntity()).thenReturn(entity);

    etlService.run();

    InOrder inOrder =
        inOrder(
            currentRunMetadata,
            runBroadcaster,
            metadataService,
            srcService,
            destService,
            srcIterable,
            srcIterator,
            srcDto,
            entity);

    inOrder.verify(currentRunMetadata).markRunStarted();
    inOrder.verify(runBroadcaster).broadcastRunStart();

    inOrder.verify(runBroadcaster).broadcastMetadataQuery();
    inOrder.verify(metadataService).findLatest();

    inOrder.verify(runBroadcaster).broadcastSyncInProgress(TEST_START_TIME);
    inOrder.verify(srcService).getIterable(TEST_START_TIME);
    inOrder.verify(srcIterable).iterator();

    inOrder.verify(srcIterator).hasNext();
    inOrder.verify(srcIterator).next();
    inOrder.verify(srcDto).toEntity();
    inOrder.verify(entity).validate();
    inOrder.verify(destService).createOrUpdate(entity);
    inOrder.verify(runBroadcaster).broadcastDestWriteSuccess(srcDto, entity);
    inOrder.verify(currentRunMetadata).incrementTotal();
    inOrder.verify(srcIterator).hasNext();

    inOrder.verify(currentRunMetadata).markRunCompleted();
    inOrder.verify(runBroadcaster).broadcastMetadataSave(currentRunMetadata);
    inOrder.verify(metadataService).save(currentRunMetadata);
    inOrder.verify(runBroadcaster).broadcastRunCompleted(currentRunMetadata);

    inOrder.verifyNoMoreInteractions();
  }

  @Test
  void run_whenNonFatalExceptionThrown_exceptionIsNotedAndRunProceeds(
      @Mock RunMetadata<String, CostPerPiece> lastRunMetadata,
      @Mock Iterable<SourceDto<CostPerPiece>> srcIterable,
      @Mock Iterator<SourceDto<CostPerPiece>> srcIterator,
      @Mock SourceDto<CostPerPiece> srcDto,
      @Mock CostPerPiece entity)
      throws MetadataReadException, SourceReadException, MappingException, MetadataWriteException,
          RunBroadcastException {

    final MappingException mappingException = mock(MappingException.class);
    final Instant FROM_DATE = Instant.now().minus(1, ChronoUnit.DAYS);

    when(metadataService.findLatest()).thenReturn(lastRunMetadata);
    when(lastRunMetadata.getStartTime()).thenReturn(FROM_DATE);
    when(srcService.getIterable(FROM_DATE)).thenReturn(srcIterable);
    when(srcIterable.iterator()).thenReturn(srcIterator);
    when(srcIterator.hasNext()).thenReturn(Boolean.TRUE, Boolean.FALSE);
    when(srcIterator.next()).thenReturn(srcDto);
    doThrow(mappingException).when(srcDto).toEntity();

    etlService.run();

    InOrder inOrder =
        inOrder(
            currentRunMetadata,
            runBroadcaster,
            metadataService,
            srcService,
            destService,
            srcIterable,
            srcIterator,
            srcDto,
            entity);

    inOrder.verify(currentRunMetadata).markRunStarted();
    inOrder.verify(runBroadcaster).broadcastRunStart();

    inOrder.verify(runBroadcaster).broadcastMetadataQuery();
    inOrder.verify(metadataService).findLatest();

    inOrder.verify(runBroadcaster).broadcastSyncInProgress(FROM_DATE);
    inOrder.verify(srcService).getIterable(FROM_DATE);
    inOrder.verify(srcIterable).iterator();

    inOrder.verify(srcIterator).hasNext();
    inOrder.verify(srcIterator).next();
    inOrder.verify(srcDto).toEntity();
    inOrder.verify(currentRunMetadata).incrementExceptionCount();
    inOrder.verify(runBroadcaster).broadcastSyncException(srcDto, mappingException);
    inOrder.verify(currentRunMetadata).incrementTotal();
    inOrder.verify(srcIterator).hasNext();

    inOrder.verify(currentRunMetadata).markRunCompleted();
    inOrder.verify(runBroadcaster).broadcastMetadataSave(currentRunMetadata);
    inOrder.verify(metadataService).save(currentRunMetadata);
    inOrder.verify(runBroadcaster).broadcastRunCompleted(currentRunMetadata);

    inOrder.verifyNoMoreInteractions();
  }

  @Test
  void run_whenFatalExceptionThrown_broadcastExceptionBeforeRethrowing()
      throws MetadataReadException, RunBroadcastException {

    when(metadataService.findLatest()).thenThrow(RuntimeException.class);

    RuntimeException e = catchThrowableOfType(() -> etlService.run(), RuntimeException.class);

    assertThat(e).isInstanceOf(RuntimeException.class);

    verify(runBroadcaster).broadcastFatalException(e);
  }
}
