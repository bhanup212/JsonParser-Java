package com.bhanu.jsonparser;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class JsonParser {
    private int index = 0;

    public Map<String, Object> parseJson(String input){
        index = 0;
        Map<String, Object> map = recursive(input);
        if (input.length() != index){
            throw new RuntimeException("Can't parse json: error at "+index+ " length of "+input.length());
        }
        return map;
    }

    private Map<String, Object> recursive(String input){
        boolean stop = false;
        String pendingKey = null;
        Phase phase = Phase.OPEN;
        Map<String, Object> res = new HashMap<>();
        while (index < input.length()){
            removeWhiteSpaces(input);
            char curChar = input.charAt(index);
            switch (phase){
                case OPEN:
                    if (curChar != '{'){
                        throw new RuntimeException("Json must starts with { : error at "+index+ " length of "+input.length());
                    }
                    phase = Phase.KEY;
                    break;
                case KEY:
                    if (curChar != '"'){
                        throw new RuntimeException("Json key must starts with \" : error at "+index+ " length of "+input.length());
                    }
                    pendingKey = extractString(input).toString();
                    index++;
                    removeWhiteSpaces(input);
                    char nextChar = input.charAt(index);
                    if (nextChar != ':'){
                        throw new RuntimeException("Json key must contains \":\" ,: error at "+index+ " length of "+input.length());
                    }
                    phase = Phase.VALUE;
                    break;
                case VALUE:
                    switch (curChar){
                        case '"':
                            res.put(pendingKey, extractString(input));
                            pendingKey = null;
                            index++;
                            break;
                        case '{':
                            res.put(pendingKey, recursive(input));
                            break;
                        default:
                            throw new RuntimeException("We need to get string value which starts from \" or child map object {, but \" + curChar");
                    }
                    removeWhiteSpaces(input);
                    nextChar = input.charAt(index);
                    switch (nextChar){
                        case ',':
                            phase = Phase.KEY;
                            break;
                        case '}':
                            phase = Phase.CLOSE;
                            break;
                        default:
                            throw new RuntimeException("We need to get , to get next key value pair or } stop and return \" + curChar");
                    }
                    break;
                case CLOSE:
                    stop = true;
                    break;
                default:
                    throw new RuntimeException("Invalid phase at json input string");
            }
            if (stop){
                break;
            }
            index++;
        }
        return res;
    }

    private void removeWhiteSpaces(String input){
        while (index < input.length() && input.charAt(index) == ' '){
            index++;
        }
    }

    private @NotNull String extractString(String input){
        int doubleQuoteCount = 0;
        StringBuilder sb = new StringBuilder();
        while (index < input.length() && doubleQuoteCount < 2){
            char c = input.charAt(index);
            if (c == '\"'){
                doubleQuoteCount++;
            } else {
                sb.append(c);
            }
            if (doubleQuoteCount == 2){
                break;
            }
            index++;
        }
        return sb.toString();
    }
}
