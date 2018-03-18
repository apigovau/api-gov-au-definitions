package au.gov.dxa.syntax

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service


@Service
class SyntaxService {

    @Autowired
    private lateinit var repository: SyntaxRepository


    fun getSyntax(identifier: String): Syntax?{
       return repository.findOne(identifier)
    }

}