package au.gov.dxa.relationship

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service


@Service
class RelationshipService {

    @Autowired
    lateinit var repository: RelationshipRepository


    fun getRelations(identifier: String):Map<String,List<Result>>{
       return repository.getRelationshipFor(identifier)
    }

}