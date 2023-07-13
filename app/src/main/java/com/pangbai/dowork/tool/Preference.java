package com.pangbai.dowork.tool;

import android.content.Context;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

public class Preference {
	Context ct;
	public String[][] mExtraKeys;

	public Preference(Context ct) {
		this.ct = ct;
	}

	public   void  readkeys() {

		File propsFile = new File("/data/data/com.pangbai.dowork/files/dowork/terminal/keys");
		if (!propsFile.exists())
			Toast.makeText(ct, "无法找到按键文件", Toast.LENGTH_LONG).show();	 

		Properties props = new Properties();
		try {
			if (propsFile.isFile() && propsFile.canRead()) {
				try (FileInputStream in = new FileInputStream(propsFile)) {
					props.load(new InputStreamReader(in, StandardCharsets.UTF_8));
				}
			}
		} catch (IOException e) { }

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

}
