/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.biklom.wikia;
import java.util.regex.*;
/**
 *
 * @author Olivier
 */
public class Tester{
    public static void main(String[] args){
        Pattern pat = Pattern.compile("(.*) Lvl\\.(.*)");
        String u = "Fire/Zombie";
        System.out.println(u.replaceAll("(.*)/(.*)","$2"));
        u = "Fombie cccc";
        System.out.println(u);
        System.out.println(u.replaceAll("(.*)/(.*)","$2"));
        
         u = "Green Vermin Lvl.5";
        System.out.println(u);
        System.out.println(u.replaceAll("(.*) Lvl\\.(.*)","$1"));
        System.out.println(u.replaceAll("(.*) Lvl\\.(.*)","$2"));
        
        System.out.println("Archimedes' Principle".replaceAll("[' ]","_").replaceAll("__+","_"));
        
}
    
}
