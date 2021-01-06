package it.unipi.dii.inginf.lsdb.justrecipe.model;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class SessionTest {

    @Test
    void getInstance() {
        Session instance = Session.getInstance();
        Assertions.assertEquals(instance, Session.getInstance());
    }
}