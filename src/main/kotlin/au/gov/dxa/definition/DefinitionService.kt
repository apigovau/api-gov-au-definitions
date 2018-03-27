package au.gov.dxa.definition

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class DefinitionService {

    @Autowired
    private lateinit var repository: DefinitionRepository

    fun getDomainByAcronym(acronym:String): Domain? = repository.getDomainByAcronym(acronym)
    fun getDomainByName(name:String): Domain? = repository.getDomainByName(name)
    fun getDomains():List<Domain> = repository.getDomains()

    fun getDefinition(identifier: String): Definition {
       return repository.findOne(identifier)
    }

    fun getDefinitions(pageNumber: Int, pageSize:Int): List<Definition>{
        return repository.findAll(pageNumber, pageSize)
    }

    fun getDefinitionsHATEOS(pageNumber: Int, pageSize:Int): List<DefinitionHATEOS>{
        val origResults = repository.findAll(pageNumber, pageSize)
        return origResults.map { DefinitionHATEOS(it) }
    }


    fun getDefinitionsForDomain(pageNumber: Int, pageSize:Int, domain: String): List<Definition>{
        return repository.findAllInDomain(pageNumber, pageSize, domain)
    }


    fun search(query: String, domain:String, page: Int, size: Int, raw:Boolean = false, ignoreSynonym: Boolean = false): SearchResults<Definition> {
        return repository.search(query, domain, page, size, raw, ignoreSynonym)
    }

    fun searchHATEOS(query: String, domain:String, page: Int, size: Int, raw:Boolean = false, ignoreSynonym: Boolean = false): SearchResults<DefinitionHATEOS> {
        val origResults = repository.search(query, domain, page, size, raw, ignoreSynonym)
        val results = origResults.results.map { DefinitionHATEOS(it) }
        return SearchResults(results, origResults.howManyResults)
    }

    fun howManyDefinitions(): Int{
        return repository.howManyDefinitions()
    }

    fun howManyDefinitionsInDomain(domain: String): Int{
        return repository.howManyDefinitionsInDomain(domain)
    }
}