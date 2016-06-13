package com.biklom.wikia;

public class DataExtractorTest {

    @org.junit.Test
    public void testGenerateData() {
        String data = "C:\\workspaces\\Unit Showcase.xlsx";
        String output = "C:\\workspaces\\csv_wiki";
        int maxUnit = 427;
        DataExtractor instance = new DataExtractor(data, output,maxUnit);
        instance.generateData();
    }
}
