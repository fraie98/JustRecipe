package it.unipi.dii.inginf.lsdb.justrecipe.persistence;

/**
 * Interface with the method that every database driver must implement
 */
public interface DatabaseDriver {
    public void initConnection();
    public void closeConnection();
}
