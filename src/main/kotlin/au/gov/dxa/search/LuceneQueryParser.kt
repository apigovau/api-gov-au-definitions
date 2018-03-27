package au.gov.dxa.search

class LuceneQueryParser {
    companion object {

        fun parse(query:String, domain:String = "",  fields:List<String> = listOf("name","definition","guidance","identifier")):String{
            var output = ""

            if(domain != "") output =  """$output+domain:($domain)"""

            if(query == "") return output

            if(domain != "") output = "$output +("

            for(field in fields){
                output = "$output$field:($query)"
                if(field == "name"){
                    output = "$output^2"
                }
                if(field == "identifier"){
                    output = "$output^2"
                }
                output = "$output "
            }
            output = output.trim()
            if(domain != "") output = "$output)"
            return output
        }
    }
}