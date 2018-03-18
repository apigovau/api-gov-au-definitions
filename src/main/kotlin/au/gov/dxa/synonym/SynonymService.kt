package au.gov.dxa.synonym

import au.gov.dxa.json.JsonHelper
import com.beust.klaxon.JsonArray
import org.springframework.stereotype.Component

data class SynonymExpansionResults(val expandedQuery: String, val usedSynonyms: Map<String, List<String>>)

@Component
class SynonymService(private var synonyms : MutableMap<String, List<String>> = mutableMapOf()) {
    var originalSynonymWordWeighting = "^1.2"

    var origSynonyms = mutableListOf<List<String>>()



    init {

        if(synonyms.isEmpty()) {

            @Suppress("UNCHECKED_CAST")
            for (synonymSet in JsonHelper.parse("/synonyms.json") as JsonArray<JsonArray<String>>) {

                val synonymList = synonymSet.map{it}
                origSynonyms.add(synonymList)
                for (synonymWord in synonymSet) {
                    if (synonymWord in synonyms) {
                        println("Duplicate synonym found: $synonymWord\n${synonyms[synonymWord]}\n$synonymSet")
                        System.exit(1)
                    }
                    synonyms[synonymWord] = synonymList.toList()
                }
            }
        }
    }


    fun expand(input:String): SynonymExpansionResults {
        var output = ""

        val usedSynonyms = mutableMapOf<String, List<String>>()

        val tokens = getTokens(input)
        for(token in tokens){
            var workingToken = token
            val hadModifier = workingToken.startsWith("-") || workingToken.startsWith("+")
            var modifier = ""
            if(hadModifier){
                modifier = workingToken.substring(0,1)
                workingToken = workingToken.substring(1)
                //println("Had modifier of '${modifier}'. Now is : ${workingToken}")
            }
            val wasQuoted = workingToken.startsWith('"') && workingToken.endsWith('"')
            if(wasQuoted) workingToken = workingToken.removePrefix("\"").removeSuffix("\"")
            //println("workingToken: ${workingToken}")
            if(workingToken in synonyms){
                var expandedSynonyms = synonyms[workingToken]!!.map{ quoteIfNotMainSynonymAndHasSpaces(workingToken, it) }.joinToString(" ")

                val synonymAlternatives = synonyms[workingToken]!!.filter { it != workingToken }
                usedSynonyms[workingToken] = synonymAlternatives

                //println(expandedSynonyms)
                var origTokenWithWeight = token + originalSynonymWordWeighting
                if(wasQuoted) origTokenWithWeight = "\"$workingToken\"$originalSynonymWordWeighting"
                expandedSynonyms = expandedSynonyms.replace( workingToken , origTokenWithWeight)
                output = "$output$modifier($expandedSynonyms)"
            }
            else output +=  token
            output +=  " "
        }
        output = output.removeSuffix(" ")

        return SynonymExpansionResults(output, usedSynonyms.toMap())
    }

    private fun quoteIfNotMainSynonymAndHasSpaces(mainToken:String, token:String): String{
        if(token.contains(" ") && token!= mainToken) return "\"$token\""
        return token
    }

    private fun getTokens(input: String): List<String> {
        val theInput = quoteSynonyms(input)
        var workingInput = theInput
        val quoteMatcher = Regex("([\"'])(?:(?=(\\\\?))\\2.)*?\\1")
        for (match in quoteMatcher.findAll(theInput)) {
            val withoutSpaces = match.value.replace(" ", "~~")
            workingInput = workingInput.replace(match.value, withoutSpaces)
        }
        val workingTokens = workingInput.split(" ")
        return workingTokens.map {it.replace("~~"," ")}

    }

    private fun quoteSynonyms(input:String):String{
        var output = ""
        for(synonym in synonyms.keys.sortedByDescending { it.length }){
            val regex = Regex("\\b$synonym\\b")
            if(input.contains(regex) && synonym.contains(" ") && !input.contains("\"$synonym\"")) output += " \"$synonym\""
        }
        output = input + " " + output.replace("\"\"","\"").trim()
        return output.trim()
    }
}