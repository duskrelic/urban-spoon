package com.pebblepost.infra.cppsync.ports.outbound;

import lombok.NonNull;
import lombok.Setter;
import lombok.SneakyThrows;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.Instant;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Objects;

@Setter
class S3Downloader implements FileDownloader {

  @NonNull private String targetDirectory;

  @Override
  public File getLatestFile() throws RuntimeException {
    return Arrays.stream(Objects.requireNonNull(new File(targetDirectory).listFiles()))
        .max(Comparator.comparing(this::getLastAccessTime))
        .orElseThrow();
  }

  @Override
  public Instant getAddedTime(File latestFile) {
    return null;
  }

  @SneakyThrows
  private Instant getLastAccessTime(File file) {
    return Files.readAttributes(file.toPath(), BasicFileAttributes.class)
        .lastAccessTime()
        .toInstant();
  }
}
