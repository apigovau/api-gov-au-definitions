package au.gov.dxa.controller

import au.gov.dxa.definition.Definition
import au.gov.dxa.definition.DefinitionService
import au.gov.dxa.dictionary.DictionaryService
import au.gov.dxa.relationship.RelationshipService
import au.gov.dxa.relationship.Result
import au.gov.dxa.search.SearchDTO
import au.gov.dxa.synonym.SynonymService
import au.gov.dxa.syntax.SyntaxService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import javax.servlet.http.HttpServletRequest
import java.net.URL


@Controller
class Controller {

    @Autowired
    private lateinit var definitionService: DefinitionService

    @Autowired
    private lateinit var dictionaryService: DictionaryService

    @Autowired
    private lateinit var synonymService: SynonymService

    @Autowired
    private lateinit var syntaxService: SyntaxService

    @Autowired
    private lateinit var relationshipService: RelationshipService

    @Autowired
    private val request: HttpServletRequest? = null

    @RequestMapping("/")
    fun searchSubmit(@ModelAttribute search: SearchDTO,
                     model: MutableMap<String, Any>,
                     @RequestParam(defaultValue = "20") size: Int,
                     @RequestParam(defaultValue = "0") page: Int,
                     @RequestParam(defaultValue = "false") raw: Boolean): String {
        val searchString = search.getQuery() ?: ""
        val filter = getFilterModel(search)
        model["search"] =  SearchDTO()
        model["domains"] = definitionService.getDomains()
        model["filter"] = filter
        if(searchString != "") {
            model["showResults"] = "true"
            model["queryString"] = searchString
            val results = definitionService.search(searchString, search.getDomainSearchQuery(),page, size, raw, search.getIgnoreSynonym(false))
            val pageResult = PageResult(results.results, URLHelper().getURL(request), results.howManyResults)
            populateResultsPage(pageResult, model, searchString,filter)
            if(results.usedSynonyms != null) model["usedSynonyms"] = results.usedSynonyms
        }

        return "search"
    }






    @RequestMapping("/help")
    fun help():String = "help"

    @RequestMapping("/definitions")
    internal fun definitions(@ModelAttribute search: SearchDTO,
                             model: MutableMap<String, Any>,
                             @RequestParam(defaultValue = "20") size: Int,
                             @RequestParam(defaultValue = "0") page: Int): String {
        val searchString = search.getQuery() ?: ""
        val filter = getFilterModel(search)
        model["search"] = SearchDTO()
        model["filter"] = filter
        if(searchString != "") {
            model["action"] =  "/definitions"
            model["showResults"] = "true"
            model["queryString"] = searchString
            val results = definitionService.search(searchString, search.getDomainSearchQuery(),page, size, false, search.getIgnoreSynonym(false))
            val pageResult = PageResult(results.results, URLHelper().getURL(request), results.howManyResults)
            populateResultsPage(pageResult, model, searchString, filter)
            if(results.usedSynonyms != null) model["usedSynonyms"] = results.usedSynonyms
        }else {
            val pageResult = PageResult(definitionService.getDefinitions(page, size), URLHelper().getURL(request), definitionService.howManyDefinitions())
            populateResultsPage(pageResult, model,filter=filter)
        }
        return "browse"
    }


    @RequestMapping("/definitions/{domain}")
    internal fun definitionsForDomain(
            @ModelAttribute search: SearchDTO,
            model: MutableMap<String, Any>,
            @RequestParam(defaultValue = "20") size: Int,
            @RequestParam(defaultValue = "0") page: Int,
            @PathVariable domain:String): String {
        val searchString = search.getQuery() ?: ""
        val domainName = definitionService.getDomainByAcronym(domain)?.name ?: "No domain called '$domain' "
        val filter = getFilterModel(search)
        if(domainName != "") model["domainName"] = domainName
        model["search"] = SearchDTO()
        model["filter"] = filter
        if(searchString != "") {
            model["action"] = "/definitions/$domain"
            model["showResults"] =  "true"
            model["queryString"] = searchString
            val results = definitionService.search(searchString, domain,page, size,false,search.getIgnoreSynonym(false))
            val pageResult = PageResult(results.results, URLHelper().getURL(request), results.howManyResults)
            populateResultsPage(pageResult, model, searchString, filter)
            if(results.usedSynonyms != null) model["usedSynonyms"] = results.usedSynonyms
        }else {
            val pageResult = PageResult(definitionService.getDefinitionsForDomain(page, size, domain), URLHelper().getURL(request), definitionService.howManyDefinitionsInDomain(domain))
            populateResultsPage(pageResult, model, filter=filter)
        }
        return "browse"
    }

