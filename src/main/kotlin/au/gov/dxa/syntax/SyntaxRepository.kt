package au.gov.dxa.syntax

import au.gov.dxa.json.JsonHelper
import com.beust.klaxon.JsonArray
import com.beust.klaxon.JsonObject
import com.beust.klaxon.obj
import com.beust.klaxon.string
import com.fasterxml.jackson.annotation.JsonIgnore
import org.springframework.stereotype.Component
import kotlin.collections.Map
import kotlin.collections.MutableMap
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.iterator
import kotlin.collections.mutableMapOf
import kotlin.collections.set


data class Syntax(@JsonIgnore val identifier: String, val syntaxes: Map<String, Map<String, String>>)

@Component
class SyntaxRepository {

    private var syntaxData: MutableMap<String, Syntax> = mutableMapOf()


    init {

        @Suppress("UNCHECKED_CAST")
        for ( jsonDefinition in JsonHelper.parse("/syntaxes.json") as JsonArray<JsonObject>){
            val identifier = jsonDefinition.string("identifier")?: ""
            val jsonSyntaxes = jsonDefinition.obj("syntax")
            val syntaxesMap = mutableMapOf<String, Map<String, String>>()
            if(jsonSyntaxes != null){
                for((syntaxName, syntaxMap) in jsonSyntaxes.iterator()){
                    val syntaxOptions = mutableMapOf<String,String>()
                    if(syntaxMap != null && syntaxMap is MutableMap<*, *>){
                        for((syntaxProperty, syntaxValue) in syntaxMap.iterator()){
                            if(syntaxProperty is String && syntaxValue is String) {
                                syntaxOptions[syntaxProperty] = syntaxValue
                            }
                        }
                    }
                    syntaxesMap[syntaxName] = syntaxOptions
                }
            }

            val newSyntax = Syntax(identifier, syntaxesMap)
            syntaxData[identifier] = newSyntax
        }
    }

    fun findOne(id: String): Syntax? = syntaxData[id]


}
