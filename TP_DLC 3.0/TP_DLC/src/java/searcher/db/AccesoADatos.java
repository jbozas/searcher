/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package searcher.db;

import java.sql.*;


/**
 *
 * @author Chino - Julian
 */
public class AccesoADatos {
    // DATABASE
    
    private static String driver = "org.postgresql.Driver";
    private static String DBUrl = "jdbc:postgresql://localhost:5432/postgres";
    private static String DBUserName = "dlcusr";
    private static String DBPassword = "dlcpwd";
    private static String DBResourceName = "jdbc/pg_postgres";
    private Connection conn = null;
    Statement stmt = null;
    public static final int SINGLECONNECTIONMODE = 1;
    public static final int POOLCONNECTIONMODE = 2;
    private int connectionMode = SINGLECONNECTIONMODE;

    
    public AccesoADatos(){
        super();
    }
    
    public void conectar() throws ClassNotFoundException, SQLException {
        if (this.conn == null) {
                Class.forName(driver);
                this.conn = DriverManager.getConnection(this.DBUrl, this.DBUserName, this.DBPassword);   
        }
    }
    
    public void desconectar() throws SQLException {
        if (this.stmt != null) try {this.stmt.close();}
        catch (Exception e) {}
        this.stmt = null;
        
        if (this.conn != null) try {this.conn.close();}
        catch (Exception e) {}
        this.conn = null;
    }
    
    public void setConnectionMode(int connectionMode) {
        this.connectionMode = connectionMode;
        if (this.connectionMode != POOLCONNECTIONMODE) {
            this.connectionMode = SINGLECONNECTIONMODE;
        }
    }
    
    public int getConnectionMode() {
        return this.connectionMode;
    }    
    
    public void setUrl(String url) {
        this.DBUrl = url;
    }
    
    public String getUrl() {
        return this.DBUrl;
    }
    
    public ResultSet executeQuery(String query) throws Exception {
        this.stmt = this.conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
        return this.stmt.executeQuery(query);
    }
    
    public int executeUpdate(String statement) throws Exception {
        this.stmt = this.conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        return this.stmt.executeUpdate(statement);
    }
    
    
    public void beginTransaction() throws Exception {
        this.conn.setAutoCommit(false);
    }

    public void commit() throws Exception {
        this.conn.commit();
        this.conn.setAutoCommit(true);
    }

    public void rollback() throws Exception {
        this.conn.rollback();
        this.conn.setAutoCommit(true);
    }
    
}
