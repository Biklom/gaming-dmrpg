package com.biklom.wikia;

import com.biklom.wikia.objects.Unit;
import com.biklom.wikia.objects.Dungeon;
import com.biklom.wikia.objects.SKILL_TYPE;
import com.biklom.wikia.objects.Skill;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.*;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

public class DataExtractor {

    private static final String SHEET_UNITS = "All monsters";
    private static final String SHEET_UNITS_NAMES = "Monster Names";
    private static final String SHEET_DUNGEONS = "Dungeons";

    private static final String SHEET_SKILLS_NAMES = "Skill Names";
    private static final String SHEET_SKILLS_STATS = "Skills Stats";
    private static final String SHEET_SKILLS_BASIC = "Skills";
    private static final String SHEET_SKILLS_LEADER = "Leader Skills";
    private static final String MATERIAL_1 = "material1";
    private static final String MATERIAL_2 = "material2";
    private static final String MATERIAL_3 = "material3";
    private static final String MORPHS_INTO = "morphsinto";
    private static final String MORPHS_FROM = "morphsfrom";
    /**
     * Chapter Campaign Dungeon Name Food Blue1 Blue2 Red1 Red2 Bonus
     */
    private static final int DUNGEON_CHAPTER = 1;
    private static final int DUNGEON_CAMPAIGN = 2;
    private static final int DUNGEON_DUNGEON = 3;
    private static final int DUNGEON_NAME = 4;
    private static final int DUNGEON_FOOD = 5;
    private static final int DUNGEON_BLUE_1 = 6;
    private static final int DUNGEON_BLUE_2 = 7;
    private static final int DUNGEON_RED_1 = 8;
    private static final int DUNGEON_RED_2 = 9;
    private static final int DUNGEON_BONUS = 10;
    private final String inputDataPath;
    private final String outputDir;

    public static void main(String[] args) {
    }

    public DataExtractor(String inputFile, String outputDirectory) {
        inputDataPath = inputFile;
        outputDir = outputDirectory;
    }

    public void generateData() {
        File file = new File(inputDataPath);
        try (Workbook workbook = WorkbookFactory.create(file)) {
            Map<String, Skill> skillsNames = generateSkillDesc(workbook.getSheet(SHEET_SKILLS_NAMES));
            updateSkillsDescs(workbook.getSheet(SHEET_SKILLS_STATS),skillsNames);
            Map<String, String[]> unitsNames = generateUnitsNames(workbook.getSheet(SHEET_UNITS_NAMES));
            Map<String, List<Unit>> units = generateUnitsData(workbook.getSheet(SHEET_UNITS), unitsNames);

            Map<String, Unit> mapUnitByCode = getMapUnitByCodeOrENName(units);
            ajustAllUnitsCrossing(mapUnitByCode);
            ajustBasicSkills(mapUnitByCode, skillsNames);
            ajustLeaderSkills(mapUnitByCode, skillsNames);
            List<Dungeon> dungeons = generateDungeonsData(workbook.getSheet(SHEET_DUNGEONS), mapUnitByCode);
            dumpUnitsToFile(units);
            dumpDungeonToFile(dungeons);
            dumpSkillsToFile(skillsNames, SKILL_TYPE.BASIC);
            dumpSkillsToFile(skillsNames, SKILL_TYPE.LEADER);
        } catch (InvalidFormatException | IOException ioe) {
            Logger.getLogger(DataExtractor.class.getName()).log(Level.SEVERE, null, ioe);
        }
    }

