package au.gov.dxa.json

import com.beust.klaxon.Parser

class JsonHelper {

    companion object {
        @JvmStatic fun parse(name: String): Any? {
            val cls = Parser::class.java
            return cls.getResourceAsStream(name)?.let { inputStream ->
                return Parser().parse(inputStream)
            }
        }

    }
}