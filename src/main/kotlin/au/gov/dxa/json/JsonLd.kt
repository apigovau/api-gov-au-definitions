package au.gov.dxa.json

import au.gov.dxa.definition.Definition
import au.gov.dxa.definition.Domain
import au.gov.dxa.relationship.Direction
import au.gov.dxa.relationship.RelationDTO
import com.beust.klaxon.JsonArray
import com.beust.klaxon.JsonObject
import com.beust.klaxon.string

// https://json-ld.org/
class JsonLd(path:String) {
    var domain: Domain = Domain("","","")
    var definitions = listOf<Definition>()
    var relations = mutableListOf<RelationDTO>()

    init {
        @Suppress("UNCHECKED_CAST")
        val data = JsonHelper.parse(path) as JsonArray<JsonObject>

        val root = data[0]
        val name = value(root[PURL_TITLE])
        val acronym = value(root[RDFS_LABEL])
        val version = value(root[PURL_MODIFIED])
        domain = Domain(name,acronym,version)

        definitions = data.drop(1).map { getDefinition(it, domain) }
        for(relation in data.drop(1)){
            relations.addAll(getRelationships(relation))
        }
    }

    private fun getRelationships(data: JsonObject): MutableList<RelationDTO>{
        val id = data["@id"] as String? ?:""
        val identifier = "http://dxa.gov.au/definition/" + id.split("/").takeLast(2).joinToString("/")
        val list = mutableListOf<RelationDTO>()

        for(relationType in listOf("http://www.w3.org/2004/02/skos/core#related","http://www.w3.org/2004/02/skos/core#broader","http://www.w3.org/2004/02/skos/core#narrower")){
            val relation = id(data[relationType])
            if(relation == "") continue
            val to = "http://dxa.gov.au/definition/" + relation.split("/").takeLast(2).joinToString("/")

            list.add (RelationDTO(identifier, "rdfs:seeAlso",to,Direction.TO))
        }


        return list
    }

    private fun getDefinition(data:JsonObject, domain :Domain):Definition{
        val id = data["@id"] as String? ?:""
        val definition = value(data[SKOS_DEFINITION])
        var name = value(data[SKOS_PREFLABEL])
        if(name == "") name = getNameFromId(id)
        val identifier = "http://dxa.gov.au/definition/" + id.split("/").takeLast(2).joinToString("/")
        return Definition(name, domain.name,"",definition,"",identifier, arrayOf(),"",arrayOf(),mapOf(),domain.acronym, id)
    }

    private fun getNameFromId(id:String):String = id.split("/").last().replace("-"," ")

    companion object {
        @JvmStatic val PURL_TITLE = "http://purl.org/dc/terms/title"
        @JvmStatic val RDFS_LABEL = "http://www.w3.org/2000/01/rdf-schema#label"
        @JvmStatic val PURL_MODIFIED = "http://purl.org/dc/terms/modified"
        @JvmStatic val SKOS_DEFINITION = "http://www.w3.org/2004/02/skos/core#definition"
        @JvmStatic val SKOS_PREFLABEL = "http://www.w3.org/2004/02/skos/core#prefLabel"

        @JvmStatic fun value(node:Any?):String{
            if(node == null) return ""
            @Suppress("UNCHECKED_CAST")
            return (node as JsonArray<JsonObject>)[0].string("@value")?:""
        }

        @JvmStatic fun id(node:Any?):String{
            if(node == null) return ""
            @Suppress("UNCHECKED_CAST")
            return (node as JsonArray<JsonObject>)[0].string("@id")?:""
        }
    }
}