    public Map<String, Unit> getMapUnitByCodeOrENName(Map<String, List<Unit>> units) {
        Map<String, Unit> map = new HashMap<>();
        for (List<Unit> l : units.values()) {
            for (Unit u : l) {
                map.put(u.getCodename(), u);
                map.put(u.getEnglishName(), u);
            }
        }
        return map;
    }
    public void updateSkillsDescs(Sheet sheet, Map<String, Skill> skills) {
        if (sheet != null) {
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row aRow = sheet.getRow(i);

                if (aRow == null) {
                    continue;
                }
                String skillCode = getCellStringValue(aRow.getCell(0));
                String descvalues = getCellStringValue(aRow.getCell(2));
                Skill sk = skills.get(skillCode);
                System.out.println("skillCode => "+skillCode+ " : sk = "+sk);
                if(sk != null) {
                    sk.initPlaceholders(sk.getPHDescription("en"), descvalues);
                }
            }
            
        }
    }

    public Map<String, String[]> generateUnitsNames(Sheet sheet) {
        System.out.println("generateUnitsNames.in, sheet : " + sheet);
        Map<String, String[]> units = new HashMap<>();
        if (sheet != null) {
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row unitRow = sheet.getRow(i);

                if (unitRow == null) {
                    continue;
                }
                String unitCode = getUnitCellValue(unitRow.getCell(0));
                String[] names = new String[]{
                    getUnitCellValue(unitRow.getCell(1)),
                    getUnitCellValue(unitRow.getCell(2)),
                    getUnitCellValue(unitRow.getCell(3)),
                    getUnitCellValue(unitRow.getCell(4)),
                    getUnitCellValue(unitRow.getCell(5))
                };
                System.out.println(unitCode + " <=> " + names);
                units.put(unitCode, names);
            }
        }
        System.out.println("generateUnitsNames.out");
        return units;
    }

    public Map<String, List<Unit>> generateUnitsData(Sheet sheet, Map<String, String[]> unitsNames) {
        Map<String, List<Unit>> units = new HashMap<>();
        if (sheet == null) {
            return units;
        }

        for (int i = 2; i <= sheet.getLastRowNum(); i++) {
            Row unitRow = sheet.getRow(i);
            if (unitRow == null) {
                continue;
            }
            Unit u = analyzeUnitRow(unitRow);
            updateNames(unitsNames, u);
            List<Unit> eltList = units.getOrDefault(u.getElement(), new ArrayList<>());
            units.putIfAbsent(u.getElement(), eltList);
            eltList.add(u);
        }
        return units;
    }

    private void updateNames(Map<String, String[]> unitsNames, Unit u) {
        String[] names = unitsNames.get(u.getCodename());
        System.out.println("updating names for [" + u.getCodename() + "]  with {" + names + "}");
        if (names != null) {
            u.addName("fr", names[0]);
            u.addName("en", names[1]);
            u.addName("it", names[2]);
            u.addName("es", names[3]);
            u.addName("de", names[4]);
        }
    }

    public String getUnitCellValue(Cell c) {
        return getUnitCellValue(c, false);
    }

    public String getUnitCellValue(Cell c, boolean isFloat) {
        String v;
        switch (c.getCellType()) {
            case Cell.CELL_TYPE_STRING:
                v = c.getStringCellValue();
                break;
            case Cell.CELL_TYPE_NUMERIC:
                if (isFloat) {
                    v = String.valueOf(c.getNumericCellValue());
                } else {
                    v = String.valueOf((int) c.getNumericCellValue());
                }
                break;
            default:
                v = "";
                break;
        }
        return v;
    }

    public Unit analyzeUnitRow(Row unitRow) {
        Unit u = new Unit();
        // Bestiary
        u.setBestiarySlot(getUnitCellValue(unitRow.getCell(0)));
        // CodeName
        u.setCodename(getUnitCellValue(unitRow.getCell(1)));
        // level
        u.setMaxlevel(getUnitCellValue(unitRow.getCell(2)));
        // XP
        u.setMaxxp(getUnitCellValue(unitRow.getCell(3)));
        // MinDp
        u.setMindp(getUnitCellValue(unitRow.getCell(4)));
        // MaxDP
        u.setMaxdp(getUnitCellValue(unitRow.getCell(5)));
        // MinHP
        u.setMinhp(getUnitCellValue(unitRow.getCell(6)));
        // MaxHP
        u.setMaxhp(getUnitCellValue(unitRow.getCell(7)));
        // atl Init
        u.setInitiative(getUnitCellValue(unitRow.getCell(8), true));
        // Skill charge
        u.setSkillcharge(getUnitCellValue(unitRow.getCell(9)));
        // speed
        u.setSpeed(getUnitCellValue(unitRow.getCell(10)));
        // Element
        u.setElement(getUnitCellValue(unitRow.getCell(11)));
        // Rarity
        u.setRarity(getUnitCellValue(unitRow.getCell(12)));
        // Skill
        u.setCodeBasicSkill(getUnitCellValue(unitRow.getCell(13)));
        // Leader Skill
        u.setCodeLeaderSkill(getUnitCellValue(unitRow.getCell(14)));
        // power
        u.setPower(getUnitCellValue(unitRow.getCell(15)));
        // evolve in
        u.setMorphsinto(getUnitCellValue(unitRow.getCell(16)));

        return u;
    }

    private void dumpUnitsToFile(Map<String, List<Unit>> units) {
        for (Map.Entry<String, List<Unit>> e : units.entrySet()) {
            StringBuilder sb = new StringBuilder();
            sb.append("--[[\n\nBefore editing this page, please read :\n\n\t"
                    + "http://dungeon-monsters-rpg.wikia.com/wiki/How_to_contribute\n\n--]]\n"
                    + "\n\nreturn {\n");
            e.getValue().stream().forEach((u) -> {
                sb.append(u.toString());
            });
            sb.append("}");
            try {
                FileUtils.writeStringToFile(new File(outputDir, "wikia_" + e.getKey() + ".lua"), sb.toString(), "UTF-8");
            } catch (IOException ex) {
                Logger.getLogger(DataExtractor.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private Map<Integer, String> extractHeaders(Row header) {
        Map<Integer, String> headerMap = new HashMap<>();
        Iterator<Cell> cellItr = header.cellIterator();
        while (cellItr.hasNext()) {
            Cell c = cellItr.next();
            String n = cleanUnitsHeaderName(c.getStringCellValue());
            headerMap.put(c.getColumnIndex(), n);
        }
        return headerMap;
    }

    public String cleanUnitsHeaderName(String name) {
        if (name == null) {
            return "";
        }
        String n = name;
        if (n.matches("[A-Z][A-Z] name")) {
            n = n.replaceAll("(.*) (.*)", "$2 $1");
        }
        return n.replaceAll("\\s", "").toLowerCase();
    }

    private List<Dungeon> generateDungeonsData(Sheet sheet, Map<String, Unit> units) {
        List<Dungeon> dungeons = new ArrayList<>();
        if (sheet != null) {
            Row r = sheet.getRow(0);
            System.out.println("R(0) nb cell  : " + r.getLastCellNum());
            Map<Integer, String> headerMap = extractHeaders(sheet.getRow(1));
            String mode = Dungeon.MODE_NORMAL;
            for (int i = 0; i <= sheet.getLastRowNum(); i++) {

                Row aRow = sheet.getRow(i);
                if (aRow == null) {
                    continue;
                }
                if (aRow.getLastCellNum() < 2) {
                    String m = getCellStringValue(aRow.getCell(0));
                    mode = StringUtils.defaultIfEmpty(m, Dungeon.MODE_NORMAL).toLowerCase().replaceAll(" mode", "");
                    continue;
                }

                if ("Chapter".equalsIgnoreCase(getCellStringValue(aRow.getCell(0)))) {
                    continue;
                }

                Dungeon aDungeon = new Dungeon();
                aDungeon.addData(Dungeon.MODE, mode);

                Iterator<Cell> cellItr = aRow.cellIterator();
                while (cellItr.hasNext()) {
                    Cell c = cellItr.next();
                    String v = getCellStringValue(c);
                    String k = headerMap.get(c.getColumnIndex());
                    switch (c.getColumnIndex()) {
                        case 5:
                            k = Dungeon.BLUE_1;
                            addDungeonUnit(aDungeon, k, v, units);
                            break;
                        case 6:
                            k = Dungeon.BLUE_2;
                            addDungeonUnit(aDungeon, k, v, units);
                            break;
                        case 7:
                            k = Dungeon.RED_1;
                            addDungeonUnit(aDungeon, k, v, units);
                            break;
                        case 8:
                            k = Dungeon.RED_2;
                            addDungeonUnit(aDungeon, k, v, units);
                            break;
                        case 9:
                            k = Dungeon.BONUS;
                            addDungeonUnit(aDungeon, k, v, units);
                            break;
                        default:
                            aDungeon.addData(k, v);
                    }
                }
                dungeons.add(aDungeon);
            }
        }

        return dungeons;
    }

    private String getCellStringValue(Cell c) {
        String v;
        switch (c.getCellType()) {
            case Cell.CELL_TYPE_STRING:
                v = c.getStringCellValue();
                break;
            case Cell.CELL_TYPE_NUMERIC:
                v = StringUtils.leftPad(String.valueOf((int) c.getNumericCellValue()), 2, "0");
                break;
            default:
                v = "";
                break;
        }
        return v;
    }

    public Map<String, Skill> generateSkillDesc(Sheet sheet) {
        Map<String, Skill> skills = new TreeMap<>();

        if (sheet != null) {
            for (int i = 1; i < sheet.getLastRowNum() - 2; i += 2) {
                Row aRow = sheet.getRow(i);
                Skill aSkill = new Skill();
                aSkill.setInternalCode(getCellStringValue(aRow.getCell(0)));
                String s = getCellStringValue(aRow.getCell(1));
                if (StringUtils.isNotEmpty(s)) {
                    aSkill.addName("fr", getCellStringValue(aRow.getCell(1)));
                    aSkill.addName("en", getCellStringValue(aRow.getCell(2)));
                    aSkill.addName("it", getCellStringValue(aRow.getCell(3)));
                    aSkill.addName("es", getCellStringValue(aRow.getCell(4)));
                    aSkill.addName("de", getCellStringValue(aRow.getCell(5)));
                    aRow = sheet.getRow(i + 1);
                    aSkill.addPHDescription("fr", getCellStringValue(aRow.getCell(1)));
                    aSkill.addPHDescription("en", getCellStringValue(aRow.getCell(2)));
                    aSkill.setDescription( getCellStringValue(aRow.getCell(2)));
                    aSkill.addPHDescription("it", getCellStringValue(aRow.getCell(3)));
                    aSkill.addPHDescription("es", getCellStringValue(aRow.getCell(4)));
                    aSkill.addPHDescription("de", getCellStringValue(aRow.getCell(5)));
                    skills.put(aSkill.getInternalCode(), aSkill);
                }
            }
        }
        return skills;

    }

    public Map<String, Skill> generateOneCategorySkill(Sheet sheet) {
        Map<String, Skill> skills = new TreeMap<>();
        if (sheet != null) {
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row aRow = sheet.getRow(i);
                Skill aSkill = new Skill();
                aSkill.setName(getCellStringValue(aRow.getCell(0)));
                aSkill.setDescription(getCellStringValue(aRow.getCell(1)));
                skills.put(aSkill.getName().toLowerCase(), aSkill);
            }
        }
        return skills;
    }

    private void dumpDungeonToFile(List<Dungeon> dungeons) {
        StringBuilder sb = new StringBuilder();
        sb.append("--[[\n\nBefore editing this page, please read :\n\n\t"
                + "http://dungeon-monsters-rpg.wikia.com/wiki/How_to_contribute\n\n--]]\n"
                + "\n\nreturn {\n");
        dungeons.stream().forEach((u) -> {
            sb.append(u.toString());
        });
        sb.append("}");
        try {
            FileUtils.writeStringToFile(new File(outputDir, "wikia_dungeons.lua"), sb.toString(), "UTF-8");

        } catch (IOException ex) {
            Logger.getLogger(DataExtractor.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void ajustAllUnitsCrossing(Map<String, Unit> mapUnitByCode) {
        mapUnitByCode.entrySet().forEach((e) -> {
            ajustOneUnit(e.getValue(), mapUnitByCode);
        });
    }

    private void ajustOneUnit(Unit u, Map<String, Unit> mapUnitByCode) {
        u.setMaterial1(ajustMaterial(u.getMaterial1(), mapUnitByCode, u));
        u.setMaterial2(ajustMaterial(u.getMaterial2(), mapUnitByCode, u));
        u.setMaterial3(ajustMaterial(u.getMaterial3(), mapUnitByCode, u));
        ajustMorphsFrom(mapUnitByCode.get(u.getMorphsinto()), u);
    }

    private String ajustMaterial(String unitCode, Map<String, Unit> mapUnitByCode, Unit usedBy) {
        if (StringUtils.isNotEmpty(unitCode)) {
            Unit unitMat = mapUnitByCode.get(unitCode.replaceAll("(.*)/(.*)", "$2"));
            Validate.notNull(unitMat, "unitcode invalid : " + unitCode);
            unitMat.addUsedBy(usedBy.getCodename(), usedBy.getElement());
            return unitMat.getElementNCode();
        }
        return "";
    }

    private void ajustMorphsFrom(Unit morphsInto, Unit morphsFrom) {
        if (morphsFrom != null) {
            if (morphsInto != null) {
                morphsInto.setMorphsfrom(morphsFrom.getElementNCode());
                morphsFrom.addUsedBy(morphsInto.getCodename(), morphsInto.getElement());
            }
            if (StringUtils.isEmpty(morphsFrom.getMorphsfrom())) {
                morphsFrom.setMorphsfrom("");
            }
        }
    }

    private void addDungeonUnit(Dungeon aDungeon, String boxType, String unitWithLevel, Map<String, Unit> units) {
        String unitCodeOrEn = unitWithLevel.replaceAll("(.*) Lvl\\.(.*)", "$1");
        Unit u = units.get(unitCodeOrEn);
        Validate.notNull(u, "Invalid code : [" + unitCodeOrEn + "] from [" + unitWithLevel + "]");
        String level = unitWithLevel.replaceAll("(.*) Lvl\\.(.*)", "$2");
        u.addFoundIn(aDungeon, boxType, level);
        aDungeon.addData(boxType, u.getCodename());
        aDungeon.addData(boxType + "_level", level);
    }

    private void ajustBasicSkills(Map<String, Unit> mapUnitByCode, Map<String, Skill> skills) {
        mapUnitByCode.values().stream().forEach((u) -> {
            String s =  u.getCodeBasicSkill();
            System.out.println("Basic skill : "+s);
            if (StringUtils.isNotEmpty(s)) {
                Skill sk = skills.get(s);
                System.out.println("Basic skill Obj: "+sk);
                if (sk != null) {
                    u.setWikiBasicSkill(sk.getTranslatedName("en"));
                    sk.addUsedby(u.getElementNCode());
                } else {
                    System.err.println("Unknown Basic skill : " + s + " for unit : " + u.getElementNCode());
                }
            }
        });
    }
    private void ajustLeaderSkills(Map<String, Unit> mapUnitByCode, Map<String, Skill> skills) {
        mapUnitByCode.values().stream().forEach((u) -> {
            String s = u.getCodeLeaderSkill();
            System.out.println("Leader skill : "+s);
            if (StringUtils.isNotEmpty(s)) {
                Skill sk = skills.get(s);
                System.out.println("Leader skill obj: "+sk);
                if (sk != null) {
                    u.setWikiLeaderSkill(sk.getTranslatedName("en"));
                    sk.addUsedby(u.getElementNCode());
                } else {
                    System.err.println("Unknown Leader skill : " + s + " for unit : " + u.getElementNCode());
                }
            }
        });
    }

    private void dumpSkillsToFile(Map<String, Skill> skills, SKILL_TYPE category) {
        StringBuilder sb = new StringBuilder();
        sb.append("--[[\n\nBefore editing this page, please read :\n\n\t"
                + "http://dungeon-monsters-rpg.wikia.com/wiki/How_to_contribute\n\n--]]\n"
                + "\n\nreturn {\n");
        skills.values().stream().forEach((u) -> {
            sb.append(u.toString());
        });
        sb.append("}");
        try {
            FileUtils.writeStringToFile(new File(outputDir, "wikia_skills_" + StringUtils.capitalize(category.name()) + ".lua"), sb.toString(), "UTF-8");

        } catch (IOException ex) {
            Logger.getLogger(DataExtractor.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }
}
