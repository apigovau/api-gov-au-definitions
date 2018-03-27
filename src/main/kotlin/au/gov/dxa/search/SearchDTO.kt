package au.gov.dxa.search


class SearchDTO {

    private var query: String? = null
    private var ignoreSynonym: String? = null
    private var domain: String? = null
    private var removeTag: String? = null

    fun getQuery(): String? {
        return query
    }

    fun setQuery(content: String) {
        this.query = content
    }

    fun getIgnoreSynonym(ignoreRemovedTags : Boolean = true): String? {
        var returnstring = ignoreSynonym
        if (ignoreRemovedTags) {
            return returnstring
        }
        else
        {
            if (getRemoveTagList().contains("ignoreS")){
                returnstring = "0"
            } else {
                returnstring = ignoreSynonym
            }
            return returnstring
        }
    }

    fun setIgnoreSynonym(content: String) {
        this.ignoreSynonym = content
    }

    fun getDomain(): String? {
        return domain
    }
    fun getDomainList(ignoreRemovedTags : Boolean = true): List<String> {
        var s:String = domain ?: ""

        if (ignoreRemovedTags) {
            return s.split(",")
        }
        else
        {
            return s.split(",").minus(getRemoveTagList())
        }

    }
    fun getDomainSearchQuery():String{
        var returnString: String = ""
        val dom = getDomainList(false)
        dom.forEach { returnString = "$returnString $it" }
        return returnString.trim()

    }
    fun setDomain(content: String) {
        this.domain = content
    }

    fun getRemoveTag(): String? {
        return removeTag
    }
    fun getRemoveTagList(): List<String> {
        var s:String = removeTag ?: ""
        return s.split(",")

    }
    fun setRemoveTag(content: String) {
        this.removeTag = content
    }
}
