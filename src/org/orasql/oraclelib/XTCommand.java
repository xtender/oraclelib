package org.orasql.oraclelib;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by malakshinovss on 18.11.2014.
 */
public class XTCommand {
    private String commandText;
    private XTCommandType commandType;
    private boolean finished = true;

    private static List<String> sqlStarts = new ArrayList<String>()
            {{
                    add("SELECT");
                    add("INSERT");
                    add("UPDATE");
                    add("DELETE");
                    add("MERGE");
            }};

    private static List<String> plsqlStarts = new ArrayList<String>()
    {{
            add("DECLARE");
            add("BEGIN");
            add("UPDATE");
            add("DELETE");
            add("MERGE");
        }};

    public void parse(String commandText){
        if (finished){
            this.commandText = commandText;

        }
    }

    private static XTCommandType CommandType(String commandText){
        Pattern pattern = Pattern.compile("^\\W*(\\w+)");
        Matcher matcher = pattern.matcher(commandText);
        if (matcher.matches()){
            String firstWord = matcher.group(1);
            if(sqlStarts.contains(firstWord.toUpperCase())){
                return XTCommandType.SQL;
            }else if(plsqlStarts.contains(firstWord.toUpperCase())){
                return XTCommandType.PLSQL;
            }
        }
        return XTCommandType.UNKNOWN;
    }

    private boolean chechEnd(){
        return true;
    }
}
