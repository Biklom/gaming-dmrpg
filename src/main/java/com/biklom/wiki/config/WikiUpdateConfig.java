package com.biklom.wiki.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 *
 * @author Olivier
 */
@Configuration
@PropertySource("classpath:config.properties")
@Getter
public class WikiUpdateConfig {

    @Value("${wiki.host}")
    private String host;

    @Value("${wiki.username}")
    private String username;

    @Value("${wiki.password}")
    @Setter
    private String password;

    @Value("${wiki.media.path}")
    private String mediaPath;

    @Value("${wiki.workDir}")
    private String workDir;

    @Value("${wiki.update.medias}")
    private boolean updateMedias;

    @Value("${wiki.update.units}")
    private boolean updateUnits;

    @Value("${wiki.update.skills}")
    private boolean updateSkills;

    @Value("${wiki.update.dungeons}")
    private boolean updateDungeons;

}
