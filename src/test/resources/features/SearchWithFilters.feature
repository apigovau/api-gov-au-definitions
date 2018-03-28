@SearchWithWithFilters
Feature: Search with filters
  As a Data Modeller
  I want filter my search results
  So that I can narrow down my search results

  Background: User is on the main page
    Given I am on the main page
    Then I will be on the page "/" and see "Definitions Catalogue" in a "h1"
    Then I will see "Search" in a "input"

  Scenario: Searching for 'Course code' with the with a filterd domain of 'Education'
    When I filter on the domain "edu" and search for "Course code"
    Then I will see "Education" in a "ul"
    Then I will see "x" in a "ul"
    Then I will see "Course of study code" in a "a"

  Scenario: Searching for 'Course code' in a domain where it doesnt exist
    When I filter on the domain "ce" and search for "Course code"
    Then I will see "Education" in a "ul"
    Then I will see "x" in a "ul"
    Then I won't see "Course of study code" in a "a"

  Scenario: Searching for car does not aso search motor vehicle when ignorning synonyms
    When I ignore synonyms and search for "car"
    Then I won't see "motor vehicle" in a "span"
    Then I won't see "Expense Operating Motor Vehicle Type Or Claim Method Code" in a "a"
