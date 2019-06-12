package au.gov.dxa.definition

import au.gov.api.config.Config
import com.beust.klaxon.JsonArray
import com.beust.klaxon.JsonObject
import com.beust.klaxon.Parser
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.databind.ObjectMapper
import khttp.get
import khttp.put
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.lang.Exception
import java.lang.StringBuilder
import java.net.URLEncoder

class Definition {

    constructor(){}
    constructor(iname: String, idomain: String, istatus: String, idefinition:  String, iguidance: String,
                iidentifier: String, iusage: Array<String>, itype: String, ivalues: Array<String>,
                ifacets: Map<String, String>, idomainAcronym: String, isourceURL:String = ""){
        name = iname
        domain = idomain
        status = istatus
        definition = idefinition
        guidance = iguidance
        identifier = iidentifier
        usage = iusage
        type = itype
        values = ivalues
        facets = ifacets
        domainAcronym = idomainAcronym
        sourceURL = isourceURL
    }
    var name: String = ""
    var domain: String = ""
    var status: String = ""
    var definition:  String = ""
    var guidance: String = ""
    var identifier: String = ""
    var usage: Array<String> = arrayOf()
    var type: String = ""
    var values: Array<String> = arrayOf()
    var facets: Map<String, String> = mapOf()
    var domainAcronym: String = ""
    var sourceURL:String = ""
}

data class SearchResults<T>(val results: List<T>, val howManyResults: Int, val usedSynonyms: Map<String,List<String>>? = null)

class Domain{
    var name:String = ""
    var acronym: String = ""
    var version:String = ""
    constructor(){}
    constructor(iname:String, iacronym: String, iversion:String){
        name = iname
        acronym = iacronym
        version = iversion
    }
}

@Service
class DefinitionService {

    val baseRepoUri = Config.get("BaseRepoURI")

    fun getDomainByAcronym(acronym:String): Domain? {
        var domain = getDomains(acronym).first()
        when (domain.acronym=="") {
            true -> return null
            false -> return domain
        }
    }
    //fun getDomains(name:String): Domain? = repository.getDomainByName(name)
    fun getDomains(acr:String = ""):List<Domain> {
        var url = if (acr != "") baseRepoUri+ "definitions/domains?domain=$acr" else baseRepoUri+ "definitions/domains"
        var response = get(url)
        var output:MutableList<Domain> = mutableListOf()
        try {
            var rawArray = splitRawJsonToArray(response.text)
            val om = ObjectMapper()
            for (jsonItem in rawArray) {
               output.add(om.readValue(jsonItem, Domain::class.java))
            }
            return output
        }
        catch (e: Exception)
        {
            e.printStackTrace()
            throw Exception("Unable to parse domains")
        }
    }

    private fun splitRawJsonToArray(json:String):List<String> {
        var rawJsonArray = json.substring(1,json.length-1).split("},{").toMutableList()
        rawJsonArray[0] = "${rawJsonArray.first()}}"
        rawJsonArray[rawJsonArray.count()-1] = "{${rawJsonArray.last()}"
        for (i in 1..rawJsonArray.count()-2) {
            var text = rawJsonArray[i]
            rawJsonArray[i] = "{$text}"
        }
        return  rawJsonArray.toList()

    }

    fun getDefinition(identifier: String): Definition {
       var response = get(baseRepoUri+"definitions/definition/detail?id=$identifier")
        try {
            var output = ObjectMapper().readValue(response.text, Definition::class.java)
            return output
        }
        catch (e: Exception)
        {
            e.printStackTrace()
            throw Exception("Unable to parse definition")
        }
    }

    fun getDefinitions(pageNumber: Int, pageSize:Int,domain: String=""): List<Definition>{
        var url = baseRepoUri+ "definitions?pageNumber=$pageNumber&pageSize=$pageSize" + getDomainPramString(domain)
        var response = get(url)
        var output:MutableList<Definition> = mutableListOf()
        try {
            var rawArray = splitRawJsonToArray(response.text)
            val om = ObjectMapper()
            for (jsonItem in rawArray) {
                output.add(om.readValue(jsonItem, Definition::class.java))
            }
            return output
        }
        catch (e: Exception)
        {
            e.printStackTrace()
            throw Exception("Unable to parse definitions")
        }
    }
    fun getDomainPramString(domain:String) :String {
        if (domain=="") return ""
        var output = ""
        if (domain.contains(' ')) {
            var splitDomains = domain.split(' ')
            for (domainAcr in splitDomains) {
                output += "&domain=$domainAcr"
            }
        } else {
            output += "&domain=$domain"
        }
        return output
    }

    fun getDefinitionsHATEOS(pageNumber: Int, pageSize:Int): List<DefinitionHATEOS>{
        val origResults = getDefinitions(pageNumber, pageSize)
        return origResults.map { DefinitionHATEOS(it) }
    }


    fun getDefinitionsForDomain(pageNumber: Int, pageSize:Int, domain: String): List<Definition>{
        return getDefinitions(pageNumber, pageSize, domain)
    }


    fun search(query: String, domain:String, page: Int, size: Int, raw:Boolean = false, ignoreSynonym: Boolean = false): SearchResults<Definition> {
        var url = baseRepoUri+ "definitions/search?query=${URLEncoder.encode(query, "UTF-8")}&page=$page&size=$size&raw=$raw&ignoreSynonym=$ignoreSynonym" + getDomainPramString(domain)
        var response = get(url)
        var responseObj = Parser().parse(StringBuilder(response.text))
        var searchResults:MutableList<Definition> = mutableListOf()
        var definitionsJSON = splitRawJsonToArray(((responseObj as Map<String,Any>).getValue("results") as JsonArray<JsonObject>).toJsonString())
        val om = ObjectMapper()
        for (jsonItem in definitionsJSON) {
            searchResults.add(om.readValue(jsonItem, Definition::class.java))
        }

        return SearchResults<Definition>(searchResults,
                (responseObj as Map<String,Int>).getValue("howManyResults"),
                (responseObj as Map<String,Map<String,List<String>>?>).getValue("usedSynonyms"))
    }

    fun searchHATEOS(query: String, domain:String, page: Int, size: Int, raw:Boolean = false, ignoreSynonym: Boolean = false): SearchResults<DefinitionHATEOS> {
        val origResults = search(query, domain, page, size, raw, ignoreSynonym)
        val results = origResults.results.map { DefinitionHATEOS(it) }
        return SearchResults(results, origResults.howManyResults)
    }

    fun howManyDefinitions(): Int{
        var response = get(baseRepoUri+ "definitions/definition/count")
        return response.text.toInt()
    }

    fun howManyDefinitionsInDomain(domain: String): Int{
        var response = get(baseRepoUri+ "definitions/definition/count?domain=$domain")
        return response.text.toInt()
    }
}