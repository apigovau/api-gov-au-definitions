/*

package au.gov.dxa

import au.gov.dxa.relationship.Direction
import au.gov.dxa.relationship.Meta
import au.gov.dxa.relationship.RelationshipRepository
import au.gov.dxa.relationship.Result
import org.junit.Assert
import org.junit.Test

class RelationshipServiceTest {


    private var meta = mutableMapOf(
            "skos:member" to Meta("skos:member",true, mapOf(Direction.TO to "is member of", Direction.FROM to  "has member")),
            "rdfs:seeAlso" to Meta("rdfs:seeAlso", false,mapOf(Direction.UNDIRECTED to "see also"))
    )


    private var relationships = mutableMapOf(
        "http://api.gov.au/definition/other/de17" to mutableListOf(
                Result(meta["rdfs:seeAlso"]!!, Direction.UNDIRECTED,"Something1","something"),
                Result(meta["rdfs:seeAlso"]!!, Direction.FROM,"Something2","something"),
                Result(meta["rdfs:seeAlso"]!!, Direction.FROM,"Something3","something"),
                Result(meta["skos:member"]!!, Direction.TO,"Something5","something")
        )
    )

    private var service = RelationshipRepository(relationships)


    @Test
    fun test_can_get_relationships() {
        val input = "http://api.gov.au/definition/other/de17"
        val result = service.getRelationshipFor(input)
        Assert.assertEquals(2, result.size)
        Assert.assertTrue(result.containsKey("rdfs:seeAlso"))
        val seeAlso = result["rdfs:seeAlso"]
        Assert.assertEquals(3, seeAlso!!.size)
        Assert.assertEquals(Direction.UNDIRECTED, seeAlso[0].direction)

        Assert.assertTrue(result.containsKey("skos:member"))
        val member = result["skos:member"]
        Assert.assertEquals(1, member!!.size)
        Assert.assertEquals(Direction.TO, member[0].direction)
        Assert.assertEquals("is member of", member[0].meta.verbs[member[0].direction])
    }


}
*/