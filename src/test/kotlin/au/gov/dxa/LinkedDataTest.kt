/*import au.gov.dxa.json.JsonHelper
import au.gov.dxa.json.JsonLd
import com.beust.klaxon.JsonArray
import com.beust.klaxon.JsonObject
import org.junit.Assert
import org.junit.Test

class LinkedDataTest{
    @Test
    fun can_get_test_data(){
        val test_data_path = "/agift.json"
        @Suppress("UNCHECKED_CAST")
        val test_data = JsonHelper.parse(test_data_path) as JsonArray<JsonObject>
        Assert.assertNotNull(test_data)
    }

    @Test
    fun canGetDomainInfo(){
        val test_data_path = "/agift.json"
        val jsonld = JsonLd(test_data_path)
        val domain = jsonld.domain
        Assert.assertEquals("Australian Governments' Interactive Functions Thesaurus (AGIFT)", domain.name)
        Assert.assertEquals("agift", domain.acronym)
        Assert.assertEquals("2016-12-02T04:49:57Z", domain.version)

    }
}
*/