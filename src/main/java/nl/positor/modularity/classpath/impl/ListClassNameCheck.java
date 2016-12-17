package nl.positor.modularity.classpath.impl;

import nl.positor.modularity.classpath.api.ClassNameCheck;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Arien on 16-Dec-16.
 */
public class ListClassNameCheck implements ClassNameCheck {
    private final List<String> classNameList;

    public ListClassNameCheck(List<String> classNameList) {
        this.classNameList = new ArrayList<>(classNameList);
    }


    @Override
    public boolean test(String className) {
        return classNameList.contains(className);
    }
}
