@BrowseDefinitions
Feature: Definition Details
  As a Data Modeller
  I want to show the details of a definition
  In order to decide if I will use it

  Background: User navigates to Tax domain
    Given I am on the main page
    Then I will be on the page "/" and see "Definitions Catalogue" in a "h1"
    Then I will see "Taxation and revenue collection" in a "a"


  Scenario: Browse tax definitions
    When I click the "Taxation and revenue collection" link
    Then I will be on the page "/definitions/trc" and see "Taxation and revenue collection" in a "h2"
    Then I will see "Electronic Contact Facsimile Area Code" in a "a"
    When I click the "Electronic Contact Facsimile Area Code" link
    Then I will be on the page "/definition/trc/de10" and see "Electronic Contact Facsimile Area Code" in a "h1"
    Then I will see "ElectronicContact.Facsimile.Area.Code" in a "dd"