package com.biklom.wiki.objects;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

public enum SKILL_TYPE {
    SPECIAL("SpecialRule_","SpecialRule"),LEADER("Leader_","Leader"),BASIC("","Basic");

    @Getter
    private final String prefix;
    @Getter
    private final String root;
    
    private SKILL_TYPE(String prefix,String root) {
        this.prefix = prefix;
        this.root = root;
    }
    
    public static SKILL_TYPE findType(String skillName) {        
        if(StringUtils.startsWith(skillName, SPECIAL.prefix)) {
            return SPECIAL;
        }
        if(StringUtils.startsWith(skillName, LEADER.prefix)) {
            return LEADER;
        }
        return BASIC;
    }
    
}
