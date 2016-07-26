package com.biklom.wikia.objects;

import org.apache.commons.lang3.StringUtils;

public enum SKILL_TYPE {
    SPECIAL("SpecialRule_"),LEADER("Leader_"),BASIC("");

    private final String prefix;
    private SKILL_TYPE(String prefix) {
        this.prefix = prefix;
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