    private fun populateResultsPage(pageResult: PageResult<Definition>, model: MutableMap<String, Any>, queryString : String = "", filter: Filters? = null ) {
        val definitions = pageResult.content
        if (!pageResult.isFirstPage()) model["prevPage"] =  pageResult.thePrevPage(false)
        if (!pageResult.isLastPage()) model["nextPage"] = pageResult.theNextPage(false)
        model["pageNumber"] = pageResult.pageNumber
        model["pageURL"] =  pageResult.uri
        model["lastPageNumber"] = pageResult.getTotalPages()
        model["totalResults"] = pageResult.numberOfElements
        model["spellCheck"] = if (pageResult.numberOfElements == 0) dictionaryService.getDictionaryCorrection(queryString, filter) else ""

        val pagesToTheLeft = pageResult.pagesToTheLeft()
        if (pagesToTheLeft.size == 1) {
            model["leftPage"] = pagesToTheLeft[0]
            model["leftPageNumber"] = 1
        }
        if (pagesToTheLeft.size == 2) {
            model["leftPage"] = pagesToTheLeft[1]
            model["leftPageNumber"] = pageResult.pageNumber - 1
            model["firstPage"] = pageResult.theFirstPage()
        }

        val pagesToTheRight = pageResult.pagesToTheRight()
        if (pagesToTheRight.size == 1) {
            model["rightPage"] = pagesToTheRight[0]
            model["rightPageNumber"] = pageResult.getTotalPages()
        }
        if (pagesToTheRight.size == 2) {
            model["rightPage"] = pagesToTheRight[1]
            model["rightPageNumber"] = pageResult.pageNumber + 1
            model["lastPage"] = pageResult.theLastPage()
        }


        val maxLength = 200

        data class ViewDefinition(val name: String, val domain: String, val definition: String, val identifier: String, val status: String, val type:String)

        val viewDefns = mutableListOf<ViewDefinition>()
        for (definition in definitions) {
            val localHref = definition.identifier.replace("http://dxa.gov.au", "")
            var shortDef = definition.definition
            if (shortDef.length > maxLength) {
                shortDef = shortDef.substring(0, maxLength) + " ..."
            }
            viewDefns.add(ViewDefinition(definition.name, definition.domain, shortDef, localHref, definition.status, definition.type))
        }
        model["definitions"] = viewDefns
    }

    private fun getFilterModel(search: SearchDTO):Filters {
        var ignoreSyn = search.getIgnoreSynonym(false)
        val filterDom = search.getDomainList(false) ?: listOf()
        val domains = definitionService.getDomains()
        val l : MutableList<au.gov.dxa.definition.Domain> = arrayListOf()
        domains.forEach{if (filterDom.contains(it.acronym)){ l.add(it) }}
        return Filters(l,ignoreSyn)
    }

    @RequestMapping("/definition/{domain}/{id}")
    internal fun detail(model: MutableMap<String, Any>, @PathVariable domain:String, @PathVariable id:String) : String{

        val identifier = """http://dxa.gov.au/definition/$domain/$id"""
        val definition = definitionService.getDefinition(identifier)

        model["name"] = definition.name
        model["domain"] = definition.domain
        model["status"] = definition.status
        model["definition"] = definition.definition
                .replace("\n","<br/>")
                .replace("\t","&nbsp;&nbsp;&nbsp;&nbsp;")
                .replace("  ", "&nbsp;&nbsp;")

        model["guidance"] = definition.guidance
                .replace("\n","<br/>")
                .replace("\t","&nbsp;&nbsp;&nbsp;&nbsp;")
                .replace("  ", "&nbsp;&nbsp;")

        model["identifier"] = definition.identifier
        model["href"] = definition.identifier.replace("http://dxa.gov.au","")
        model["usage"] = definition.usage
        model["api"] = definition.identifier.replace("http://dxa.gov.au","http://dxa.gov.au/api")
        if(definition.type != "") model["type"] = definition.type
        if(definition.sourceURL != "") model["source"] = definition.sourceURL
        model["typeValues"] = definition.values
        model["typeFacets"] = definition.facets

        val relations = relationshipService.getRelations(definition.identifier)
        if(relations.isNotEmpty()){
            model["relationShipImageUrl"] = URLHelper().convertURL(request,model["api"].toString())
            val relationsWithDefinitions = addDefinitionToRelationshipResults(relations)
            model["relationships"] = relationsWithDefinitions
        }

        val syntaxes = syntaxService.getSyntax(identifier)
        if(syntaxes != null){
            model["syntaxes"] = syntaxes.syntaxes
        }

        return "detail"
    }

    private fun addDefinitionToRelationshipResults(relations: Map<String, List<Result>>): MutableMap<String, MutableList<ResultWithDefinition>> {
        val relationsWithDefinitions = mutableMapOf<String, MutableList<ResultWithDefinition>>()
        for (relationName in relations.keys) {
            val definitions = mutableListOf<ResultWithDefinition>()
            for (result in relations[relationName]!!) {
                val definition = definitionService.getDefinition(result.to)
                val newURL = URLHelper().convertURL(request, result.to)
                val newResult = Result(result.meta, result.direction, newURL, definition.name)
                definitions.add(ResultWithDefinition(newResult, definition))
            }
            relationsWithDefinitions[relationName] = definitions
        }
        return relationsWithDefinitions
    }

    @RequestMapping("/synonyms")
    internal fun synonyms(model: MutableMap<String, Any> ): String {
        model["synonyms"] = synonymService.origSynonyms
        return "synonyms"
    }

    data class ResultWithDefinition(var result: Result, val definition: Definition)

}
class Filters(val Domains:MutableList<au.gov.dxa.definition.Domain>, val IgnoreSynonym: Boolean){

}
