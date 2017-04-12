package com.biklom.wiki;

import java.util.Arrays;
import java.util.Collection;

public class DataExtractorTest {

    @org.junit.Test
    public void testGenerateData() {
        String data = "C:\\workspaces\\Unit Showcase.xlsx";
        String output = "C:\\workspaces\\csv_wiki";
        Collection<String> exclud = Arrays.asList(new String[]{});
        DataExtractor instance = new DataExtractor(data, output,exclud);
        instance.generateData();
    }
}
