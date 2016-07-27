package com.biklom.wikia.objects;

import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.*;


public class Unit {

    public static final String UNIT_CODE = "codename";
    public static final String UNIT_ELEMENT = "element";
    public static final String UNIT_INITIATIVE = "initiative";
    public static final String UNIT_BESTIARY = "bestiary";
    public static final String SKILL_END_DESCRIPTION = "description";
    public static final String UNIT_NAME_EN = "nameen";
    public static final String SKILL_BASIC = "skillname";
    public static final String SKILL_LEADER = "leaderskillname";

    @Getter @Setter private String codeBasicSkill;
    @Getter @Setter private String codeLeaderSkill;
    @Getter @Setter private String wikiBasicSkill = "";
    @Getter @Setter private String wikiLeaderSkill = "";
    @Getter @Setter private String codename;
    @Getter @Setter private String maxlevel;
    @Getter @Setter private String maxxp;
    @Getter @Setter private String mindp;
    @Getter @Setter private String maxdp;
    @Getter @Setter private String minhp;
    @Getter @Setter private String maxhp;
    @Getter @Setter private String skillcharge;
    @Getter @Setter private String rarity;
    @Getter @Setter private String power;
    @Getter @Setter private String morphsinto = "";
    @Getter @Setter private String morphsfrom = "";
    @Getter @Setter private String speed;
    @Getter @Setter private String material1  = "";
    @Getter @Setter private String material2 = "";
    @Getter @Setter private String material3 = "";

    @Getter private String element;
    @Getter private String bestiaryslot;
    @Getter private String initiative;

    private final Set<String> foundsIn = new TreeSet<>();
    private final Set<String> usedBy = new TreeSet<>();
    private final Map<String,String> names = new TreeMap<>();

    public Unit() {}

    public void setElement(String element) {
        this.element = StringUtils.capitalize(element);
    }

    public void setBestiarySlot(String bestiarySlot) {
        this.bestiaryslot = StringUtils.leftPad(bestiarySlot, 3, "0");
    }

    public void setInitiative(String initiative) {
        this.initiative = initiative.replaceAll(",", ".");
    }
    
    public String getElementNCode() {
        return element + "/" + codename;
    }
    
    public String getEnglishName() {
        return names.get("en");
    }

    public void addName(String language, String name){
        names.put(language.toLowerCase(),name);
    }
    public void addUsedBy(String unitCode, String elementName) {
        Validate.notEmpty(unitCode);
        Validate.notEmpty(elementName);

        usedBy.add(new StringBuilder().append(elementName).append("/").append(unitCode).toString());

    }

    public void addFoundIn(Dungeon dungeon, String boxslot, String level) {
        Validate.notEmpty(boxslot);
        foundsIn.add(new StringBuilder()
                .append(dungeon.getCode())
                .append("@")
                .append(boxslot)
                .append("@")
                .append(level)
                .append("@")
                .append(dungeon.getData(Dungeon.MODE))
                .toString());
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("\t\t").append(codename).append("={\n");
        sb.append("\t\t\t").append("bestiary=\"").append(bestiaryslot).append("\",\n");
        sb.append("\t\t\t").append("codename=\"").append(codename).append("\",\n");
        sb.append("\t\t\t").append("element=\"").append(element).append("\",\n");
        sb.append("\t\t\t").append("initiative=\"").append(initiative).append("\",\n");
        sb.append("\t\t\t").append("leaderskillname=\"").append(wikiLeaderSkill).append("\",\n");
        sb.append("\t\t\t").append("material1=\"").append(material1).append("\",\n");
        sb.append("\t\t\t").append("material2=\"").append(material2).append("\",\n");
        sb.append("\t\t\t").append("material3=\"").append(material3).append("\",\n");
        sb.append("\t\t\t").append("maxdp=\"").append(maxdp).append("\",\n");
        sb.append("\t\t\t").append("maxhp=\"").append(maxhp).append("\",\n");
        sb.append("\t\t\t").append("maxlevel=\"").append(maxlevel).append("\",\n");
        sb.append("\t\t\t").append("maxxp=\"").append(maxxp).append("\",\n");
        sb.append("\t\t\t").append("mindp=\"").append(mindp).append("\",\n");
        sb.append("\t\t\t").append("minhp=\"").append(minhp).append("\",\n");
        sb.append("\t\t\t").append("morphsfrom=\"").append(morphsfrom).append("\",\n");
        sb.append("\t\t\t").append("morphsinto=\"").append(morphsinto).append("\",\n");
        names.entrySet()
                .stream()
                .forEach((e) -> {
                    sb.append("\t\t\t").append("name").append(e.getKey().toLowerCase(Locale.ENGLISH)).append("=\"").append(e.getValue()).append("\",\n");
                });
        sb.append("\t\t\t").append("power=\"").append(power).append("\",\n");
        sb.append("\t\t\t").append("rarity=\"").append(rarity).append("\",\n");
        sb.append("\t\t\t").append("skillcharge=\"").append(skillcharge).append("\",\n");
        sb.append("\t\t\t").append("skillname=\"").append(wikiBasicSkill).append("\",\n");
        sb.append("\t\t\tusedby = {\n");
        usedBy.stream().forEach((e) -> {
            sb.append("\t\t\t\t\"").append(e).append("\",\n");
        });
        sb.append("\t\t\t},\n");
        sb.append("\t\t\tfoundin = {\n");
        foundsIn.stream().forEach((e) -> {
            sb.append("\t\t\t\t\"").append(e).append("\",\n");
        });
        sb.append("\t\t\t},\n");
        sb.append("\t\t},\n");
        return sb.toString();
    }
}
