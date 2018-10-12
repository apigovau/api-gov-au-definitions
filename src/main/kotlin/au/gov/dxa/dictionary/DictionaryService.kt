package au.gov.dxa.dictionary;
import au.gov.dxa.controller.Filters
import au.gov.dxa.definition.Definition
import au.gov.dxa.definition.DefinitionRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
public class DictionaryService {

    @Autowired
    private lateinit var repository: DefinitionRepository

    fun getDictionaryCorrection(query:String, filters:Filters? = null):String
    {
        try {
            if (query.length > 3) {
                if (filters == null) {
                    return runQuery(query, repository.getAllDefinitions())
                } else {
                    if (filters.Domains.count() > 0) {
                        var filterdList: MutableList<Definition> = mutableListOf()
                        var domainStrings: MutableList<String> = mutableListOf()
                        filters.Domains.forEach { domainStrings.add(it.acronym) }
                        domainStrings.forEach { filterdList.addAll(repository.getAllDefinitionsInDomain(it)) }
                        return runQuery(query, filterdList)
                    } else {
                        return runQuery(query, repository.getAllDefinitions())
                    }
                }
            } else {
                return ""
            }
        }
        catch (e:Exception)
        {
            return ""
        }
    }

    fun runQuery(query: String, filterdDef:MutableList<Definition>):String
    {
        var results: MutableList<DistanceResult> = mutableListOf()
        filterdDef.forEach { results.add(DistanceResult(it.name, levenshtein(query,it.name))) }
        results.sortBy { it.distance  }
        return results.first().value
    }

    private fun levenshtein(lhs : CharSequence, rhs : CharSequence) : Int {
        val lhsLength = lhs.length
        val rhsLength = rhs.length

        var cost = Array(lhsLength) { it }
        var newCost = Array(lhsLength) { 0 }

        for (i in 1..rhsLength-1) {
            newCost[0] = i

            for (j in 1..lhsLength-1) {
                val match = if(lhs[j - 1] == rhs[i - 1]) 0 else 1

                val costReplace = cost[j - 1] + match
                val costInsert = cost[j] + 1
                val costDelete = newCost[j - 1] + 1

                newCost[j] = Math.min(Math.min(costInsert, costDelete), costReplace)
            }

            val swap = cost
            cost = newCost
            newCost = swap
        }

        return cost[lhsLength - 1]
    }
}
data class DistanceResult(var value:String, var distance:Int)
