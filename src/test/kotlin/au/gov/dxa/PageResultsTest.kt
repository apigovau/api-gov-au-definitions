package au.gov.dxa

import au.gov.dxa.controller.PageResult
import org.junit.Assert
import org.junit.Test

class PageResultsTest {

    var content: MutableList<Int> = mutableListOf(0,1,2,3,4,5,6,7,8,9)
    val defaultPage: String = "http://api.gov.au/browse"
    val defaultPageParams: String = "http://api.gov.au/browse?page=1&size=20"
    val firstPage: String = "http://api.gov.au/browse?page=1&size=3"
    val secondPage: String = "http://api.gov.au/browse?page=2&size=3"
    val thirdPage: String = "http://api.gov.au/browse?page=3&size=3"
    val lastPage: String = "http://api.gov.au/browse?page=4&size=3"

    @Test
    fun PageResults_know_theyre_the_first_page() {
        var result = PageResult<Int>(mutableListOf(0, 1, 2), firstPage, content.size)
        Assert.assertTrue(result.isFirstPage())
        Assert.assertFalse(result.isLastPage())
    }

    @Test
    fun PageResults_know_theyre_the_last_page() {
        var result = PageResult<Int>(mutableListOf(0), lastPage, content.size)
        Assert.assertFalse(result.isFirstPage())
        Assert.assertTrue(result.isLastPage())
    }

    @Test
    fun PageResults_can_get_page_size_and_number() {
        var result = PageResult<Int>(mutableListOf(0, 1, 2), firstPage, content.size)
        Assert.assertEquals(firstPage, result.uri)
        Assert.assertEquals(1, result.pageNumber)
        Assert.assertEquals(3, result.pageSize)
    }

    @Test
    fun PageResults_can_get_nextpage_uri() {
        Assert.assertEquals(secondPage, PageResult<Int>(mutableListOf(0, 1, 2), firstPage, content.size).theNextPage())
        Assert.assertEquals(thirdPage, PageResult<Int>(mutableListOf(0, 1, 2), secondPage, content.size).theNextPage())
        Assert.assertEquals(lastPage, PageResult<Int>(mutableListOf(0, 1, 2), thirdPage, content.size).theNextPage())
        Assert.assertEquals("/browse?page=2&size=3", PageResult<Int>(mutableListOf(0, 1, 2), firstPage, content.size).theNextPage(false))
        Assert.assertEquals("/browse?page=3&size=3", PageResult<Int>(mutableListOf(0, 1, 2), secondPage, content.size).theNextPage(false))
        Assert.assertEquals("/browse?page=4&size=3", PageResult<Int>(mutableListOf(0, 1, 2), thirdPage, content.size).theNextPage(false))

    }

    @Test
    fun PageResults_can_get_prevpage_uri() {
        Assert.assertEquals(firstPage, PageResult<Int>(mutableListOf(0, 1, 2), secondPage, content.size).thePrevPage())
        Assert.assertEquals(secondPage, PageResult<Int>(mutableListOf(0, 1, 2), thirdPage, content.size).thePrevPage())
        Assert.assertEquals(thirdPage, PageResult<Int>(mutableListOf(0, 1, 2), lastPage, content.size).thePrevPage())
        Assert.assertEquals("/browse?page=1&size=3", PageResult<Int>(mutableListOf(0, 1, 2), secondPage, content.size).thePrevPage(false))
        Assert.assertEquals("/browse?page=2&size=3", PageResult<Int>(mutableListOf(0, 1, 2), thirdPage, content.size).thePrevPage(false))
        Assert.assertEquals("/browse?page=3&size=3", PageResult<Int>(mutableListOf(0, 1, 2), lastPage, content.size).thePrevPage(false))
    }


    @Test
    fun PageResults_can_get_lastpage_uri() {
        Assert.assertEquals(lastPage, PageResult<Int>(mutableListOf(0, 1, 2), firstPage, content.size).theLastPage())
        Assert.assertEquals(lastPage, PageResult<Int>(mutableListOf(0, 1, 2), secondPage, content.size).theLastPage())
        Assert.assertEquals(lastPage, PageResult<Int>(mutableListOf(0, 1, 2), thirdPage, content.size).theLastPage())
        Assert.assertEquals(lastPage, PageResult<Int>(mutableListOf(0, 1, 2), lastPage, content.size).theLastPage())
        Assert.assertEquals(316, PageResult<Int>(mutableListOf(0, 1, 2), defaultPageParams, 6317).getTotalPages())

        Assert.assertEquals("/browse?page=4&size=3", PageResult<Int>(mutableListOf(0, 1, 2), firstPage, content.size).theLastPage(false))
        Assert.assertEquals("/browse?page=4&size=3", PageResult<Int>(mutableListOf(0, 1, 2), secondPage, content.size).theLastPage(false))
        Assert.assertEquals("/browse?page=4&size=3", PageResult<Int>(mutableListOf(0, 1, 2), thirdPage, content.size).theLastPage(false))
        Assert.assertEquals("/browse?page=4&size=3", PageResult<Int>(mutableListOf(0, 1, 2), lastPage, content.size).theLastPage(false))
            }

