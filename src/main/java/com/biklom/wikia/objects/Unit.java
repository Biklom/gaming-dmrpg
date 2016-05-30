package com.biklom.wikia.objects;

import com.biklom.wikia.objects.Dungeon;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
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

    private final Set<String> foundsIn = new TreeSet<>();
    private final Set<String> usedBy = new TreeSet<>();

    private final Map<String, String> datas = new TreeMap<>();
    private String element;

    public Unit() {
    }

    public String getElementNCode() {
        return getElement() + "/" + getCodeName();
    }

    public String getData(String key) {
        return datas.get(key);
    }

    public String getElement() {
        return datas.get(UNIT_ELEMENT);
    }

    public String getEnglishName() {
        return datas.get(UNIT_NAME_EN);
    }

    public String getCodeName() {
        return datas.get(UNIT_CODE);
    }

    public void addData(String name, String value) {
        String v = StringUtils.defaultIfEmpty(value, "");
        switch (name) {
            case Unit.UNIT_ELEMENT:
                v = StringUtils.capitalize(v);
                break;
            case Unit.UNIT_BESTIARY:
                v = StringUtils.leftPad(v, 3, "0");
                break;
            default:
        }

        if (!name.toLowerCase().endsWith(SKILL_END_DESCRIPTION)) {
            datas.put(name, v);
        }
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
                .toString());
    }

    /**
     * <pre>
     * AngelFire={                         -- normalized internal Manacube code used as identifier, all non-alphabetic character should be replaced by "_"
     * codename="AngelFire",           -- internal Manacube code
     * nameen="Ignithiel",             -- english name
     * namefr="Ignithiel",             -- french name
     * nameit="Ignitiele",             -- italian name
     * namees="Igniciel",              -- spanish name
     * namede="Flammiel",              -- german name
     * maxlevel="40",                  -- maximum level
     * maxxp="80000",                  -- total xp need to reach max level
     * mindp="364",                    -- damage when level 1 without stars
     * maxdp="672",                    -- damage when level max without stars
     * minhp="522",                    -- health when level 1 without stars
     * maxhp="1138",                   -- health when level max without stars
     * initiative="4,25",              -- initiative, will be sanitized to display a dot a decimal separator.
     * skillcharge="5",                -- skill charge
     * element="fire",                 -- element
     * rarity="rare",                  -- rarity, will be translate into C, C+, U, U+, R, R+, UR, UR+, L
     * power="39",                     -- power without any stars
     * morphsinto="ArchangelFire",     -- unit code into which it will morph if possible
     * skillname="Breath Of Fire",     -- unit basic skill, link with description will be automatic
     * leaderskillname="",             -- unit leader skill once metamorphosed, link with description will be automatic
     * bestiaryslot=""                 -- numeric id in the bestiary (unit number)
     * },</pre
     *
     * @return
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("\t\t").append(datas.get(UNIT_CODE)).append("={\n");
        datas.entrySet()
                .stream()
                .forEach((e) -> {
                    sb.append("\t\t\t").append(e.getKey()).append("=\"").append(e.getValue()).append("\",\n");
                });
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

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + Objects.hashCode(this.datas);
        hash = 79 * hash + Objects.hashCode(this.element);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Unit other = (Unit) obj;
        if (!Objects.equals(this.element, other.element)) {
            return false;
        }

        return Objects.equals(this.datas, other.datas);
    }
}
