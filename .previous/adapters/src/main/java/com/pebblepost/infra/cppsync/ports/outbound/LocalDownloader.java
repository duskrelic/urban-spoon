package com.pebblepost.infra.cppsync.ports.outbound;

import lombok.SneakyThrows;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.Instant;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Objects;

class LocalDownloader implements FileDownloader {

  private final Path targetDirectory;

  public LocalDownloader(Path targetDirectory) {
    this.targetDirectory = targetDirectory;
  }

  @Override
  public File getLatestFile() throws RuntimeException {
    final File[] dir = targetDirectory.toFile().listFiles();
    return Arrays.stream(Objects.requireNonNull(dir))
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
