package com.amazonas.backend.service;

import java.util.LinkedList;
import java.util.List;

public class InitialRunFileExecutor {

    public static List<String[]> parser(String file) {
        String[] operations = (file+" ").split(";");
        List<String[]> ret = new LinkedList<>();
        if (!removeWhiteSpaces(operations[operations.length - 1]).isEmpty()) {
            throw new IllegalArgumentException("After the last operation there is more text: \"" + operations[operations.length - 1] + "\"");
        }
        for (int index = 0; index < operations.length - 1; index++) {
            String op = operations[index];
            String[] words = op.split("[(]");
            if(words.length != 2) {
                throw new IllegalArgumentException("missing '(' at operation: \"" + op + "\"");
            }
            String[] operator = words[0].split("\\W");
            if(operator.length != 1) {
                boolean found = false;
                for (String splited : operator) {
                    String str = removeWhiteSpaces(splited);
                    if (!str.isEmpty()) {
                        if (!found) {
                            found = true;
                            operator[0] = str;

                        }
                        else {
                            throw new IllegalArgumentException("Illegal operator at operation: \"" + op + "\" = " + operator.length);

                        }
                    }
                }
                if (!found) {
                    throw new IllegalArgumentException("no operator found at operation: \"" + op + "\" = " + operator.length);
                }
            }
            String[] argumentsWord = (words[1]+" ").split("[)]");
            if(argumentsWord.length != 2 || !removeWhiteSpaces(argumentsWord[1]).isEmpty()) {
                throw new IllegalArgumentException("no continue after aoeration at: \"" + op + "\"");
            }
            String[] arguments = argumentsWord[0].split(",");
            String[] operation = new String[arguments.length + 1];
            operation[0] = operator[0];
            for(int i = 1; i <= arguments.length; i++) {
                operation[i] = removeWhiteSpaces(arguments[i-1]);

            }
            ret.add(operation);
        }
        return ret;
    }

    private static String removeWhiteSpaces(String input){
        int first = -1;
        int end = -1;
        for (int i = 0; i < input.length(); i++) {
            if (input.charAt(i) > 32) {
                if (first == -1) {
                    first = i;
                }
                else {
                    end = i+1;
                }
            }
        }
        if (first == -1) {
            return "";
        }
        if (end == -1) {
            end = first + 1;
        }
        return input.substring(first, end);
    }
}
