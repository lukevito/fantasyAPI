package com.gmail.com.fantasy_api;

import org.neo4j.driver.v1.AuthTokens;
import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.GraphDatabase;
import org.neo4j.driver.v1.Session;
import org.neo4j.driver.v1.StatementResult;
import org.neo4j.driver.v1.Transaction;
import org.neo4j.driver.v1.TransactionWork;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static org.neo4j.driver.v1.Values.parameters;

@Service
public class NeoService implements AutoCloseable {

    private final NeoConfiguration neoConfiguration;

    private final Driver driver;

    @Autowired
    public NeoService(NeoConfiguration neoConfiguration) {
        this.neoConfiguration = neoConfiguration;
        driver = GraphDatabase.driver(neoConfiguration.getUrl(), AuthTokens.basic(neoConfiguration.getUser(), neoConfiguration.getPassword()));
    }

    @Override
    public void close() throws Exception {
        driver.close();
    }

    public void printGreeting(final String message) {
        try (Session session = driver.session()) {
            String greeting = session.writeTransaction(new TransactionWork<String>() {
                @Override
                public String execute(Transaction tx) {
                    StatementResult result = tx.run("CREATE (a:Greeting) " +
                                    "SET a.message = $message " +
                                    "RETURN a.message + ', from node ' + id(a)",
                            parameters("message", message));
                    return result.single().get(0).asString();
                }
            });
            System.out.println(greeting);
        }
    }
}
