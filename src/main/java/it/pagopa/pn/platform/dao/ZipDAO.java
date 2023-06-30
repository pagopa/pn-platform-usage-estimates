package it.pagopa.pn.platform.dao;

public interface ZipDAO {


    void zipFiles(String directoryPath);

    byte[] getZipFile(String directoryPath);

}
