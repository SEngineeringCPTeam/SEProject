package com.example.choppingmobile;

import java.util.HashMap;

public class Product {
    public String name;
    public String id;
    public String category;
    public String option;

    public Product(String _name, String _id, String _category, String _option)
    {
        name=_name;
        id=_id;
        category=_category;
        option=_option;
    }
    public Product(HashMap<String, Object> map)
    {
        name=map.get("name").toString();
        id=map.get("id").toString();
        category=map.get("category").toString();
        option=map.get("option").toString();
    }

}
