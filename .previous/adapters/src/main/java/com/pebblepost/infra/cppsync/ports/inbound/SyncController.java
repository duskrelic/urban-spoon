package com.pebblepost.infra.cppsync.ports.inbound;

import com.pebblepost.infra.cppsync.usecases.sync.SyncLatestFileService;

import java.io.IOException;

public class SyncController {

  private final SyncLatestFileService service;

  public SyncController(SyncLatestFileService service) {
    this.service = service;
  }

  public void run() throws IOException {
    service.sync();
  }
}
