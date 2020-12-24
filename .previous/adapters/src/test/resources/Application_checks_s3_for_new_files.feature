Feature: Application checks S3 for new files

  Return true if the specified S3 bucket contains at least one new file,
  otherwise return false

  Background:
    Given a lastChecked timestamp
    And an s3Bucket with no files newer than lastChecked

  Scenario: A new file has been added to the s3Bucket after lastChecked
    When a new file has been added to s3Bucket
    And the sync job checks s3Bucket for new files
    Then the s3 check returns true

  Scenario: Multiple new files have been added to s3Bucket after lastChecked
    When multiple new files have been added to s3Bucket
    And the sync job checks s3Bucket for new files
    Then the s3 check returns true

  Scenario: No new files have been added to s3Bucket after lastChecked
    When multiple new files have been added to s3Bucket
    And the sync job checks s3Bucket for new files
    Then the s3 check returns true
