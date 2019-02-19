@SearchWithSynonyms
Feature: Search with synonyms
  As a Data Modeller
  I want search to include common alternatives for terms
  So that I can find definitions I want without having to know the exact jargon to search for

  Background: User is on the main page
    Given I am on the main page
    Then I will be on the page "/" and see "Definitions Catalogue" in a "h1"
    Then I will see "Search" in a "input"

  Scenario: Searching for car also searches for synonyms motor vehicle
    When I search for "car"
    Then I will see "synonyms" in a "a"
    Then I will see "motor vehicle" in a "span"
    Then I will see "Luxury Car Tax Claimable Credits Amount" in a "a"

  Scenario: Searching for address doesn't include any synonyms
    When I search for "address"
    Then I won't see "synonyms" in a "a"
