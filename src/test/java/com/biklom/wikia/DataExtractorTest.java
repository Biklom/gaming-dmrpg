package com.biklom.wikia;

import java.util.Arrays;
import java.util.Collection;

public class DataExtractorTest {

    @org.junit.Test
    public void testGenerateData() {
        String data = "C:\\workspaces\\Unit Showcase_20160726.xlsx";
        String output = "C:\\workspaces\\csv_wiki";
        Collection<String> exclud = Arrays.asList(new String[]{"441","435","436","440","439","437"});
        DataExtractor instance = new DataExtractor(data, output,exclud);
        instance.generateData();
    }
}
