package it.pagopa.pn.platform.dao;

public class DAOException extends RuntimeException{

    private DaoName daoName;

    public enum DaoName {

        CSVDAO,
        ZIPDAO;

    }

    public DAOException(DaoName daoName, String message){
        super(message);
        this.daoName = daoName;
    }

}
