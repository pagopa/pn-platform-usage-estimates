package it.pagopa.pn.platform.msclient;

public interface DataVaultEncryptionClient {
    default String encode(String data) { return data; }
    default String encode(String data, String type) { return data; }
    String decode(String data);
}
