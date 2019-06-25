package au.gov.dxa.controller

import au.gov.dxa.definition.DefinitionHATEOS
import au.gov.dxa.definition.DefinitionService
import au.gov.dxa.relationship.RelationshipService
import au.gov.dxa.relationship.Result
import au.gov.dxa.synonym.SynonymService
import au.gov.dxa.syntax.Syntax
import au.gov.dxa.syntax.SyntaxService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import java.net.URL
import javax.servlet.http.HttpServletRequest


@RestController
class APIController {

    @Autowired
    private lateinit var definitionService: DefinitionService

    @Autowired
    private lateinit var synonymService: SynonymService

    @Autowired
    private lateinit var syntaxService: SyntaxService

    @Autowired
    private lateinit var relationshipService: RelationshipService

    @Autowired
    private val request: HttpServletRequest? = null


    @ResponseStatus(HttpStatus.FORBIDDEN)
    class UnauthorisedToAccessMonitoring() : RuntimeException()

    /* Legacy
    @CrossOrigin
    @GetMapping("/monitor")
    fun test_db_stats(@RequestParam authKey:String):Map<String, Any>{
        var authKeyEnv: String = System.getenv("authKey") ?: ""
        if(authKey != authKeyEnv) throw UnauthorisedToAccessMonitoring()

        val map = mutableMapOf<String,Any>()
        map["queryLogTableRows"] = queryLogger.numberOfQueries()
        map["definitionCount"] = definitionService.howManyDefinitions()

        return map
    }*/


    @CrossOrigin
    @GetMapping("/api/definition/{domain}/{id}")
    fun specific_definition(@PathVariable domain:String, @PathVariable id:String): DefinitionHATEOS {
        val identifier = "http://api.gov.au/definition/$domain/$id"
        val definition =  definitionService.getDefinition(identifier)
        return DefinitionHATEOS(definition)
    }

    @CrossOrigin
    @GetMapping("/api/" +
            "")
    fun browse_definitions(@RequestParam(defaultValue = "20") size: Int,
                           @RequestParam(defaultValue = "0") page: Int): PageResult<DefinitionHATEOS> {
        return PageResult(definitionService.getDefinitionsHATEOS(page, size), URLHelper().getURL(request), definitionService.howManyDefinitions())
    }

    @CrossOrigin
    @GetMapping("/api/domains")
    fun domains():List<au.gov.dxa.definition.Domain>{
        val output = definitionService.getDomains()
        return output
    }

    @CrossOrigin
    @GetMapping("/api/search")
    fun search_definitions(
            @RequestParam(defaultValue = "") query: String,
            @RequestParam(defaultValue = "") domain: String,
            @RequestParam(defaultValue = "20") size: Int,
            @RequestParam(defaultValue = "0") page: Int
    ): PageResult<DefinitionHATEOS> {
        val results = definitionService.searchHATEOS(query, domain, page, size)
        return PageResult(results.results, URLHelper().getURL(request), results.howManyResults)
    }


    @CrossOrigin
    @GetMapping("/api/syntax/{domain}/{id}")
    fun specific_definition_syntax(@PathVariable domain:String, @PathVariable id:String): Syntax? {
        val identifier = "http://api.gov.au/definition/$domain/$id"
        return syntaxService.getSyntax(identifier)
    }


    @CrossOrigin
    @GetMapping("/api/relations/{domain}/{id}")
    fun relations(@PathVariable domain:String, @PathVariable id:String): Map<String,List<Result>> {
        val identifier = "http://api.gov.au/definition/$domain/$id"
        val relations =  relationshipService.getRelations(identifier)
        for(relation in relations.keys){
            for(result in relations[relation]!!) {
                val definition = definitionService.getDefinition(result.to)
                result.toName = definition.name
            }
        }
        return relations
    }

    /*

    Example code to recieve files.
    Exploring this idea of synchronising between domains.

    @CrossOrigin
    @PostMapping("/api/domains/{domain}")
    fun specific_definition_syntax(@PathVariable domain:String, @RequestBody body: Any?): String {
        println(request)
        return "Seems ok."
    }
    */

    @CrossOrigin
    @GetMapping("/api/synonyms")
    fun synonyms():MutableList<List<String>>{
        return synonymService.getAllSynonyms()
    }

    @GetMapping("/api/imgRedirect.svg")
    fun imgRedirect(@RequestParam url:String):String{
        if(url.contains("localhost")) return """<svg id="graph00" xmlns="http://www.w3.org/2000/svg" width="200" height="50" version="1.1"><text x="5" y="20">No images in dev.</text></svg>"""
        return URL(url).readText()
    }

}