    @Test
    fun PageResults_can_get_firstpage_uri() {
        Assert.assertEquals(firstPage, PageResult<Int>(mutableListOf(0, 1, 2), firstPage, content.size).theFirstPage())
        Assert.assertEquals(firstPage, PageResult<Int>(mutableListOf(0, 1, 2), secondPage, content.size).theFirstPage())
        Assert.assertEquals(firstPage, PageResult<Int>(mutableListOf(0, 1, 2), thirdPage, content.size).theFirstPage())
        Assert.assertEquals(firstPage, PageResult<Int>(mutableListOf(0, 1, 2), lastPage, content.size).theFirstPage())

        Assert.assertEquals("/browse?page=1&size=3", PageResult<Int>(mutableListOf(0, 1, 2), firstPage, content.size).theFirstPage(false))
        Assert.assertEquals("/browse?page=1&size=3", PageResult<Int>(mutableListOf(0, 1, 2), secondPage, content.size).theFirstPage(false))
        Assert.assertEquals("/browse?page=1&size=3", PageResult<Int>(mutableListOf(0, 1, 2), thirdPage, content.size).theFirstPage(false))
        Assert.assertEquals("/browse?page=1&size=3", PageResult<Int>(mutableListOf(0, 1, 2), lastPage, content.size).theFirstPage(false))
    }

    @Test
    fun PageResults_can_add_paging_to_uri(){
        Assert.assertEquals(defaultPageParams, PageResult<Int>(mutableListOf(0, 1, 2), defaultPage, content.size).uri)
    }


    @Test
    fun PageResults_can_get_current_page_number() {
        Assert.assertEquals(1, PageResult<Int>(mutableListOf(0, 1, 2), firstPage, content.size).pageNumber)
        Assert.assertEquals(2, PageResult<Int>(mutableListOf(0, 1, 2), secondPage, content.size).pageNumber)
        Assert.assertEquals(3, PageResult<Int>(mutableListOf(0, 1, 2), thirdPage, content.size).pageNumber)
        Assert.assertEquals(4, PageResult<Int>(mutableListOf(0, 1, 2), lastPage, content.size).pageNumber)
    }

    @Test
    fun PageResults_can_get_adjacent_pages() {
        Assert.assertEquals(listOf<String>(), PageResult<Int>(mutableListOf(0, 1, 2), firstPage, content.size).pagesToTheLeft())
        Assert.assertEquals(listOf("/browse?page=1&size=3"), PageResult<Int>(mutableListOf(0, 1, 2), secondPage, content.size).pagesToTheLeft())
        Assert.assertEquals(listOf("/browse?page=1&size=3", "/browse?page=2&size=3"), PageResult<Int>(mutableListOf(0, 1, 2), thirdPage, content.size).pagesToTheLeft())
        Assert.assertEquals(listOf("/browse?page=1&size=3","/browse?page=3&size=3"), PageResult<Int>(mutableListOf(0, 1, 2), lastPage, content.size).pagesToTheLeft())

        Assert.assertEquals(listOf("/browse?page=4&size=3","/browse?page=2&size=3"), PageResult<Int>(mutableListOf(0, 1, 2), firstPage, content.size).pagesToTheRight())
        Assert.assertEquals(listOf("/browse?page=4&size=3","/browse?page=3&size=3"), PageResult<Int>(mutableListOf(0, 1, 2), secondPage, content.size).pagesToTheRight())
        Assert.assertEquals(listOf("/browse?page=4&size=3"), PageResult<Int>(mutableListOf(0, 1, 2), thirdPage, content.size).pagesToTheRight())
        Assert.assertEquals(listOf<String>(), PageResult<Int>(mutableListOf(0, 1, 2), lastPage, content.size).pagesToTheRight())
    }


    @Test
    fun PageResults_dont_mess_with_other_params(){
        val request = "http://localhost:5000/api/search?query=Name+and+date&page=1&size=20"
        var result = PageResult<Int>(mutableListOf(0, 1, 2), request, content.size)
        Assert.assertEquals(request, result.uri)
        Assert.assertEquals(1, result.pageNumber)
        Assert.assertEquals(20, result.pageSize)

    }


    @Test
    fun PageResults_dont_mess_with_other_params_with_duplicates(){
        val request = "http://localhost:5000/api/search?domain=trc&domain=edu&query=Name+and+date&page=1&size=20"
        var result = PageResult<Int>(mutableListOf(0, 1, 2), request, content.size)
        Assert.assertEquals(request, result.uri)
        Assert.assertEquals(1, result.pageNumber)
        Assert.assertEquals(20, result.pageSize)

    }

}
