Feature: Application checks the local filesystem for new files

  Return true if the specified local directory contains at least one new file,
  otherwise return false

  Background:
    Given a lastChecked timestamp
    And a localDirectory with no files newer than lastChecked

  Scenario: A new file has been added to localDirectory after lastChecked
    When a new file has been added to localDirectory
    And the sync job checks localDirectory for new files
    Then the local filesystem check returns true

  Scenario: Multiple new files have been added to localDirectory after lastChecked
    When multiple new files have been added to localDirectory
    And the sync job checks localDirectory for new files
    Then the local filesystem check returns true

  Scenario: No new files have been added to localDirectory after lastChecked
    When multiple new files have been added to localDirectory
    And the sync job checks localDirectory for new files
    Then the local filesystem check returns false
