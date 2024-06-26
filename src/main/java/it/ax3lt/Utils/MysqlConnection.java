package it.ax3lt.Utils;

// mysql:
//  enabled: false
//  host: 'localhost'
//  port: 3306
//  database: 'twitch'
//  username: 'root'
//  password: ''
//  table: 'twitch_channels'

import it.ax3lt.Main.TLA;
import it.ax3lt.Utils.Configs.MessagesConfigUtils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MysqlConnection {
    public static boolean enabled;
    public static String host;
    public static int port;
    public static String database;
    public static String username;
    public static String password;
    public static String table;
    public static Connection conn = null;

    public static void load() {
        enabled = TLA.getInstance().getConfig().getBoolean("mysql.enabled");
        host = TLA.getInstance().getConfig().getString("mysql.host");
        port = TLA.getInstance().getConfig().getInt("mysql.port");
        database = TLA.getInstance().getConfig().getString("mysql.database");
        username = TLA.getInstance().getConfig().getString("mysql.username");
        password = TLA.getInstance().getConfig().getString("mysql.password");
        table = TLA.getInstance().getConfig().getString("mysql.table");
    }

    // errorConnectingToDatabase: '%prefix% &8» &cError connecting to the database: %error%'

    public static void connect() {
        if (enabled) {
            // Connect to MySQL
            try {
                conn = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database + "?useSSL=false", username, password);
                TLA.getInstance().getServer().getConsoleSender().sendMessage(MessagesConfigUtils.getString("successConnectingToDatabase"));
            } catch (SQLException e) {
                TLA.getInstance().getServer().getConsoleSender().sendMessage(MessagesConfigUtils.getString("errorConnectingToDatabase").replace("%error%", e.getMessage()));
            } finally {
                try {
                    if (conn != null) {
                        conn.close();
                    }
                } catch (SQLException e) {
                    TLA.getInstance().getServer().getConsoleSender().sendMessage(MessagesConfigUtils.getString("errorClosingDatabaseConnection").replace("%error%", e.getMessage()));
                }
            }
        }
    }
}
