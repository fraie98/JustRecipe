package it.unipi.dii.inginf.lsdb.justrecipe.controller;

import it.unipi.dii.inginf.lsdb.justrecipe.persistence.Neo4jDriver;


public class ProfilePageController {
    private Neo4jDriver neo4jDriver;

    /**
     * Function used to pass the Neo4jDriver instance from another controller to this controller
     * @param neo4jDriver
     */
    public void transferNeo4jDriver(Neo4jDriver neo4jDriver) {
        this.neo4jDriver = neo4jDriver;
    }


}
