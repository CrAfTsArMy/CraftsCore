package de.craftsblock.craftscore.api.mysql;

public interface MySQLCallback {

    void connect(AbstractMySQL sql);
    void disconnect(AbstractMySQL sql);

}
