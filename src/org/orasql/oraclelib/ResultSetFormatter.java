package org.orasql.oraclelib;


import org.apache.commons.lang3.StringUtils;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ResultSetFormatter {

    private ResultSet rs;
    private ResultSetMetaData metadata;

    private Map<Integer,String> colNames = new HashMap<Integer,String>();
    private Map<String,Integer> colIndexes = new HashMap<String,Integer>();

    public ResultSetFormatter(ResultSet rs) throws SQLException {
        this.rs = rs;
        this.metadata = rs.getMetaData();
        for(int i=1; i<= metadata.getColumnCount(); i++){
            colNames.put(i, metadata.getColumnName(i));
            colIndexes.put(metadata.getColumnName(i), i);
        }
    }

    public ResultSetFormatter(ResultSet rs, String colDelimiter) throws SQLException {
        this(rs);
    }

    public static void debug(String msg){
        if(Props.getInstance().getValue("DEBUG").equals(1))
            System.err.println("DEBUG: " + msg);
    }


    public static String rpad(String s, int n, String c) {
        return StringUtils.leftPad(s, n, c);
    }

    public static String rpad(String s, int n) {
        return StringUtils.leftPad(s, n, " ");
    }

    public static String lpad(String s, int n, String c) {
        return StringUtils.rightPad(s, n, c);
    }
    public static String lpad(String s, int n) {
        return StringUtils.rightPad(s, n, " ");
    }

    private int getColFormatLength(int i) throws SQLException {
        int colLength = Math.min(
                Math.max( this.metadata.getColumnDisplaySize(i)
                        , this.metadata.getColumnName(i).length()
                ),
                Props.getInstance().getIntValue("format.max_column_length", Integer.MAX_VALUE)
        );
        switch(metadata.getColumnTypeName(i)){
            case "DATE":
                colLength = Math.max(colLength,Props.getValue("format.date_format","").length());
        }
        return colLength;
    }

    public String formatHeader() throws SQLException {
        String res="";
        for (int i=1; i <= metadata.getColumnCount(); i++) {
            res += rpad(metadata.getColumnName(i), getColFormatLength(i) , " ") + Props.getValue("format.set.colseparator"," ");
        }
        return res;
    }

    public String br() throws SQLException {
        String res="";
        for (int i=1; i <= metadata.getColumnCount(); i++) {
            res += StringUtils.repeat(Props.getValue("format.set.headseparator","-"), getColFormatLength(i)) + Props.getValue("format.set.colseparator"," ");
        }
        return res;
    }

    public String formatNumber(int colN) throws SQLException {
        //int displaySize = metadata.getColumnDisplaySize(colN);
        int displaySize = getColFormatLength(colN);
        int precision   = metadata.getPrecision(colN);
        int scale       = metadata.getScale(colN);
        debug("colN = " + colN + ", displaySize=" + displaySize + ", precision = " + precision + ",scale = " + scale);
        String format;
        String res;
        switch(scale){
            case 0:
                res = String.format("%"+displaySize+"d",rs.getInt(colN));
                break;
            default:
                if (scale>0){
                    format = "%"+displaySize+"."+scale+"f";
                }else{
                    format = "%"+displaySize+"f";
                }
                debug("format = " + format);
                res = String.format(format,rs.getFloat(colN));
        }
        return res;
    }

    public String formatVarchar2(int colN) throws SQLException {
        String res;
        res = String.format("%"+getColFormatLength(colN)+"s",rs.getString(colN));
        return res;
    }

    public String formatChar(int colN) throws SQLException {
        String res;
        res = String.format("%"+getColFormatLength(colN)+"s",rs.getString(colN));
        return res;
    }

    public String formatClob(int colN) throws SQLException {
        String res;
        Clob clob = rs.getClob(colN);
        res = clob.getSubString(1, (int) clob.length());
        return res;
    }

    public String formatBlob(int colN) throws SQLException {
        String res;
        Blob blob = rs.getBlob(colN);
        byte[] bdata = blob.getBytes(1, (int) blob.length());
        res = new String(bdata);
        return res;
    }

    public String formatDate(int colN) throws SQLException {
        String res;
        Timestamp timestamp = rs.getTimestamp(colN);
        if(timestamp != null) {
            SimpleDateFormat sdf = new SimpleDateFormat(Props.getValue("DATE_FORMAT", "yyyy-MM-dd HH:mm:ss"), Locale.US);
            res = sdf.format(timestamp);
        }else{
            res = Props.getValue("format.set.null","");
        }
        return lpad(res, getColFormatLength(colN), " ");
    }

    public String formatField(int colN) throws SQLException {
        String res= "";
        switch (metadata.getColumnTypeName(colN)) {
            case "NUMBER":
                res = formatNumber(colN);
                break;
            case "VARCHAR2":
                res = formatVarchar2(colN);
                break;
            case "CHAR":
                res = formatVarchar2(colN);
                break;
            case "CLOB":
                res = formatClob(colN);
                break;
            case "BLOB":
                res = formatBlob(colN);
                break;
            case "DATE":
                res = formatDate(colN);
                break;
        }
        return res;
    }

    public void printRS() throws SQLException {
        ResultSetMetaData metaData = rs.getMetaData();
        int colCount = metaData.getColumnCount();

        System.out.println(this.formatHeader());
        System.out.println(this.br());

        while (rs.next()){
            String[] fields = new String[colCount];
            for(int i=1;i<=colCount;i++){
                fields[i-1] = formatField(i);
            }

            String line="";
            for(int i=1;i<=colCount;i++){
                line += fields[i-1] + Props.getValue("format.set.colseparator"," ");
            }
            System.out.println(line);
        }
    }

}
