package com.erpy.test;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * Created by baeonejune on 15. 4. 9..
 */
public class JacksonJsonTest {
    public void WeMefParser() throws Exception {
        int count=0;
        ObjectMapper objectMapper = new ObjectMapper();
        List<HashMap<String, String>> data = new ArrayList<HashMap<String, String>>();
        byte[] jsonData = Files.readAllBytes(Paths.get("/Users/baeonejune/work/social_shop/seed/wemef.json"));
        JsonNode rootNode = objectMapper.readTree(jsonData);
        Iterator<JsonNode> iter = rootNode.iterator();
        while(iter.hasNext()) {
            count++;
            JsonNode node = iter.next();
            System.out.println(node.path("deal_id").asText());
            System.out.println(node.path("main_name").asText());
            System.out.println(node.path("img_deal_list2").asText());
            System.out.println(node.path("line_summary").asText());
            System.out.println(node.path("price").asText());
            System.out.println(node.path("price_org").asText());
            System.out.println(node.path("cnt_shopping").asText());
            System.out.println(node.path("update_time").asText());
            System.out.println("["+count+"]====================================");
        }
    }

    public static void main(String args[]) throws Exception {
        JacksonJsonTest wemef = new JacksonJsonTest();
        wemef.WeMefParser();
    }
}
