package com.biklom.wikia;

public class DataExtractorTest {

    @org.junit.Test
    public void testGenerateData() {
        String data = "F:\\dungeon monsters rpg\\Dungeon Monsters parameters for sending_updated_20160503.xlsx";
        String output = "F:\\dungeon monsters rpg\\csv_wiki";
        DataExtractor instance = new DataExtractor(data, output);
        instance.generateData();
    }
}
