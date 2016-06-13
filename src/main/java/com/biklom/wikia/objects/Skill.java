package com.biklom.wikia.objects;

import java.util.*;
import org.apache.commons.lang3.Validate;

public class Skill {
    
    /** Match the ID column from "Skill Names" tab. */
    private String internalCode; 
    /** Match the english name of skill. */
    private String name;
    /** Match technical description with placeholders. */
    private String description;
    /** translated descriptions without placeholders replacement. Languages code in lowercase. */
    private final Map<String, String> translatedDescWithPH = new TreeMap<>();
    /** translated descriptions with placeholders replacement. Languages code in lowercase. */
    private Map<String, String> translatedDesc = new TreeMap<>();
    /** Names in supported languages. Languages code in lowercase. */
    private Map<String, String> translatedNames = new TreeMap<>();
    /** List of units using this skill. */
    private Set<String> usedby = new TreeSet<>();
    /** map of placeholders and associated values. */
    private Map<String,String> placeholders2Values = new HashMap<>();
    
    
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("\t").append(makeCode()).append(" = {\n");
        sb.append("\t\tenglishname = \"").append(getTranslatedName("en")).append("\",\n");
        sb.append("\t\tdescription = \"").append(transformDescription (translatedDescWithPH.get("en"),"en")).append("\",\n");
        sb.append("\t\ttechnical_description = \"").append(description).append("\",\n");
        sb.append("\t\tusedby = {\n");
        usedby.stream().forEach((u) -> {
            sb.append("\t\t\t\"").append(u).append("\",\n");
        });
        sb.append("\t\t},\n");
        sb.append("\t\tnames = {\n");
        translatedNames.entrySet().stream().forEach((e) -> {
            sb.append("\t\t\t").append(e.getKey()).append("=\"").append(e.getValue()).append("\",\n");
        });
        sb.append("\t\t},\n");
        sb.append("\t\tdescriptions = {\n");
        translatedDescWithPH.entrySet().stream().forEach((e) -> {
            sb.append("\t\t\t").append(e.getKey()).append("=\"").append(transformDescription (e.getValue(),e.getKey())).append("\",\n");
        });
        sb.append("\t\t},\n");
        sb.append("\t},\n");
        return sb.toString();
    }

    private String makeCode() {
        return getTranslatedName("en").replaceAll("[' \\-]", "_").replaceAll("__+", "_");
    }
    public void addPHDescription (String lang, String desc) {
        translatedDescWithPH.put(lang.toLowerCase(),desc);
    }
    public String getPHDescription(String lang) {
        return translatedDescWithPH.get(lang.toLowerCase());
    }
    
    public void addAndTransformDescription (String lang, String desc) {
        Validate.notEmpty(desc);
        String s = desc;
        for(Map.Entry<String,String> e :  placeholders2Values.entrySet()) {
            s = s.replace(e.getKey(), e.getValue());
        }
        translatedDesc.put( lang, s );
    }
    public String transformDescription (String desc,String lang) {
        Validate.notEmpty(desc);
        String s = desc;
        for(Map.Entry<String,String> e :  placeholders2Values.entrySet()) {
            s = s.replace(e.getKey(), e.getValue());
        }
        if(s.indexOf("{")>-1) {
            System.err.println("Incompleted translation for skill code ["+internalCode+"] / name ["+getTranslatedName("en")+"] in language ["+lang+"]");
        }
        return s;
    }
    
    public void initPlaceholders(String desc, String trad) {
        List<String> descSplit = Arrays.asList(desc.replaceAll("\\\\n|\\.|\\,", " ").split("\\s"));
        List<String> tradSplit = Arrays.asList(trad.replaceAll("\\\\n|\\.|\\,", " ").split("\\s"));
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

    public String getInternalCode() {
        return internalCode;
    }

    public void setInternalCode(String internalCode) {
        this.internalCode = internalCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Map<String, String> getTranslatedDesc() {
        return translatedDesc;
    }

    public void setTranslatedDesc(Map<String, String> translatedDesc) {
        this.translatedDesc = translatedDesc;
    }

    public Map<String, String> getTranslatedNames() {
        return translatedNames;
    }

    public void setTranslatedNames(Map<String, String> translatedNames) {
        this.translatedNames = translatedNames;
    }
    
    
    
    public Set<String> getUsedby() {
        return usedby;
    }

    public void addUsedby(String unit) {
        usedby.add(unit);
    }

    public Map<String,String> getPlaceholders2Values() {
        return placeholders2Values;
    }

    public void setPlaceholders2Values(Map<String,String> placeholders2Values) {
        this.placeholders2Values = placeholders2Values;
    }
    
    public void addName(String lang, String name) {
        translatedNames.put(lang.toLowerCase(),name);
    }
    
    public String getTranslatedName(String lang) {
        return translatedNames.get(lang);
    }

    public boolean isUsed() {
        return usedby.size() > 0;
    }
}
