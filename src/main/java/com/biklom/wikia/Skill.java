package com.biklom.wikia;

import java.util.*;

public class Skill {

    String name;
    String description;
    Set<String> usedby = new TreeSet<>();

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("\t").append(makeCode()).append(" = {\n");
        sb.append("\t\tenglishname = \"").append(name).append("\",\n");
        sb.append("\t\tdescription = \"").append(description).append("\",\n");
        sb.append("\t\tusedby = {\n");
        usedby.stream().forEach((u) -> {
            sb.append("\t\t\t\"").append(u).append("\",\n");
        });
        sb.append("\t\t}\n");
        sb.append("\t},\n");
        return sb.toString();
    }

    private String makeCode() {
        return name.replaceAll("[' \\-]", "_").replaceAll("__+", "_");
    }

}
