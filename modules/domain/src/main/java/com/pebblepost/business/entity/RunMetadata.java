package com.pebblepost.business.entity;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.time.Instant;

@Getter
@ToString
@RequiredArgsConstructor
public abstract class RunMetadata<RunKey, Entity> {

  @NonNull private final RunKey runKey;

  private Instant startTime;
  private Instant endTime;
  private Long exceptionCount = 0L;
  private Long total = 0L;

  public void markRunStarted() {
    startTime = Instant.now();
  }

  public void markRunCompleted() {
    endTime = Instant.now();
  }

  public void incrementTotal() {
    total++;
  }

  public void incrementExceptionCount() {
    exceptionCount++;
  }
}
