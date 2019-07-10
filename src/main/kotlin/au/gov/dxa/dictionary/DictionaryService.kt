package au.gov.dxa.dictionary

import au.gov.api.config.Config
import au.gov.dxa.controller.Filters
import khttp.get
import org.springframework.stereotype.Service
import java.net.URLEncoder

@Service
class DictionaryService {
    val baseRepoUri = Config.get("BaseRepoURI")

    fun getDictionaryCorrection(query: String, filters: Filters? = null): String {
        val url = baseRepoUri + "definitions/dict?query=${URLEncoder.encode(query, "UTF-8")}" + filterPramConstructor(filters)
        var response = get(url)
        return response.text
    }

    private fun filterPramConstructor(filters: Filters?): String {
        var output = ""
        if (filters == null) {
            return output
        } else {
            for (domain in filters.Domains) {
                output += "&domains=${domain.acronym}"
            }
            return output
        }
    }
}