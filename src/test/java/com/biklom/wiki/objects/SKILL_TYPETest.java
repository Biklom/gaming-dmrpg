
package com.biklom.wiki.objects;

import com.biklom.wiki.objects.SKILL_TYPE;
import org.junit.Test;
import static org.junit.Assert.*;


public class SKILL_TYPETest {
    

    @Test
    public void testFindTypeBasic() {
        String skillName = "basicSkill";
        SKILL_TYPE expResult = SKILL_TYPE.BASIC;
        SKILL_TYPE result = SKILL_TYPE.findType(skillName);
        assertEquals(expResult, result);
    }
    @Test
    public void testFindTypeLeader() {
        String skillName = "Leader_leaderSkill";
        SKILL_TYPE expResult = SKILL_TYPE.LEADER;
        SKILL_TYPE result = SKILL_TYPE.findType(skillName);
        assertEquals(expResult, result);
    }
    @Test
    public void testFindTypeSpecial() {
        String skillName = "SpecialRule_specialSkill";
        SKILL_TYPE expResult = SKILL_TYPE.SPECIAL;
        SKILL_TYPE result = SKILL_TYPE.findType(skillName);
        assertEquals(expResult, result);
    }

}
