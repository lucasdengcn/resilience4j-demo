package org.example.demo.leets;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class NodeWalks {

    public List<String> walks(String s) {
        return walkRecurse("", 0, s.length(), s);
    }

    public List<String> walkRecurse(String prefix, int currentPos, int endPos, String s) {
        List<String> result = new ArrayList<>();
        if (currentPos + 1 >= endPos){
            return result;
        }
        char currentChar = s.charAt(currentPos);
        if (Objects.equals(prefix, "")){
            prefix = currentChar + "";
        }
        //
        currentPos++;
        currentChar = s.charAt(currentPos);
        result.add(prefix + currentChar);
        //
        List<String> stringList = walkRecurse(currentChar + "", currentPos, endPos, s);
        result.addAll(stringList);
        //
        if (currentPos + 1 >= endPos){
            return result;
        }
        result.add(prefix + s.charAt(currentPos + 1));
        //
        stringList = walkRecurse(currentChar + "", currentPos + 1, endPos, s);
        result.addAll(stringList);
        //
        return result;
    }

}
