package com.example.cebuapp.controllers.User.NearbyPlaces;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class JsonParser {
    private HashMap<String,String> parseJsonObject(JSONObject object) {
        HashMap<String, String> dataList = new HashMap<>();
        // get name from object
        try {
            String name = object.getString("name");
            // get latitude from obj
            String latitude = object.getJSONObject("geometry")
                    .getJSONObject("location").getString("lat");
            //get longitude from obj
            String longitude = object.getJSONObject("geometry")
                    .getJSONObject("location").getString("lng");
            // put all values in hashmap
            dataList.put("name", name);
            dataList.put("lat", latitude);
            dataList.put("lng", longitude);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        // return hashmap
        return dataList;
    }

    private List<HashMap<String,String>> parseJsonArray(JSONArray jsonArray) {
        // init hash map list
        List<HashMap<String,String>> dataList = new ArrayList<>();
        for (int i=0; i<jsonArray.length(); i++) {
            try {
                // init hash map
                HashMap<String,String> data = parseJsonObject((JSONObject) jsonArray.get(i));
                // add data in hash map list
                dataList.add(data);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return dataList;
    }

    public List<HashMap<String,String>> parseResult(JSONObject object) {
        // init json array
        JSONArray jsonArray = null;
        // get result jsonArray
        try {
            jsonArray = object.getJSONArray("results");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return parseJsonArray(jsonArray);
    }
}
