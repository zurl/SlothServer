/*
 * ©2015-2016 Zhang Chengyi All Rights Reserved.
 */

package Zurl.Sloth.Http;

import Dependency.JSONObject;

import java.io.File;
import java.io.FileReader;

/**
 * Created by zcy on 3/25/2016.
 */
public class HttpServerConfig {
    static JSONObject data;
    public static void initialize(String filename){
        try {
            FileReader fr = new FileReader(filename);

            String text = "";
            char []buf = new char[1024];
            int len = 0;
            while((len = fr.read(buf)) != -1){
                text += new String(buf,0,len);
            }
            data = new JSONObject(text);
        }catch(Exception e){
            System.err.print("fuck");
        }
    }
    public static Object getConfig(String name){
        return data.get(name);
    }

}
