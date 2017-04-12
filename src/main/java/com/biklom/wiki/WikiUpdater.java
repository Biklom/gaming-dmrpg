package com.biklom.wiki;

import com.biklom.wiki.config.WikiUpdateConfig;
import com.biklom.wiki.objects.SKILL_TYPE;
import com.biklom.wiki.objects.Skill;
import com.biklom.wiki.objects.Unit;
import java.io.Closeable;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.security.auth.login.FailedLoginException;
import javax.security.auth.login.LoginException;
import org.slf4j.LoggerFactory;
import org.wikipedia.Wiki;

public class WikiUpdater implements Closeable {


    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(WikiUpdater.class);

    private final TreeSet<String> existingPages = new TreeSet<>();
    private final Wiki w;

    
    private final WikiUpdateConfig config;
    public WikiUpdater(WikiUpdateConfig initialConfig) {
        config = initialConfig;
        w = new Wiki(config.getHost());
    }

    
    public WikiUpdater login() throws IOException, FailedLoginException {
        w.login(config.getUsername(), config.getPassword());
        return this;
    }

    public WikiUpdater init() throws IOException {
        existingPages.addAll(Arrays.asList(w.listPages("", null, Wiki.MAIN_NAMESPACE)));
        return this;
    }


    public WikiUpdater createMissingLuaModules() {
        return this;
    }
    
    public WikiUpdater updateLuaData() {
        return this;
    }
    
    public WikiUpdater createMissingUnit(Collection<Unit> unitsToUpdate) {
        if (config.isUpdateUnits() && unitsToUpdate != null) {
            unitsToUpdate.stream().forEach(u -> {

                try {
                    String s = "Card/" + u.getElementNCode();
                    if (!existingPages.contains(s)) {
                        LOGGER.info("Updating unit [{}] ...", s);
                        w.edit(s, "{{#invoke:Logic/Units/Card | build_card   }}", "");
                    }
                } catch (IOException | LoginException ex) {
                    Logger.getLogger(WikiUpdater.class.getName()).log(Level.SEVERE, null, ex);
                }
            });
        }

        return this;
    }

    public WikiUpdater createMissingUnitAliases(Collection<Unit> unitsToUpdate) {
        if (config.isUpdateUnits() && unitsToUpdate != null) {
            unitsToUpdate.stream().forEach(u -> {

                String unitLink = "Card/" + u.getElementNCode();
                u.getDistinctNames().stream().forEach(s -> {
                    if (!existingPages.contains(s)) {
                        try {
                            LOGGER.info("Updating unit alias [{}] ...", s);
                            w.edit(s, "#REDIRECT [[" + unitLink + "]]", "creating unit alias");
                        } catch (IOException | LoginException ex) {
                            Logger.getLogger(WikiUpdater.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                });

            });
        }

        return this;
    }

    public WikiUpdater createMissingSkills(Collection<Skill> skills) {
        if (config.isUpdateSkills() && skills != null) {
            skills.stream().forEach((u) -> {
                String s = "Skill/" + SKILL_TYPE.findType(u.getInternalCode()).getRoot() + "/" + u.makeCode();
                if (!existingPages.contains(s)) {
                    try {
                        LOGGER.info("Updating skill [{}] ...", s);
                        w.edit(s, "{{#invoke:Logic/Skills | display_it}}", "");
                    } catch (IOException | LoginException ex) {
                        Logger.getLogger(WikiUpdater.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            });
        }
        return this;
    }

    public WikiUpdater createMissingSkillsAliases(Collection<Skill> skills) {
        if (config.isUpdateSkills() && skills != null) {
            skills.stream().forEach((sk) -> {
                String skillLink = "Skill/" + SKILL_TYPE.findType(sk.getInternalCode()).getRoot() + "/" + sk.makeCode();
                sk.getDistinctNames().stream().forEach(s -> {
                    if (!existingPages.contains(s)) {
                        try {
                            LOGGER.info("Updating skill alias [{}] ...", s);
                            w.edit(s, "#REDIRECT [[" + skillLink + "]]", "creating skill alias");
                        } catch (IOException | LoginException ex) {
                            Logger.getLogger(WikiUpdater.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                });
            });
        }
        return this;
    }
    
    public WikiUpdater createMissingMedias() {
        if(config.isUpdateMedias()){
        
        }
        return this;
    }
            
    public WikiUpdater createMissingDungeons() {
        if(config.isUpdateDungeons()){
        
        }
        return this;
    }
    
    public WikiUpdater logout() {
        w.logout();
        return this;
    }

    @Override
    public void close() throws IOException {
        logout();
    }

}
