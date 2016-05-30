package com.biklom.wikia;

import java.util.*;
import org.apache.commons.lang3.Validate;

public class Skill {
    
    /** Match the ID column from "Skill Names" tab. */
    String internalCode; 
    /** Match the english name of skill. */
    String name;
    /** Match technical description with placeholders. */
    String description;
    /** translated descriptions with placeholders replacement. Languages code in lowercase. */
    Map<String, String> translatedDesc = new HashMap<>();
    /** Names in supported languages. Languages code in lowercase. */
    Map<String, String> translatedNames = new HashMap<>();
    /** List of units using this skill. */
    Set<String> usedby = new TreeSet<>();
    /** map of placeholders and associated values. */
    Map<String,String> placeholders2Values = new HashMap<>();
    
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
        sb.append("\t\tnames = {\n");
        translatedNames.entrySet().stream().forEach((e) -> {
            sb.append("\t\t\t").append(e.getKey()).append("\"").append(e.getValue()).append("\",\n");
        });
        sb.append("\t\t}\n");
        sb.append("\t\tdescripptions = {\n");
        translatedDesc.entrySet().stream().forEach((e) -> {
            sb.append("\t\t\t").append(e.getKey()).append("\"").append(e.getValue()).append("\",\n");
        });
        sb.append("\t\t}\n");
        sb.append("\t},\n");
        return sb.toString();
    }

    private String makeCode() {
        return name.replaceAll("[' \\-]", "_").replaceAll("__+", "_");
    }
    
    public void addPHDescription (String lang, String desc) {
        Validate.notEmpty(desc);
        String s = desc;
        for(Map.Entry<String,String> e :  placeholders2Values.entrySet()) {
            s = s.replace(e.getKey(), e.getValue());
        }
        translatedDesc.put( lang, s );
    }
    
    public void initPlaceholders(String desc, String trad) {
        List<String> descSplit = Arrays.asList(desc.replaceAll("\\\\n", " ").split("\\s"));
        List<String> tradSplit = Arrays.asList(trad.replaceAll("\\\\n", " ").split("\\s"));
        List<String> comm = new ArrayList<>();
        comm.addAll(descSplit);
        comm.removeAll(tradSplit);
        List<String> tok = new ArrayList<>();
        tok.addAll(tradSplit);
        tok.removeAll(descSplit);
        for (int i = Math.min(tok.size() , comm.size()); i>0;i--) {
            placeholders2Values.put(comm.get(i - 1), tok.get(i - 1));
        }
    }
    
}
