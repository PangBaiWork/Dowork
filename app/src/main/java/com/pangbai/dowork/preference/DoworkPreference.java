package com.pangbai.dowork.preference;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.pangbai.dowork.tool.Init;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

public class DoworkPreference {
    Context ct;
    public String[][] mExtraKeys;
    public SharedPreferences sharedPreferences;
	public 	String fontSize="cmd_fontSize";



    public DoworkPreference(Context ct) {
        this.ct = ct;
        sharedPreferences = ct.getSharedPreferences("dowork_preference", Context.MODE_PRIVATE);

    }

    public void readkeys() {

        File propsFile = new File(Init.keyPath);
        if (!propsFile.exists())
            Toast.makeText(ct, "无法找到按键文件", Toast.LENGTH_LONG).show();

        Properties props = new Properties();
        try {
            if (propsFile.isFile() && propsFile.canRead()) {
                try (FileInputStream in = new FileInputStream(propsFile)) {
                    props.load(new InputStreamReader(in, StandardCharsets.UTF_8));
                }
            }
        } catch (IOException e) {
        }

        try {

            JSONArray arr = new JSONArray(props.getProperty("extra-keys", "[['ESC', 'TAB', 'CTRL', 'ALT', '-', 'DOWN', 'UP']]"));

            // JSONArray arr = new JSONArray(props.getProperty("extra-keys"," ['ESC','<','>','BACKSLASH','=','^','$','()','{}','[]','ENTER'], \ ['TAB','&',';','/','~','%','*','HOME','UP','END','PGUP'], \ ['CTRL','FN','ALT','|','-','+','QUOTE','LEFT','DOWN','RIGHT','PGDN'] \ ]"));
            mExtraKeys = new String[arr.length()][];
            for (int i = 0; i < arr.length(); i++) {
                JSONArray line = arr.getJSONArray(i);
                mExtraKeys[i] = new String[line.length()];
                for (int j = 0; j < line.length(); j++) {
                    mExtraKeys[i][j] = line.getString(j);
                }
            }
        } catch (JSONException e) {
            Toast.makeText(ct, "Could not load the extra-keys property from the config: " + e.toString(), Toast.LENGTH_LONG).show();
            // Log.e("termux", "Error loading props", e);
            mExtraKeys = new String[0][];
        }
    }


    public void setIntStoredAsString(String key, int value, boolean commitToFile) {
        if (sharedPreferences == null) {
            Log.e("preference", "sp error");
            //Logger.logError(, "Ignoring setting int value \"" + value + "\" for the \"" + key + "\" key into null shared preferences.");
            return;
        }

        if (commitToFile)
            sharedPreferences.edit().putString(key, Integer.toString(value)).commit();
        else
            sharedPreferences.edit().putString(key, Integer.toString(value)).apply();
    }

    public int getIntStoredAsString(String key, int def) {
        if (sharedPreferences == null) {
            //	Logger.logError(LOG_TAG, "Error getting int value for the \"" + key + "\" key from null shared preferences. Returning default value \"" + def + "\".");
            Log.e("preference", "sp error");
            return def;
        }

        String stringValue;
        int intValue;

        try {
            stringValue = sharedPreferences.getString(key, Integer.toString(def));
            if (stringValue != null)
                intValue = Integer.parseInt(stringValue);
            else
                intValue = def;
        } catch (NumberFormatException | ClassCastException e) {
            intValue = def;
        }

        return intValue;
    }


    public boolean getBoolStoredAsString(String key, boolean def) {
        if (sharedPreferences == null) {
            //	Logger.logError(LOG_TAG, "Error getting int value for the \"" + key + "\" key from null shared preferences. Returning default value \"" + def + "\".");
            Log.e("preference", "sp error");
            return def;
        }

        return  sharedPreferences.getBoolean(key, def);
    }


}
