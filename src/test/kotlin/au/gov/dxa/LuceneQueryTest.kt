package au.gov.dxa

import au.gov.dxa.search.LuceneQueryParser
import org.junit.Assert
import org.junit.Test

class LuceneQueryTest {

    @Test
    fun Test_single_word_query_tests_all_fields() {
        val expected = "name:(address)^2 definition:(address) guidance:(address)"
        Assert.assertEquals(expected, LuceneQueryParser.parse("address","",listOf<String>("name","definition","guidance")))
    }

    @Test
    fun Test_single_word_query_tests_all_fields_with_domain() {
        val expected = """+domain:"sbr" +(name:(address)^2 definition:(address) guidance:(address))"""
        Assert.assertEquals(expected, LuceneQueryParser.parse("address","sbr",listOf<String>("name","definition","guidance")))
    }

    @Test
    fun Test_multi_word_query_tests_all_fields() {
        val expected = "name:(address details)^2 definition:(address details) guidance:(address details)"
        Assert.assertEquals(expected, LuceneQueryParser.parse("address details","",listOf<String>("name","definition","guidance")))
    }

    @Test
    fun Test_multi_word_query_tests_all_fields_with_domain() {
        //+(name:(address details) definition:(address details) guidance:(address details)) +domain:"Financial Statistics"
        val expected = """+domain:"sbr" +(name:(address details)^2 definition:(address details) guidance:(address details))"""
        Assert.assertEquals(expected, LuceneQueryParser.parse("address details","sbr",listOf<String>("name","definition","guidance")))
    }


    @Test
    fun Test_empty_query_tests_all_fields() {
        val expected = ""
        Assert.assertEquals(expected, LuceneQueryParser.parse("","",listOf<String>("name","definition","guidance")))
    }

    @Test
    fun Test_empty_query_tests_all_fields_with_domain() {
        val expected = """+domain:"sbr""""
        Assert.assertEquals(expected, LuceneQueryParser.parse("","sbr",listOf<String>("name","definition","guidance")))
    }


    @Test
    fun Test_multi_word_query_with_synonyms_prepended() {
        val expected = """name:(("australian business number"^1.2 "abn") not quoted)^2 definition:(("australian business number"^1.2 "abn") not quoted) guidance:(("australian business number"^1.2 "abn") not quoted)"""
        Assert.assertEquals(expected, LuceneQueryParser.parse("""("australian business number"^1.2 "abn") not quoted""","",listOf<String>("name","definition","guidance")))
    }


}