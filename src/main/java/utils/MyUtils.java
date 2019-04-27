package main.java.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.FileReader;
import java.io.FileWriter;

public final class MyUtils {
    private static Gson gson =
            new GsonBuilder().excludeFieldsWithoutExposeAnnotation().setPrettyPrinting().create();

    private MyUtils (){}

    public static String convertObjectToJSONStr(Object obj){
        String jsonInString = gson.toJson(obj);
        return jsonInString;
    }

    public static Object loadConfigFromFile(Class<?> type, String filePath){
        Object config = null;
        try{
            FileReader fr = new FileReader(filePath);
            config = gson.fromJson(fr, type);
            fr.close();
        }catch (Exception e){
            System.out.println(e);
        }
        return config;
    }

    public static void saveConfigToFile(Object config,String filePath){
        if (config == null)
            return;
        try{
            FileWriter fw = new FileWriter(filePath);
            gson.toJson(config, fw);
            fw.close();
        }catch (Exception e){
            System.out.println(e);
        }
    }
}
