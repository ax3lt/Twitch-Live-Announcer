package it.ax3lt.tla.database;

import it.ax3lt.tla.TwitchLiveAnnouncerPlugin;
import it.ax3lt.tla.config.MessageConfiguration;
import it.ax3lt.tla.core.PluginContext;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class MysqlService {

    private final TwitchLiveAnnouncerPlugin plugin;
    private boolean enabled;
    private String host;
    private int port;
    private String database;
    private String username;
    private String password;
    private String table;

    public MysqlService(TwitchLiveAnnouncerPlugin plugin) {
        this.plugin = plugin;
        this.enabled = PluginContext.get().getConfiguration().getBoolean("mysql.enabled");
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void loadSettings() {
        host = PluginContext.get().getConfiguration().getString("mysql.host");
        port = PluginContext.get().getConfiguration().getInt("mysql.port");
        database = PluginContext.get().getConfiguration().getString("mysql.database");
        username = PluginContext.get().getConfiguration().getString("mysql.username");
        password = PluginContext.get().getConfiguration().getString("mysql.password");
        table = PluginContext.get().getConfiguration().getString("mysql.table");
    }

    public boolean canConnect() {
        try (Connection connection = createConnection()) {
            plugin.getServer().getConsoleSender().sendMessage(MessageConfiguration.getMessage("successConnectingToDatabase"));
            return true;
        } catch (SQLException exception) {
            plugin.getServer().getConsoleSender().sendMessage(
                    MessageConfiguration.getMessage("errorConnectingToDatabase").replace("%error%", exception.getMessage())
            );
            return false;
        }
    }

    public void setupTable() {
        try (Connection connection = createConnection()) {
            connection.createStatement().executeUpdate("CREATE TABLE IF NOT EXISTS " + table + " (channel VARCHAR(255))");
        } catch (SQLException exception) {
            plugin.getServer().getConsoleSender().sendMessage(
                    MessageConfiguration.getMessage("errorCreatingTable").replace("%error%", exception.getMessage())
            );
        }
    }

    public void addChannel(String channel) {
        executeUpdate("INSERT INTO " + table + " (channel) VALUES ('" + channel + "')",
                MessageConfiguration.getMessage("errorAddingChannel"));
    }

    public void removeChannel(String channel) {
        executeUpdate("DELETE FROM " + table + " WHERE channel = '" + channel + "'",
                MessageConfiguration.getMessage("errorRemovingChannel"));
    }

    public void clearTable() {
        executeUpdate("TRUNCATE TABLE " + table,
                MessageConfiguration.getMessage("errorClearingTable"));
    }

    private void executeUpdate(String query, String errorMessage) {
        try (Connection connection = createConnection()) {
            connection.createStatement().executeUpdate(query);
        } catch (SQLException exception) {
            plugin.getServer().getConsoleSender().sendMessage(errorMessage.replace("%error%", exception.getMessage()));
        }
    }

    private Connection createConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database + "?useSSL=false", username, password);
    }
}
