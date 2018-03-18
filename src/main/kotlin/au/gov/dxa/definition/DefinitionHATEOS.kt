package au.gov.dxa.definition

import com.fasterxml.jackson.annotation.JsonIgnore


class DefinitionHATEOS(definition: Definition){

    var content = definition

    @JsonIgnore
    private val hrefLinks = mapOf("syntax" to "http://definitions.ausdx.io/api/syntax", "relations" to "http://definitions.ausdx.io/api/relations")


    fun getLinks():List<Map<String,String>>{
        val list = mutableListOf<Map<String,String>>()
        for((description, hrefLink) in hrefLinks){
            list.add(mutableMapOf("rel" to description, "href" to "$hrefLink/${content.domainAcronym}/${content.identifier.split("/").last()}"))
        }
        return list
    }

}