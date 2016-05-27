package com.biklom.wikia;
import java.util.*;
import org.apache.commons.lang3.*;

public class Dungeon{
    public static final String CHAPTER = "chapter";
    public static final String CAMPAIGN = "campaign";
    public static final String DUNGEON = "dungeon";
    public static final String NAME = "name";
    public static final String FOOD = "food";
    public static final String BLUE_1 = "blue1";
    public static final String BLUE_1_LEVEL = "blue1_level";
    public static final String BLUE_2 = "blue2";
    public static final String BLUE_2_LEVEL = "blue2_level";
    public static final String RED_1 = "red1";
    public static final String RED_1_LEVEL = "red1_level";
    public static final String RED_2 = "red2";
    public static final String RED_2_LEVEL = "red2_level";
    public static final String BONUS = "bonus";
    public static final String BONUS_LEVEL = "bonus_level";
    
    
    public Dungeon(){
        addData(BLUE_2, "");
        addData(BLUE_2_LEVEL, "");
        addData(RED_2, "");
        addData(RED_2_LEVEL, "");        
    }
    
    
    
    private final Map<String, String> datas = new TreeMap<>();
    
    public void addData(String name, String value) {
        String v = StringUtils.defaultIfEmpty(value,"");
        datas.put(name, v);
    }
    public String getData(String key) {
        return datas.get(key);
    }
    public String getCode(){
        return new StringBuilder("ch")
        .append(getData(CHAPTER))
        .append("_cp")
        .append(getData(CAMPAIGN))
        .append("_dg")
        .append(getData(DUNGEON)).toString();
    }
    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder().append("\t").append(getCode()).append(" = {\n");
        sb.append("\t\tcode=\"").append(getCode()).append("\",\n");
        datas.entrySet()
                .stream()
                .forEach((e) -> {
            sb.append("\t\t").append(e.getKey()).append("=\"").append(e.getValue()).append("\",\n");
        });
        
        return sb.append("\t},\n").toString();
    }
}
