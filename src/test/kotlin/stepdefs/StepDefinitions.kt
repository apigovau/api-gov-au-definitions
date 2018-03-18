package cucumber.runtime.kotlin.test;

import cucumber.api.Scenario
import cucumber.api.java8.En
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.junit.Assert
import org.junit.Assert.assertNotSame
import org.springframework.context.annotation.Configuration


var lastInstance : StepDefinitions? = null

@Configuration
class StepDefinitions : En {

    private val host = "http://localhost:5000/"
    private var lastDoc : Document? = null
    private var lastURL: String? = null

    private fun getLastDoc():Document{
        if(lastDoc == null) Assert.fail("lastDoc was null. Did the previous request work?")
        return lastDoc as Document
    }

    private fun getPage(url:String){
        lastURL = url
        lastDoc = Jsoup.connect(url).get()
    }

    init {
        Before { scenario: Scenario ->
            assertNotSame(this, lastInstance)
            lastInstance = this
        }

        Given("^I am on the main page$") {
            getPage(host)
        }

        Given("^I am on page \"([^\"]*)\"$") { uri: String ->
            getPage(host + uri)
        }


        When("^I click the \"([^\"]*)\" link$") { arg1: String ->
            val link = getLastDoc().select("a[href]").filter { it.text().equals(arg1) }.first()
            val href = link.attr("href")
            if(href.startsWith("http")){
                getPage(href)
            }else {
                getPage(host + href)
            }
        }

        Then("^I will be on the page \"([^\"]*)\" and see \"([^\"]*)\" in a \"([^\"]*)\"$") { url: String, title: String, element: String ->
            Assert.assertTrue(thereIsAnElementWithTitle(element, title))
            Assert.assertTrue("The URL was: $lastURL", lastURL!!.endsWith(url))
        }

        Then("^I will see \"([^\"]*)\" in a \"([^\"]*)\"$") { title: String, element: String ->
            Assert.assertTrue(thereIsAnElementWithTitle(element, title))
        }

        Then("^I won't see \"([^\"]*)\" in a \"([^\"]*)\"$") { title: String, element: String ->
            Assert.assertFalse(thereIsAnElementWithTitle(element, title))
        }

        When("^I search for \"([^\"]*)\"$") { query: String ->
            getPage(lastURL!! + "?query=$query")
        }

    }

    private fun thereIsAnElementWithTitle(element: String, title: String):Boolean{
        val asText = getLastDoc().select(element).filter { it.text().contains( title ) }.isNotEmpty()
        val asVal = getLastDoc().select(element).filter {it.hasAttr("value") && it.attr("value") == title}.isNotEmpty()

        return asText or asVal
    }

}