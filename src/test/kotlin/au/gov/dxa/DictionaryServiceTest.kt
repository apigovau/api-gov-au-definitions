
import au.gov.dxa.definition.Definition
import au.gov.dxa.dictionary.DictionaryService
import org.junit.Assert
import org.junit.Test



class DictionaryServiceTest{

    val service:DictionaryService = DictionaryService()

    @Test
    fun Test_Dict_correction(){
        Assert.assertEquals("Electronic Contact Facsimile Area Code",service.runQuery("ElectroCotacsimiAreaode",getTestDefList()))
        Assert.assertEquals("Address",service.runQuery("Badress",getTestDefList()))
        Assert.assertEquals("Surname",service.runQuery("Firname",getTestDefList()))
        Assert.assertEquals("Superannuation Fund Details Annual Salary For Contributions Amount",service.runQuery("Superannuation Fund Details Annual Salary For Amount",getTestDefList()))
        Assert.assertEquals("Assets Loans And Receivables Lease Financing Gross Total Amount",service.runQuery("Assets And Receivables Lease Financing Total Amount Gross",getTestDefList()))
        Assert.assertEquals("Higher Education Provider code",service.runQuery("Higher Education code",getTestDefList()))
    }

    fun getTestDefList():MutableList<Definition>
    {
        val output:MutableList<Definition> = mutableListOf()

        output.add(Definition("Electronic Contact Facsimile Area Code","Taxation and revenue collection","","","","",arrayOf<String>(),"",arrayOf<String>(),mapOf(),""))
        output.add(Definition("Address","Core Entity","","","","",arrayOf<String>(),"",arrayOf<String>(),mapOf(),""))
        output.add(Definition("Surname","Financial Insolvency","","","","",arrayOf<String>(),"",arrayOf<String>(),mapOf(),""))
        output.add(Definition("Superannuation Fund Details Annual Salary For Contributions Amount","Super Stream","","","","",arrayOf<String>(),"",arrayOf<String>(),mapOf(),""))
        output.add(Definition("Assets Loans And Receivables Lease Financing Gross Total Amount","Financial Statistics","","","","",arrayOf<String>(),"",arrayOf<String>(),mapOf(),""))
        output.add(Definition("Higher Education Provider code","Education","","","","",arrayOf<String>(),"",arrayOf<String>(),mapOf(),""))

        return output
    }
}
