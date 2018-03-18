@SemanticRelationships
Feature: Follow semantic relationships to other definitions
  As a Data Modeller
  I want to see what is related to a definitions
  So that I can explore for information around what I'm looking for

  Background: User has found a definition that has relationships
    Given I am on page "/definition/ce/ce1"
    Then I will see "Address" in a "h1"

  Scenario: I can see what this definition is related to
    Then I will see "Related definitions" in a "dt"
    Then I will see "rdfs:seeAlso" in a "dt"

  Scenario: Clicking on a related definition takes me to that page
    Then I will see "LegalEntity" in a "a"
    When I click the "LegalEntity" link
    Then I will be on the page "/definition/ce/ce32" and see "LegalEntity" in a "h1"

  Scenario: I can see a relationhsip link back to the definition I came from
    Then I will see "Related definitions" in a "dt"
    Then I will see "rdfs:seeAlso" in a "dt"
    Then I will see "Address" in a "a"
