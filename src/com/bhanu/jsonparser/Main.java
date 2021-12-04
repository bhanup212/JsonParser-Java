package com.bhanu.jsonparser;

import java.util.Map;

public class Main {

    public static void main(String[] args) {
	    String input = "{\"abc\":{\"bcde\":\"fg\",\"xml\":\"asd\"}}";
        Map<String, Object> output = new JsonParser().parseJson(input);
        System.out.println("final json is: "+output.toString());
    }
}
