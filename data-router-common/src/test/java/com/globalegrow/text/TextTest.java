package com.globalegrow.text;


import org.apache.commons.text.StrSubstitutor;
import org.apache.commons.text.StringSubstitutor;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class TextTest {

    @Test
    public void test() {
        Map valuesMap = new HashMap();
        valuesMap.put("animal", "quick brown fox");
        valuesMap.put("target", "lazy dog");
        String templateString = "The ${animal} jumped over the ${target}.";
        StringSubstitutor sub = new StringSubstitutor(valuesMap);
        String resolvedString = sub.replace(templateString);
        System.out.println(resolvedString);
    }

}
