package au.gov.dxa.syntax

import au.gov.api.config.Config
import com.fasterxml.jackson.databind.ObjectMapper
import khttp.get
import org.springframework.stereotype.Service

class Syntax {
    var identifier: String = ""
    var syntaxes: Map<String, Map<String, String>> = mapOf()
}

@Service
class SyntaxService {
    val baseRepoUri = Config.get("BaseRepoURI")

    fun getSyntax(identifier: String): Syntax? {
        var response = get(baseRepoUri + "definitions/syntax?id=$identifier")

        try {
            return ObjectMapper().readValue(response.text, Syntax::class.java)
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }
}