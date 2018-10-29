package au.gov.dxa

import au.gov.dxa.controller.URLHelper
import org.junit.Assert
import org.junit.Test

class URLHelperTest {


    @Test
    fun Test_can_switch_servers() {
        val to = "http://localhost:5000/definition/other/de18"
        val from = "http://api.gov.au/definition/other/de17"
        val expected = "http://localhost:5000/definition/other/de17"
        val results = URLHelper()._convertURL(to, from)
        Assert.assertEquals(expected, results)
    }

}
