package com.biklom.wikia.objects;

import java.util.*;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Skill {
    private static final Logger LOGGER = LoggerFactory.getLogger(Skill.class);
    
    
    /** Match the english name of skill. */
    @Getter @Setter private String name;
    @Getter @Setter private String description;
    @Getter @Setter private String internalCode; 

    /** translated descriptions. Languages code in lowercase. */
    private final Map<String, String> translatedDesc = new TreeMap<>();

    /** Names in supported languages. Languages code in lowercase. */
    private final Map<String, String> translatedNames = new TreeMap<>();

    /** List of units using this skill. */
    @Getter private final Set<String> usedby = new TreeSet<>();
    
    
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("\t").append(makeCode()).append(" = {\n");
        sb.append("\t\tenglishname = \"").append(getTranslatedName("en")).append("\",\n");
        sb.append("\t\tdescription = \"").append(getTranslatedDescription("en")).append("\",\n");
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
        translatedDesc.entrySet().stream().forEach((e) -> {
            sb.append("\t\t\t").append(e.getKey()).append("=\"").append(e.getValue()).append("\",\n");
        });
        sb.append("\t\t},\n");
        sb.append("\t},\n");
        return sb.toString();
    }

    private String makeCode() {
        return getTranslatedName("en").replaceAll("[' \\-]", "_").replaceAll("__+", "_");
    }

    public void addUsedby(String unit) {
        usedby.add(unit);
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

    public void addDescription(String lang, String desc) {
        translatedDesc.put(lang.toLowerCase(),desc);
    }

    public String getTranslatedDescription(String lang) {
        return translatedDesc.get(lang);
    }
}
