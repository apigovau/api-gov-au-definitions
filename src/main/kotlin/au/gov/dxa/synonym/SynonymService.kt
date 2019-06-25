package au.gov.dxa.synonym

import au.gov.api.config.Config
import com.fasterxml.jackson.databind.ObjectMapper
import khttp.get
import org.springframework.stereotype.Service

class SynonymExpansionResults {
    var expandedQuery: String = ""
    var usedSynonyms: Map<String, List<String>> = mapOf()
}

@Service
class SynonymService {

    val baseRepoUri = Config.get("BaseRepoURI")

    fun getAllSynonyms(): MutableList<List<String>> {
        var response = get(baseRepoUri + "/definitions/synonyms")

        try {
            var output = ObjectMapper().readValue(response.text, List::class.java)
            return output as MutableList<List<String>>
        } catch (e: Exception) {
            e.printStackTrace()
            var output = mutableListOf<List<String>>()
            return output
        }
    }
}