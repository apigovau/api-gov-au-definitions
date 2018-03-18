package au.gov.dxa.definition


import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import java.sql.Connection
import java.sql.SQLException
import javax.sql.DataSource

@Component
@RestController
class QueryLogger {
    @Value("\${spring.datasource.url}")
    private var dbUrl: String? = null

    @Autowired
    private lateinit var dataSource: DataSource

    private val log = LoggerFactory.getLogger(this.javaClass)

    fun logQuery(query: String, expanded: String, results: Int) {
        var connection: Connection? = null
        try {
            connection = dataSource.connection
            val stmt = connection.createStatement()
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS queries (query text, expanded text, results integer, time timestamp)")
            val insertStmt = connection.prepareStatement("INSERT INTO queries VALUES( ?, ?, ?, now())")
            insertStmt.setString(1, query)
            insertStmt.setString(2, expanded)
            insertStmt.setInt(3, results)
            insertStmt.execute()
            log.info("Search: '$query' -> '$expanded' = $results results")
        } catch (e: Exception) {
            log.error("Something went wrong saving the query to the database. " + e.message)
        } finally {
            if(connection != null) connection.close()
        }

    }

    data class LoggedQuery(val query:String, val expanded: String, val results: Int, val time:String)
    data class QueryLog(val count:Int, val queries:List<LoggedQuery>)

    @GetMapping("/api/queries")
    fun getQueries(): QueryLog {
        var connection: Connection? = null
        try {
            connection = dataSource.connection
            val stmt = connection.createStatement()
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS queries (query text, expanded text, results integer, time timestamp)")
            val rs = stmt.executeQuery("SELECT * from queries")

            val output = mutableListOf<LoggedQuery>()
            while (rs.next()) {
                output.add(LoggedQuery(rs.getString("query"), rs.getString("expanded"), rs.getInt("results"),rs.getString("time")))
            }
            return QueryLog(output.size, output.toList())
        } catch (e: Exception) {
            log.error("Something went wrong getting the queries from the database. " + e.message)
        }
        finally {
            if(connection != null) connection.close()
        }

        return QueryLog(0, listOf())
    }


    @Bean
    @Throws(SQLException::class)
    fun dataSource(): DataSource? {
        if (dbUrl?.isEmpty() ?: true) {
            return HikariDataSource()
        } else {
            val config = HikariConfig()
            config.jdbcUrl = dbUrl
            try {
                return HikariDataSource(config)
            }catch(e:Exception){
                return null
            }
        }
    }
}