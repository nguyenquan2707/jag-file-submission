# new feature
# Tags: optional

Feature: Rush processing data point can be added to the package

  Scenario: Validate the rush processing data point can be added to the package
    Given valid user is authenticated
    When request is posted to submission to add rush processing
    Then rush processing is added to the package