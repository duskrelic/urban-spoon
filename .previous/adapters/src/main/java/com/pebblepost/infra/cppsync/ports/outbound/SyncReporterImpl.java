package com.pebblepost.infra.cppsync.ports.outbound;

import com.pebblepost.infra.cppsync.ports.inbound.SyncLatestFileUseCase.SyncSummary;

public class SyncReporterImpl implements SyncReporter {

  @Override
  public void reportSummary(SyncSummary summary) {}
}
