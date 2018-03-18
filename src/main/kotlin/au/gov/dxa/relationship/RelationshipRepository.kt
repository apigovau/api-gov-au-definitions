package au.gov.dxa.relationship

import au.gov.dxa.json.JsonHelper
import au.gov.dxa.json.JsonLd
import com.beust.klaxon.*
import com.fasterxml.jackson.annotation.JsonIgnore
import org.springframework.stereotype.Component


enum class Direction {
    FROM, TO, UNDIRECTED
}

data class Result(@JsonIgnore var meta:Meta, val direction: Direction, val to: String, var toName:String = "")
data class Meta(val type:String, val directed:Boolean, val verbs : Map<Direction, String>)
data class RelationDTO(val from:String, val type:String, val to:String, val direction: Direction)


@Component
class RelationshipRepository(
        private var relationships: MutableMap<String, MutableList<Result>> = mutableMapOf(),
        private var metas: MutableMap<String, Meta> = mutableMapOf()) {


    private fun addResult(from:String, type:String, to:String, direction: Direction){
        if(!metas.containsKey(type)) println("\n\n*****\nNo relationsihp meta for $type. Failing\n*****\n\n")
        val meta = metas[type]!!
        if(!relationships.containsKey(from)){
            relationships[from] = mutableListOf()
        }
        relationships[from]!!.add(Result(meta, direction, to))
    }

    init {
        addMetas()
        addJson()
        addJsonLd()

    }

    private fun addJsonLd(){
        for(jsonld in listOf("agift")){
            val json = JsonLd("/definitions/jsonld/$jsonld.json")
            json.relations.forEach { addResult( it.from, it.type, it.to, Direction.TO)
                addResult( it.to, it.type, it.from, Direction.FROM)}
        }
    }

    private fun addJson() {
        if (relationships.isEmpty()) {
            for (relationshipFile in listOf("ce", "ungrouped")) {
                @Suppress("UNCHECKED_CAST")
                for (jsonRelationship in JsonHelper.parse("/relationships/$relationshipFile.json") as JsonArray<JsonObject>) {
                    val type = jsonRelationship.string("type") ?: ""
                    val relations = jsonRelationship.array<JsonArray<String>>("content")
                    if (relations != null) {
                        for (relationships in relations) {
                            val from = relationships[0]
                            val to = relationships[1]
                            addResult(from, type, to, Direction.TO)
                            addResult(to, type, from, Direction.FROM)
                        }
                    }
                }
            }
        }
    }

    private fun addMetas() {
        if (metas.isEmpty()) {
            @Suppress("UNCHECKED_CAST")
            for (jsonRelationship in JsonHelper.parse("/relationships/meta.json") as JsonArray<JsonObject>) {
                val type = jsonRelationship.string("type") ?: ""
                val directed = jsonRelationship.boolean("directed") ?: false
                val to = jsonRelationship.string("to") ?: ""
                val from = jsonRelationship.string("from") ?: ""
                val verbMap = mapOf(Direction.TO to to, Direction.FROM to from)
                val meta = Meta(type, directed, verbMap)
                metas[type] = meta
            }
        }
    }

    fun getRelationshipFor(identifier: String): MutableMap<String, MutableList<Result>>{
        if(!relationships.containsKey(identifier)) return mutableMapOf()
        val relations = relationships[identifier]

        val results = mutableMapOf<String, MutableList<Result>>()

        for(relation in relations!!){
            if(!results.containsKey(relation.meta.type)) results[relation.meta.type] = mutableListOf()
            results[relation.meta.type]!!.add(relation)
        }
        return results
    }

}
