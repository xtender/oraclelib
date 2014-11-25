package org.orasql.oraclelib;

import oracle.jdbc.OracleConnection;
import oracle.jdbc.OracleStatement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;
import java.sql.SQLException;

@SuppressWarnings("unused")
public class QueryExecutor {
    private static final Logger logger = LoggerFactory.getLogger(QueryExecutor.class);

    private OracleConnection    connection;
    private String              query;
    private OracleStatement     statement;
    private ResultSet           resultSet;

    public QueryExecutor(OracleConnection connection){
        this.connection = connection;
    }

    public QueryExecutor(OracleConnection connection, String query) {
        this(connection);
        this.setQuery(query);
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public OracleStatement getStatement() throws SQLException {
        if(this.statement == null){
            this.statement = (OracleStatement) connection.createStatement();
            initStatementParams();
        }
        return this.statement;
    }

    public void initStatementParams() throws SQLException {
        statement.setLobPrefetchSize(Props.getIntValue("oracle.lob_prefetch_size"));
        statement.setRowPrefetch(    Props.getIntValue("oracle.row_prefetch"));
        statement.setFetchSize(      Props.getIntValue("oracle.fetch_size"));
    }

    public void executeAndFetch(String query) throws SQLException {
        setQuery(query);
        executeAndFetch();
    }

    public void executeAndFetch() throws SQLException {
        execute();
        ResultSetFormatter resultSetFormatter = new ResultSetFormatter(this.resultSet);
        resultSetFormatter.printRS();
        resultSet.close();
    }

    public void execute() throws SQLException {
        this.resultSet = this.getStatement().executeQuery(this.query);
    }

    public String getQuery() {
        return query;
    }

    public ResultSet getResultSet() {

        return resultSet;
    }

}
