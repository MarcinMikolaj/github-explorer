package com.explorer.infrastructure.utils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class UtilsObjectMapper {

    public static <T> List<T> getInstanceAsList(String json, Class<T> type){
        Type listType = TypeToken.getParameterized(ArrayList.class, type).getType();
        return new Gson().fromJson(json, listType);
    }

}
