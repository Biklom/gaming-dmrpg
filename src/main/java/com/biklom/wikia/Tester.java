/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.biklom.wikia;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.*;

/**
 *
 * @author Olivier
 */
public class Tester {

    public static void main(String[] args) {
        testTRSkill();
    }
    
    private static void test1() {
        Pattern pat = Pattern.compile("(.*) Lvl\\.(.*)");
        String u = "Fire/Zombie";
        System.out.println(u.replaceAll("(.*)/(.*)", "$2"));
        u = "Fombie cccc";
        System.out.println(u);
        System.out.println(u.replaceAll("(.*)/(.*)", "$2"));

        u = "Green Vermin Lvl.5";
        System.out.println(u);
        System.out.println(u.replaceAll("(.*) Lvl\\.(.*)", "$1"));
        System.out.println(u.replaceAll("(.*) Lvl\\.(.*)", "$2"));

        System.out.println("Archimedes' Principle".replaceAll("[' ]", "_").replaceAll("__+", "_"));

    }

    public static void testTRSkill() {
        String desc = "Deals {action}. Wood DP to a random foe for every attack you give.\\nLasts for {duration} attacks.";
        String trad = "Deals 200/500 Wood DP to a random foe for every attack you give. Lasts for 2/6 attacks.";
        List<String> descSplit = Arrays.asList(desc.replaceAll("\\\\n", " ").split("\\s"));
        List<String> tradSplit = Arrays.asList(trad.replaceAll("\\\\n", " ").split("\\s"));
        List<String> comm = new ArrayList<>();
        comm.addAll(descSplit);
        comm.removeAll(tradSplit);
        List<String> tok = new ArrayList<>();
        tok.addAll(tradSplit);
        tok.removeAll(descSplit);
        System.out.println(comm);
        System.out.println(tok);
        String tradIt = "Infligge {action} DP Legno a un avversario a caso ad ogni tuo attacco.\\nDura per {duration} attacchi.";
        System.out.println("trad it unmapped : [" + tradIt + "]");
        for (int i = comm.size(); i > 0; i--) {
            tradIt = tradIt.replace(comm.get(i - 1), tok.get(i - 1));
        }
        System.out.println("trad it mapped   : [" + tradIt + "]");
    }

}
