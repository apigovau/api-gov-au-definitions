---
name : "Definitions Catalogue"
description : "The Definitions Catalogue REST API allows you to search and browse for data definitions used by the Australian Government"
logo : "https://api.gov.au/img/catalogue_brand.png"
tags:
 - "Security:Open"
 - "Technology:Rest/JSON"
 - "OpenAPISpec:Swagger"
 - "AgencyAcr:ATO"
 - "Status:Published"
 - "Category:Metadata"
 - "Definitions"
 
 # MergeMapping
 # "# Getting Started" add "# Overview"
 # "# Posting" insertAfter "# Definitions"
 # "# Posting" insertAfter "# Paths"
---

# Getting Started

The Definitions Catalogue REST API allows you to search and browse for data definitions used by the Australian Government.

## Key Information

### Base URL

All URLs referenced in the documentation have the following base:

>https://api.gov.au/definitions/api/

The Definitions Catalogue API is served over both HTTP and HTTPS.

### Response Format

Responses are in JSON

## OpenAPI specification

The OpenAPI / Swagger documentation for the API is [here](/swagger-ui/index.html?url=https://api.gov.au/definitions/swagger.json)

Here's an automatically generated class diagram of the service.

[![Generated class diagram from swagger](https://api.gov.au/graph/swagger.svg?url=https://api.gov.au/definitions/swagger.json)](https://api.gov.au/graph/swagger.svg?url=https://api.gov.au/definitions/swagger.json)

Click for bigger version


We try to keep it up to date, but the documentation you're reading is you're best bet and understanding the API.

## Authentication

This API is 100% public.

There is no Authentication for read only requests.

# Collaborate

This is a placeholder page that will be populated with the collaboration features of api.gov.au

# Using the API

## HATEOS

Most of the resources returned the API follow the HATEOS approach: links are provided to help you navigate around.

This API uses HATEOS for things like:
- paging
- getting specific details about defintions

HATEOS in this API follows this general pattern:

```json
{
    "content": { ... the resource data ...},
    "links": [
        {
            "rel": "a_name_for_the_link",
           "href": "a_link_to_related_things"
         },
         ... more rels/hrefs ...
    ]
}
```



## Paging

This API pages results.

The `page` query parameter selects which result page to return.

The `size` query parameter specifies how many results per page to return. The default is 20 and the maximum is 100.

A page of results give you helpful information about navigating the other result pages.

Result pages look like this:

```json
{
  "content": [
	... the result page data ...
  ],
  "numberOfElements": 123,
  "firstPage": true,
  "lastPage": false,
  "totalPages": 7,
  "links": [
    {
      "rel": "first",
      "href": "http:/api.gov.au/definitions/api/search?query=car&page=1&size=20"
    },
    {
      "rel": "last",
      "href": "http:/api.gov.au/definitions/api/search?query=car&page=7&size=20"
    },
    {
      "rel": "self",
      "href": "http://api.gov.au/definitions/api/search?query=car&page=1&size=20"
    },
    {
      "rel": "next",
      "href": "http://api.gov.au/definitions/api/search?query=car&page=2&size=20"
    }
  ],
  "id": {
    "rel": "self",
    "href": "http://api.gov.au/definitions/api/search?query=car&page=1&size=20"
  }
}
```


## The web version


There is a [web version of the catalogue](https://api.gov.au/definitions) that mirrors the functionality of this API.

In general, the API uri's follow the same pattern as the web ones but with the '/api/' path segment removed.

For example:

- [http://api.gov.au/definitions/definition/other/de55](http://api.gov.au/definitions/definition/other/de55) <-- the web version
- [http://api.gov.au/definitions/api/definition/other/de55](http://api.gov.au/definitions/api/definition/other/de55) <-- the api version

So to get an easy head start on what you can do wit the API, head over to the web version and take a look.


## Rate Limiting


At the moment there is no imposed rate limiting.

We'll monitor the service and impose limiting if it has trouble keeping up.


# Search

The search service is available at this URL

>https://api.gov.au/definitions/api/search

In addition to the [paging](Using+the+API#Paging) parameters, search uses the following:

The `query` query parameter specifies the query to search on.

The `domain` query parameter specifies a domain to restrict the search to. If you don't provide a value for `domain`, the definitons from all domains will be searched.

More information about domains, and the values the parameter takes is [here](Domains).

The response from the search, including the [HATEOS](Getting+Started#HATEOS) wrapper, service looks like this:

> http://api.gov.au/definitions/api/search?query=de10

```json
{
  "content": [
    {
      "content": {
        "name": "Electronic Contact Facsimile Area Code",
        "domain": "Other",
        "status": "Standard",
        "definition": "This element describes a standard Australian Area Code as used in conjunction with facsimile numbers.",
        "guidance": "",
      	"identifier": "http://api.gov.au/definition/other/de10",
      	"usage": [
                "Australian Taxation Office",
                "NSW Office of Revenue",
                "SA Office of Revenue",
                "VIC Office of Revenue"
              ],
        "type": "string",
        "values": [

        ],
        "facets": {
          "pattern": "[0-9]{2}"
        }
      },
      "links": [
        {
          "rel": "syntax",
          "href": "http://api.gov.au/definitions/api/syntax/other/de10"
        }
      ]
    }
  ],
  "numberOfElements": 1,
  "firstPage": true,
  "lastPage": true,
  "totalPages": 1,
  "links": [
    {
      "rel": "first",
      "href": "http://api.gov.au/definitions/api/search?query=de10&page=1&size=20"
    },
    {
      "rel": "last",
      "href": "http://api.gov.au/definitions/api/search?query=de10&page=1&size=20"
    },
    {
      "rel": "self",
      "href": "http://api.gov.au/definitions/api/search?query=de10&page=1&size=20"
    }
  ],
  "id": {
    "rel": "self",
    "href": "http://api.gov.au/definitions/api/search?query=de10&page=1&size=20"
  }
}
```

## Examples

Here is an example call that searches for the words 'motor vehicle' in all domains:

```json
http://api.gov.au/definitions/api/search?query=motor+vehicle
```

And and example speficying a domain:
```json
http://api.gov.au/definitions/api/search?query=motor+vehicle&domain=fs
```

And an example with paging:

```json
http://api.gov.au/definitions/api/search?query=motor+vehicle&domain=fs&page=1&size=20
```

## Synonyms

We will include any results that have [synonyms](http://api.gov.au/definitions/synonyms) from your search query.

We want to make the synonym list better, so contact us if you have updates.

# Browse

The base URL for the browse service is:

>http://api.gov.au/definitions/api/browse

In addition to the [paging](Using+the+API#Paging) parameters, search uses the following:

The `domain` query parameter specifies a domain to browse within.

If you don't provide a value for `domain`, the definitons from all domains will be browsed.

More information about domains, and the values the parameter takes is [here](Domains).

## Examples

Here is an example result from the browse service, including the [HATEOS](Getting+Started#HATEOS) wrapper:

>http://api.gov.au/definitions/api/browse?domain=fi&page=1&size=1

```json
{
  "content": [
    {
      "content": {
        "name": "Electronic Contact Facsimile Area Code",
        "domain": "Other",
        "status": "Standard",
        "definition": "This element describes a standard Australian Area Code as used in conjunction with facsimile numbers.",
        "guidance": "",
        "identifier": "http://api.gov.au/definition/other/de10",
        "usage": [
          "Australian Taxation Office",
          "NSW Office of Revenue",
          "SA Office of Revenue",
          "VIC Office of Revenue"
        ],
        "type": "string",
        "values": [

        ],
        "facets": {
          "pattern": "[0-9]{2}"
        }
      },
      "links": [
        {
          "rel": "syntax",
          "href": "http://api.gov.au/definitions/api/syntax/other/de10"
        }
      ]
    }
  ],
  "numberOfElements": 6338,
  "firstPage": true,
  "lastPage": false,
  "totalPages": 6338,
  "links": [
    {
      "rel": "first",
      "href": "http://api.gov.au/definitions/api/definitions?domain=fi&page=1&size=1"
    },
    {
      "rel": "last",
      "href": "http://api.gov.au/definitions/api/definitions?domain=fi&page=6338&size=1"
    },
    {
      "rel": "self",
      "href": "http://api.gov.au/definitions/api/definitions?domain=fi&page=1&size=1"
    },
    {
      "rel": "next",
      "href": "http://api.gov.au/definitions/api/definitions?domain=fi&page=2&size=1"
    }
  ],
  "id": {
    "rel": "self",
    "href": "http://api.gov.au/definitions/api/definitions?domain=fi&page=1&size=1"
  }
}
```

# Details

The details of a definition can be found at this URL:

> https://api.gov.au/definitions/api/definition/{domain}/{id}


The `domain` URL parameter specifies a domain the definition is in.

More information about domains, and the values the parameter takes is [here](Domains).

The `id` URL parameter specifies the unique id, within a domain, for the definition.


The results of the details service look like this:

```json
{
  "content": {
    "name": "Electronic Contact Facsimile Area Code",
    "domain": "Other",
    "status": "Standard",
    "definition": "This element describes a standard Australian Area Code as used in conjunction with facsimile numbers.",
    "guidance": "",
    "identifier": "https://api.ausdx.io/definition/otherde10",
    "usage": [
      "Australian Taxation Office",
      "NSW Office of Revenue",
      "SA Office of Revenue",
      "VIC Office of Revenue"
    ],
    "type": "string",
    "values": [

    ],
    "facets": {
      "pattern": "[0-9]{2}"
    }
  },
  "links": [
    {
      "rel": "syntax",
      "href": "http://api.gov.au/definitions/api/syntax/other/de10"
    }
  ]
}
```

## Finding ids

Each result from the search or browse serviecs includes all the details of a definition.

If you want to call the detail service anyway, you can use the definition's `identifier` field.

This URL is for the web version - if you want the API version, insert /api before /definition/ (eg: http://api.gov.au/definitions/api/definition/other/de10):

eg:
```json
"content": {
        "name": "Electronic Contact Facsimile Area Code",
        "domain": "Other",
        "status": "Standard",
        "definition": "This element describes a standard Australian Area Code as used in conjunction with facsimile numbers.",
        "guidance": "",

       --->  "identifier": "http://api.gov.au/definition/other/de10",

        "usage": [
          "Australian Taxation Office",
          "NSW Office of Revenue",
          "SA Office of Revenue",
          "VIC Office of Revenue"
        ],
        "type": "string",
        "values": [

        ],
        "facets": {
          "pattern": "[0-9]{2}"
        }
      },
      "links": [
        {
          "rel": "syntax",
          "href": "http://api.gov.au/definitions/api/syntax/other/de10"
        }
      ]
    }
```

## Syntax specific metadata

Some definitions have metadata for specific technologies.

The metadata service is at this URL:

> https://api.gov.au/definitions/api/syntax/{domain}/{id}


The syntax URL for every definition is included in the HATEOS links in the search, browse and detail services.


The results from the syntax service look like:

```json
{
  "syntaxes": {
    "xbrl": {
      "period": "duration",
      "classification": "py/pyde/pyde.02.00"
    }
  }
}
```

The fields will vary based on the syntax, but we believe they will always be key:value pairs.

# Domains

The set of current domains is available here:

>https://api.gov.au/definitions/api/domains

It returns something like this:

```json
[
  {
    "name": "Other",
    "acronym": "other",
    "version": "2017.02.81"
  },
  {
    "name": "Financial Statistics",
    "acronym": "fs",
    "version": "2017.02.81"
  },
  {
    "name": "Financial Insolvency",
    "acronym": "fi",
    "version": "2018-01-10_09:42"
  }
]
```


This API uses the `acronym` field when passing domains as parameteres.

# Posting

## Adding and Updating Definitions

All additions and updates to definitions can be done via this endpoint:

> https://api.gov.au/definitions/api/definition/{domain}/{id}

Replace the ```{domain}``` and ```{id}``` with the domain and id of the definition you wish to update. For new definitions the ```{id}``` should be set to "new".

Make a ```POST``` request with your api key and details about the new definition. The post body needs to be valid JSON, example post request body:

```json
{
   "name":"Welfare Lodgment Document Checklist Item Document Provided Indicator",
   "usage":[
      "Centerlink"
   ],
   "domain":"Welfare",
   "status":"published",
   "datatype":{
      "type":"boolean",
      "facets":{

      }
   },
   "guidance":"",
   "definition":"Indicates that the document specified has been included in the lodgment.",
   "identifier":" ",
   "domainAcronym":"wf"
}
```

Note that the "identifier" attribute should be left blank when creating a new definition.

If your request is successful the identifier of the definition will be sent back.
