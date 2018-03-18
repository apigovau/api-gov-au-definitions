package au.gov.dxa.controller

import com.fasterxml.jackson.annotation.JsonIgnore
import org.apache.http.client.utils.URLEncodedUtils
import org.springframework.beans.factory.annotation.Value
import java.net.URI
import java.net.URLEncoder


class PageResult<T>{
    @Value("\${page.default.size}")
    private val defaultPageSize: Int = 20

    var content: List<T> = listOf()
    var numberOfElements: Int = 0
    @JsonIgnore
    var uri: String = ""
    @JsonIgnore
    var pageNumber: Int = 1
    @JsonIgnore
    var pageSize: Int = 0
    var firstPage: Boolean = false
    var lastPage: Boolean = false



    constructor(theContent :List<T>, theUri: String, theTotalResults: Int) {
        content = theContent
        numberOfElements = theTotalResults
        uri = theUri
        this.uri = this.parseUri()
        this.firstPage = isFirstPage()
        this.lastPage = isLastPage()

    }

    private fun parseUri(): String{
        var origParams = mutableMapOf<String,String>()
        val params = URLEncodedUtils.parse(URI(this.uri), "UTF-8")
        val asURI = URI(this.uri)
        var newUri: String
        var oldURIquery = asURI.rawQuery
        if(oldURIquery == null) oldURIquery = ""

        for(pair in params) {
            origParams[pair.name] =  pair.value
        }

        if("page" !in origParams) origParams["page"] = "1"
        if("size" !in origParams) origParams["size"] = defaultPageSize.toString()
        if(origParams["size"]!!.toInt() > 100) origParams["size"] = "100"
        this.pageNumber = origParams["page"]!!.toInt()
        this.pageSize = origParams["size"]!!.toInt()


        newUri = this.uri.replace(oldURIquery, "")
        if(asURI.rawQuery == null) newUri += "?"

        for ((param, value) in origParams) {
            newUri += param + "=" + URLEncoder.encode(value, "UTF-8") + "&"
        }
        if(newUri.endsWith('&')) newUri = newUri.dropLast(1)


        return newUri
    }


    fun isFirstPage():Boolean =
            pageNumber == 1

    fun isLastPage(): Boolean{
        return (content.size < pageSize) or (pageSize * pageNumber == numberOfElements)
    }

    private fun newPageNumberInURL(page: Int, fullPath: Boolean): String{
        var updatedUri = this.uri.replace("page=\\d+".toRegex(), "page=" + page)
        val asURI = URI(updatedUri)
        if(!fullPath) updatedUri = asURI.path + "?" + asURI.rawQuery
        return updatedUri
    }


    fun getTotalPages(): Int{
        return Math.ceil(numberOfElements.div(pageSize.toDouble())).toInt()
    }

    fun theNextPage(fullPath: Boolean = true): String{
        return newPageNumberInURL(pageNumber + 1, fullPath)
    }


    fun thePrevPage(fullPath:Boolean = true): String{
        return newPageNumberInURL(pageNumber - 1, fullPath)
    }

    fun theLastPage(fullPath:Boolean = true): String{
        return newPageNumberInURL(getTotalPages(), fullPath)
    }

    fun theFirstPage(fullPath:Boolean = true): String {
        return newPageNumberInURL(1, fullPath)
    }

    fun getId():Map<String,String>{
        return mutableMapOf("rel" to "self", "href" to this.uri)
    }

    fun getLinks():List<Map<String,String>>{
        var list = mutableListOf<Map<String,String>>()

        list.add(mutableMapOf("rel" to "first", "href" to theFirstPage()))
        list.add(mutableMapOf("rel" to "last", "href" to theLastPage()))
        list.add(mutableMapOf("rel" to "self", "href" to this.uri))
        if(! isFirstPage())   list.add(mutableMapOf("rel" to "prev", "href" to thePrevPage()))
        if(! isLastPage())   list.add(mutableMapOf("rel" to "next", "href" to theNextPage()))

        return list
    }


    fun pagesToTheLeft(): List<String>{
        var pages = mutableListOf<String>()

        if(pageNumber > 1 && getTotalPages() > 1){
            pages.add(theFirstPage(false))
        }

        if(pageNumber > 2){
            pages.add(thePrevPage(false))
        }

        return pages
    }


    fun pagesToTheRight(): List<String>{
        var pages = mutableListOf<String>()

        if(pageNumber < getTotalPages()){
            pages.add(theLastPage(false))
        }

        if(pageNumber < getTotalPages() - 1){
            pages.add(theNextPage(false))
        }


        return pages
    }
}