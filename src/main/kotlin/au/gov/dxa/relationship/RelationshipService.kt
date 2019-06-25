package au.gov.dxa.relationship

import au.gov.api.config.Config
import com.fasterxml.jackson.databind.ObjectMapper
import khttp.get
import org.springframework.stereotype.Service
import java.util.*

enum class Direction {
    FROM, TO, UNDIRECTED
}

data class Result(var meta: Meta?, val direction: Direction, val to: String, var toName: String = "")
data class Meta(val type: String, val directed: Boolean, val verbs: Map<Direction, String>)

@Service
class RelationshipService {


    val baseRepoUri = Config.get("BaseRepoURI")

    fun getRelations(identifier: String): Map<String, List<Result>> {
        var response = get(baseRepoUri + "definitions/relationships?id=$identifier")
        var jsonHashMao = ObjectMapper().readValue(response.text, LinkedHashMap::class.java)
        var output: MutableMap<String, List<Result>> = mutableMapOf()
        for ((key, value) in jsonHashMao) {
            var res: MutableList<Result> = mutableListOf()
            for (resultEntry in (value as Iterable<LinkedHashMap<String, String>>)) {
                res.add(Result(getMeta(key as String), Direction.valueOf(resultEntry.getValue("direction")), resultEntry.getValue("to"), resultEntry.getValue("toName")))
            }
            output.set(key as String, res)
        }
        return output
    }

    fun getMeta(relationType: String): Meta {
        var response = get(baseRepoUri + "definitions/relationships/meta?relationType=$relationType")
        var jsonHashMap = ObjectMapper().readValue(response.text, LinkedHashMap::class.java)
        var ver: MutableMap<Direction, String> = mutableMapOf()
        var verbs = (jsonHashMap["verbs"] as LinkedHashMap<String, String>)
        for (resultEntry in verbs.keys) {
            ver.set(Direction.valueOf(resultEntry), verbs[resultEntry]!!)
        }
        return Meta(jsonHashMap["type"] as String, (jsonHashMap["type"] as String).toBoolean(), ver.toMap())
    }

}