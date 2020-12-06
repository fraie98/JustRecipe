package sample;

import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;

public class Neo4jDriver implements AutoCloseable{
    private Driver driver;
    private String hostname;
    private int port;
    private String username;
    private String password;

    //DA CAMBIARE: PASSARE LA CLASSE PARAMETRI DI CONFIGURAZIONE
    public Neo4jDriver( String hostName, int port, String username, String password )
    {
        this.hostname = hostName;
        this.port = port;
        this.username = username;
        this.password = password;
    }

    public void initConnection()
    {
        driver = GraphDatabase.driver( "neo4j://" + hostname + ":" + port, AuthTokens.basic( username, password ) );
    }

    public void closeConnection ()
    {
        driver.close();
    }

    @Override
    public void close() throws Exception {
        driver.close();
    }
}
