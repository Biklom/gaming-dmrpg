package com.biklom.wikia;

public class DataExtractorTest {

    @org.junit.Test
    public void testGenerateData() {
        String data = "C:\\workspaces\\test_dmrpg.xlsx";
        data = "C:\\workspaces\\Unit Showcase.xlsx";
        String output = "C:\\workspaces\\csv_wiki";
        DataExtractor instance = new DataExtractor(data, output);
        instance.generateData();
    }
}
