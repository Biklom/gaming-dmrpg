package com.biklom.wikia;

import com.biklom.wikia.objects.Unit;
import com.biklom.wikia.objects.Dungeon;
import com.biklom.wikia.objects.SKILL_TYPE;
import com.biklom.wikia.objects.Skill;
import java.io.File;
import java.io.IOException;
import java.util.*;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.*;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DataExtractor {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataExtractor.class);
    private static final String SHEET_UNITS = "All monsters";
    private static final String SHEET_DUNGEONS = "Dungeons";
    private static final String SHEET_SKILLS_STATS = "Skills Stats";

    private final String inputDataPath;
    private final String outputDir;
    private final Set<String> excludeExport = new TreeSet<>();
    public static void main(String[] args) {
    }

    public DataExtractor(String inputFile, String outputDirectory, Collection<String> excludedFromExport) {
        inputDataPath = inputFile;
        outputDir = outputDirectory;
        if(excludedFromExport != null) {
            excludeExport.addAll(excludedFromExport);
        }
    }

    public void generateData() {
        File file = new File(inputDataPath);
        try (Workbook workbook = WorkbookFactory.create(file)) {
            Map<String, Skill> skillsNames = generateSkillDesc(workbook.getSheet(SHEET_SKILLS_STATS));
            Map<String, List<Unit>> units = generateUnitsData(workbook.getSheet(SHEET_UNITS));

            Map<String, Unit> mapUnitByCode = getMapUnitByCodeOrENName(units);
            ajustAllUnitsCrossing(mapUnitByCode);
            ajustBasicSkills(mapUnitByCode, skillsNames);
            ajustLeaderSkills(mapUnitByCode, skillsNames);
            List<Dungeon> dungeons = generateDungeonsData(workbook.getSheet(SHEET_DUNGEONS), mapUnitByCode);
            dumpUnitsToFile(units);
            dumpDungeonToFile(dungeons);
            List<Skill> namedSortedSkills = sortSkillsByEnName(skillsNames);
            dumpSkillsToFile(namedSortedSkills, SKILL_TYPE.BASIC);
            dumpSkillsToFile(namedSortedSkills, SKILL_TYPE.LEADER);
        } catch (InvalidFormatException | IOException ioe) {
            LOGGER.error( null, ioe);
        }
    }
    
    public List<Skill> sortSkillsByEnName(Map<String, Skill> skillsMap) {
        List<Skill> skills = new ArrayList<>();
        skills.addAll(skillsMap.values());
        Collections.sort(skills, new Comparator<Skill>() {
            @Override
            public int compare(Skill o1, Skill o2) {
                if(o1 == null) {
                    return -1;
                }
                if(o2 == null) {
                    return 1;
                }
                return StringUtils.defaultIfEmpty(o1.getTranslatedName("en"),"").compareToIgnoreCase(StringUtils.defaultIfEmpty(o2.getTranslatedName("en"),""));
            }
        });
        return skills;
    }
    public Map<String, Unit> getMapUnitByCodeOrENName(Map<String, List<Unit>> units) {
        Map<String, Unit> map = new HashMap<>();
        units.values().stream().forEach((l) -> {
            l.stream().forEach((u) -> {
                map.put(u.getCodename(), u);
                map.put(u.getEnglishName(), u);
            });
        });
        return map;
    }
    
    public Map<String, List<Unit>> generateUnitsData(Sheet sheet) {
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
            if (u != null) {
                //updateNames(unitsNames, u);
                List<Unit> eltList = units.getOrDefault(u.getElement(), new ArrayList<>());
                units.putIfAbsent(u.getElement(), eltList);
                eltList.add(u);
            }
        }
        return units;
    }

    public String getUnitCellValue(Cell c) {
        return getUnitCellValue(c, false);
    }

    public String getUnitCellValue(Cell c, boolean isFloat) {
        String v = "";
        if (c != null) {
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
        }
        return v;
    }

    public Unit analyzeUnitRow(Row unitRow) {
        String id = getUnitCellValue(unitRow.getCell(0));
        if (StringUtils.isEmpty(id)) {
            return null;
        }
        Unit u = new Unit();
        // Bestiary
        u.setBestiarySlot(id);
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
        // MorphRecipe1
        u.setMaterial1(getUnitCellValue(unitRow.getCell(17)));
        // MorphRecipe2
        u.setMaterial2(getUnitCellValue(unitRow.getCell(18)));
        // MorphRecipe3
        u.setMaterial3(getUnitCellValue(unitRow.getCell(19)));
        // name fr
        u.addName("fr", getUnitCellValue(unitRow.getCell(20)));
        // name en
        u.addName("en", getUnitCellValue(unitRow.getCell(21)));
        // it
        u.addName("it", getUnitCellValue(unitRow.getCell(22)));
        // es
        u.addName("es", getUnitCellValue(unitRow.getCell(23)));
        // de
        u.addName("de", getUnitCellValue(unitRow.getCell(24)));

        return u;
    }

    private void dumpUnitsToFile(Map<String, List<Unit>> units) {
        for (Map.Entry<String, List<Unit>> e : units.entrySet()) {
            StringBuilder sb = new StringBuilder();
            sb.append("--[[\n\nBefore editing this page, please read :\n\n\t"
                    + "http://dungeon-monsters-rpg.wikia.com/wiki/How_to_contribute\n\n--]]\n"
                    + "\n\nreturn {\n");
            e.getValue().stream().forEach((u) -> {
                if(excludeExport.contains(u.getBestiaryslot())) {
                    LOGGER.warn( "Skipping unit #{} [ {} ] due to NDA restrictions", u.getBestiaryslot(), u.getCodename());
                } else {
                    sb.append(u.toString());
                }
            });
            sb.append("}");
            try {
                FileUtils.writeStringToFile(new File(outputDir, "wikia_" + e.getKey() + ".lua"), sb.toString(), "UTF-8");
            } catch (IOException ex) {
                LOGGER.error("Error while writing units files : ", ex);
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
            LOGGER.debug("R(0) nb cell  : {}", r.getLastCellNum());
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
            String[] languages= {"en","fr","it","de","es"};
            for (int i = 1; i <= sheet.getLastRowNum() ; i ++) {
                Row aRow = sheet.getRow(i);
                Skill aSkill = new Skill();
                aSkill.setInternalCode(getCellStringValue(aRow.getCell(0)));
                int idxCol = 1;
                for(String lg : languages) {
                    aSkill.addName(lg, getCellStringValue(aRow.getCell(idxCol++)));
                    aSkill.addDescription(lg, getCellStringValue(aRow.getCell(idxCol++)));
                }
                skills.put(aSkill.getInternalCode(), aSkill);
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
            LOGGER.error( null, ex);
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
            String s = u.getCodeBasicSkill();

            if (StringUtils.isNotEmpty(s)) {
                Skill sk = skills.get(s);

                if (sk != null) {
                    u.setWikiBasicSkill(sk.getTranslatedName("en"));
                    if(excludeExport.contains(u.getBestiaryslot())) {
                        LOGGER.warn( "Umapped Basic skill : {} for unit : {} due to NDA restrictions", s, u.getElementNCode());
                    } else {
                        sk.addUsedby(u.getElementNCode());
                    }
                } else {
                    LOGGER.warn( "Unknown Basic skill : {} for unit : {}", s, u.getElementNCode());
                }
            }
        });
    }

    private void ajustLeaderSkills(Map<String, Unit> mapUnitByCode, Map<String, Skill> skills) {
        mapUnitByCode.values().stream().forEach((u) -> {
            String s = u.getCodeLeaderSkill();

            if (StringUtils.isNotEmpty(s)) {
                Skill sk = skills.get(s);

                if (sk != null) {
                    u.setWikiLeaderSkill(sk.getTranslatedName("en"));
                    if(excludeExport.contains(u.getBestiaryslot())) {
                        LOGGER.warn( "Umapped Leader skill : {} for unit : {} due to NDA restrictions", s, u.getElementNCode());
                    } else {
                        sk.addUsedby(u.getElementNCode());
                    }
                } else {
                    LOGGER.warn( "Unknown Leader skill : {} for unit : {}", s, u.getElementNCode());
                }
            }
        });
    }

    private void dumpSkillsToFile(Collection< Skill> skills, SKILL_TYPE category) {
        StringBuilder sb = new StringBuilder();
        sb.append("--[[\n\nBefore editing this page, please read :\n\n\t"
                + "http://dungeon-monsters-rpg.wikia.com/wiki/How_to_contribute\n\n--]]\n"
                + "\n\nreturn {\n");
        skills.stream().forEach((u) -> {
            if(SKILL_TYPE.findType(u.getInternalCode()) == category) {
                if (u.isUsed()) {
                    sb.append(u.toString());
                } else {
                    LOGGER.info( "Ignoring unused skill [{}]", u.getInternalCode());
                }
            }
        });
        sb.append("}");
        try {
            FileUtils.writeStringToFile(new File(outputDir, "wikia_skills_" + StringUtils.capitalize(category.name().toLowerCase()) + ".lua"), sb.toString(), "UTF-8");

        } catch (IOException ex) {
            LOGGER.error( null, ex);
        }
    }
}
