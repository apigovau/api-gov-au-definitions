package au.gov.dxa.search

class SearchDTO {

    private var query: String? = null

    fun getQuery(): String? {
        return query
    }

    fun setQuery(content: String) {
        this.query = content
    }
}
