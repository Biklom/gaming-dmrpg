package com.biklom.wikia;
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
public class DataExtractor{
    private static final String SHEET_UNITS="Units";
    private static final String SHEET_DUNGEONS="Dungeons";
    private static final String SHEET_SKILLS_BASIC="Skills";
    private static final String SHEET_SKILLS_LEADER="Leader Skills";
    private static final String MATERIAL_1="material1";
    private static final String MATERIAL_2="material2";
    private static final String MATERIAL_3="material3";
    private static final String MORPHS_INTO="morphsinto";
    private static final String MORPHS_FROM="morphsfrom";
    /**
     * Chapter	
     * Campaign	
     * Dungeon	
     * Name	
     * Food	
     * Blue1	
     * Blue2	
     * Red1	
     * Red2	
     * Bonus
     */
    private static final int DUNGEON_CHAPTER=1;
    private static final int DUNGEON_CAMPAIGN=2;
    private static final int DUNGEON_DUNGEON=3;
    private static final int DUNGEON_NAME=4;
    private static final int DUNGEON_FOOD=5;
    private static final int DUNGEON_BLUE_1=6;
    private static final int DUNGEON_BLUE_2=7;
    private static final int DUNGEON_RED_1=8;
    private static final int DUNGEON_RED_2=9;
    private static final int DUNGEON_BONUS=10;
    private final String inputDataPath;
    private final String outputDir;
    public static void main(String[] args){
    }
    public DataExtractor(String inputFile,String outputDirectory){
        inputDataPath=inputFile;
        outputDir=outputDirectory;
    }
    public void generateData(){
        File file=new File(inputDataPath);
        try(Workbook workbook=WorkbookFactory.create(file)){
            Map<String,Skill> basicSkills=generateOneCategorySkill(workbook.getSheet(SHEET_SKILLS_BASIC));
            Map<String,Skill> leaderSkills=generateOneCategorySkill(workbook.getSheet(SHEET_SKILLS_LEADER));
            Map<String,List<Unit>> units=generateUnitsData(workbook.getSheet(SHEET_UNITS));
            Map<String,Unit> mapUnitByCode=getMapUnitByCodeOrENName(units);
            ajustAllUnitsCrossing(mapUnitByCode);
            ajustSkills(mapUnitByCode,basicSkills,Unit.SKILL_BASIC);
            ajustSkills(mapUnitByCode,leaderSkills,Unit.SKILL_LEADER);
            List<Dungeon> dungeons=generateDungeonsData(workbook.getSheet(SHEET_DUNGEONS),mapUnitByCode);
            dumpUnitsToFile(units);
            dumpDungeonToFile(dungeons);
            dumpSkillsToFile(basicSkills,"Basic");
            dumpSkillsToFile(leaderSkills,"Leader");
        }catch(InvalidFormatException|IOException ioe){
            Logger.getLogger(DataExtractor.class.getName()).log(Level.SEVERE,null,ioe);
        }
    }
    public Map<String,Unit> getMapUnitByCodeOrENName(Map<String,List<Unit>> units){
        Map<String,Unit> map=new HashMap<>();
        for(List<Unit> l:units.values())
            for(Unit u:l){
                map.put(u.getCodeName(),u);
                map.put(u.getEnglishName(),u);
            }
        return map;
    }
    public Map<String,List<Unit>> generateUnitsData(Sheet sheet){
        Map<String,List<Unit>> units=new HashMap<>();
        if(sheet==null)
            return units;
        Map<Integer,String> headerMap=extractHeaders(sheet);
        for(int i=1;i<=sheet.getLastRowNum();i++){
            Row unitRow=sheet.getRow(i);
            if(unitRow==null)
                continue;
            Unit u=new Unit();
            Iterator<Cell> cellItr=unitRow.cellIterator();
            while(cellItr.hasNext()){
                Cell c=cellItr.next();
                String k=headerMap.get(c.getColumnIndex());
                String v;
                switch(c.getCellType()){
                    case Cell.CELL_TYPE_STRING:
                        v=c.getStringCellValue();
                        break;
                    case Cell.CELL_TYPE_NUMERIC:
                        if(Unit.UNIT_INITIATIVE.equalsIgnoreCase(k))
                            v=String.valueOf(c.getNumericCellValue());
                        else
                            v=String.valueOf((int)c.getNumericCellValue());
                        break;
                    default:
                        v="";
                        break;
                }
                u.addData(k,v);
            }
            List<Unit> eltList=units.getOrDefault(u.getElement(),new ArrayList<>());
            units.putIfAbsent(u.getElement(),eltList);
            eltList.add(u);
        }
        return units;
    }
    private void dumpUnitsToFile(Map<String,List<Unit>> units){
        for(Map.Entry<String,List<Unit>> e:units.entrySet()){
            StringBuilder sb=new StringBuilder();
            sb.append("--[[\n\nBefore editing this page, please read :\n\n\t"
            +"http://dungeon-monsters-rpg.wikia.com/wiki/How_to_contribute\n\n--]]\n"
            +"\n\nreturn {\n");
            e.getValue().stream().forEach((u)->{
                sb.append(u.toString());
            });
            sb.append("}");
            try{
                FileUtils.writeStringToFile(new File(outputDir,"wikia_"+e.getKey()+".lua"),sb.toString(),"UTF-8");
            }catch(IOException ex){
                Logger.getLogger(DataExtractor.class.getName()).log(Level.SEVERE,null,ex);
            }
        }
    }
    private Map<Integer,String> extractHeaders(Sheet sheet){
        Map<Integer,String> headerMap=new HashMap<>();
        Row header=sheet.getRow(0);
        Iterator<Cell> cellItr=header.cellIterator();
        while(cellItr.hasNext()){
            Cell c=cellItr.next();
            String n=cleanUnitsHeaderName(c.getStringCellValue());
            headerMap.put(c.getColumnIndex(),n);
        }
        return headerMap;
    }
    public String cleanUnitsHeaderName(String name){
        if(name==null)
            return "";
        String n=name;
        if(n.matches("[A-Z][A-Z] name"))
            n=n.replaceAll("(.*) (.*)","$2 $1");
        return n.replaceAll("\\s","").toLowerCase();
    }
    private List<Dungeon> generateDungeonsData(Sheet sheet,Map<String,Unit> units){
        List<Dungeon> dungeons=new ArrayList<>();
        Map<Integer,String> headerMap=extractHeaders(sheet);
        for(int i=1;i<=sheet.getLastRowNum();i++){
            Row aRow=sheet.getRow(i);
            if(aRow==null)
                continue;
            Dungeon aDungeon=new Dungeon();
            Iterator<Cell> cellItr=aRow.cellIterator();
            while(cellItr.hasNext()){
                Cell c=cellItr.next();
                String k=headerMap.get(c.getColumnIndex());
                String v=getCellStringValue(c);
                //System.out.println(k+" : "+v);
                switch(k){
                    case Dungeon.BLUE_1:
                    case Dungeon.BLUE_2:
                    case Dungeon.RED_1:
                    case Dungeon.RED_2:
                    case Dungeon.BONUS:
                        addDungeonUnit(aDungeon,k,v,units);
                        break;
                    default:
                        aDungeon.addData(k,v);
                }
            }
            dungeons.add(aDungeon);
        }
        return dungeons;
    }
    private String getCellStringValue(Cell c){
        String v;
        switch(c.getCellType()){
            case Cell.CELL_TYPE_STRING:
                v=c.getStringCellValue();
                break;
            case Cell.CELL_TYPE_NUMERIC:
                v=StringUtils.leftPad(String.valueOf((int)c.getNumericCellValue()),2,"0");
                break;
            default:
                v="";
                break;
        }
        return v;
    }
    public Map<String,Skill> generateOneCategorySkill(Sheet sheet){
        Map<String,Skill> skills=new TreeMap<>();
        for(int i=1;i<=sheet.getLastRowNum();i++){
            Row aRow=sheet.getRow(i);
            Skill aSkill=new Skill();
            aSkill.name=getCellStringValue(aRow.getCell(0));
            aSkill.description=getCellStringValue(aRow.getCell(1));
            skills.put(aSkill.name.toLowerCase(),aSkill);
        }
        return skills;
    }
    private void dumpDungeonToFile(List<Dungeon> dungeons){
        StringBuilder sb=new StringBuilder();
        sb.append("--[[\n\nBefore editing this page, please read :\n\n\t"
        +"http://dungeon-monsters-rpg.wikia.com/wiki/How_to_contribute\n\n--]]\n"
        +"\n\nreturn {\n");
        dungeons.stream().forEach((u)->{
            sb.append(u.toString());
        });
        sb.append("}");
        try{
            FileUtils.writeStringToFile(new File(outputDir,"wikia_dungeons.lua"),sb.toString(),"UTF-8");
        }catch(IOException ex){
            Logger.getLogger(DataExtractor.class.getName()).log(Level.SEVERE,null,ex);
        }
    }
    private void ajustAllUnitsCrossing(Map<String,Unit> mapUnitByCode){
        mapUnitByCode.entrySet().forEach((e)->{
            ajustOneUnit(e.getValue(),mapUnitByCode);
        });
    }
    private void ajustOneUnit(Unit u,Map<String,Unit> mapUnitByCode){
        u.addData(MATERIAL_1,ajustMaterial(u.getData(MATERIAL_1),mapUnitByCode,u));
        u.addData(MATERIAL_2,ajustMaterial(u.getData(MATERIAL_2),mapUnitByCode,u));
        u.addData(MATERIAL_3,ajustMaterial(u.getData(MATERIAL_3),mapUnitByCode,u));
        ajustMorphsFrom(mapUnitByCode.get(u.getData(MORPHS_INTO)),u);
    }
    private String ajustMaterial(String unitCode,Map<String,Unit> mapUnitByCode,Unit usedBy){
        if(StringUtils.isNotEmpty(unitCode)){
            Unit unitMat=mapUnitByCode.get(unitCode.replaceAll("(.*)/(.*)","$2"));
            Validate.notNull(unitMat,"unitcode invalid : "+unitCode);
            unitMat.addUsedBy(usedBy.getCodeName(),usedBy.getElement());
            return unitMat.getElementNCode();
        }
        return "";
    }
    private void ajustMorphsFrom(Unit morphsInto,Unit morphsFrom){
        if(morphsFrom!=null){
            if(morphsInto!=null){
                morphsInto.addData(MORPHS_FROM,morphsFrom.getElementNCode());
                morphsFrom.addUsedBy(morphsInto.getCodeName(),morphsInto.getElement());
            }
            if(StringUtils.isEmpty(morphsFrom.getData(MORPHS_FROM)))
                morphsFrom.addData(MORPHS_FROM,"");
        }
    }
    private void addDungeonUnit(Dungeon aDungeon,String boxType,String unitWithLevel,Map<String,Unit> units){
        String unitCodeOrEn=unitWithLevel.replaceAll("(.*) Lvl\\.(.*)","$1");
        Unit u=units.get(unitCodeOrEn);
        Validate.notNull(u,"Invalid code : ["+unitCodeOrEn+"] from ["+unitWithLevel+"]");
        String level=unitWithLevel.replaceAll("(.*) Lvl\\.(.*)","$2");
        u.addFoundIn(aDungeon,boxType,level);
        aDungeon.addData(boxType,u.getCodeName());
        aDungeon.addData(boxType+"_level",level);
    }
    private void ajustSkills(Map<String,Unit> mapUnitByCode,Map<String,Skill> skills,String skillCategory){
        mapUnitByCode.values().stream().forEach((u)->{
            String s=u.getData(skillCategory).toLowerCase();
            //System.out.println("s : "+s);
            if(StringUtils.isNotEmpty(s)){
                if(skills.get(s) != null)
                    skills.get(s).usedby.add(u.getElementNCode());
                else 
                    System.err.println("Unknown skill : "+s+" for unit : "+u.getElementNCode()+" ; skill cat : "+skillCategory);
            }
        });
    }
    private void dumpSkillsToFile(Map<String,Skill> skills,String category){
        StringBuilder sb=new StringBuilder();
        sb.append("--[[\n\nBefore editing this page, please read :\n\n\t"
        +"http://dungeon-monsters-rpg.wikia.com/wiki/How_to_contribute\n\n--]]\n"
        +"\n\nreturn {\n");
        skills.values().stream().forEach((u)->{
            sb.append(u.toString());
        });
        sb.append("}");
        try{
            FileUtils.writeStringToFile(new File(outputDir,"wikia_skills_"+category+".lua"),sb.toString(),"UTF-8");
        }catch(IOException ex){
            Logger.getLogger(DataExtractor.class.getName()).log(Level.SEVERE,null,ex);
        }
    }
}
