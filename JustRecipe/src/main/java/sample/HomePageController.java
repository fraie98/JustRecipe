package sample;

public class HomePageController{
    private Neo4jDriver neo4jDriver;

    /**
     * Function used to pass the Neo4jDriver instance from another controller to this controller
     * @param neo4jDriver
     */
    public void transferNeo4jDriver(Neo4jDriver neo4jDriver) {
        this.neo4jDriver = neo4jDriver;
        /*try {
            this.neo4jDriver.closeConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }*/
    }

    public void initialize()
    {

    }
}
