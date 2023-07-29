package com.pangbai.dowork.tool;

import android.content.Context;

import com.pangbai.dowork.R;
import com.pangbai.linuxdeploy.ParamUtils;
import com.pangbai.linuxdeploy.PrefStore;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class containerInfor {
    private static Map<String, Integer> iconMap;
    public static List<containerInfor> ctList = new ArrayList<containerInfor>();

    public String name;
    public String version;
    public String methon;
    public int iconId;
    public int size;

    public containerInfor(String methon,String name, String version, int iconId) {
        this.methon=methon;
        this.name = name;
        this.version = version;
        this.iconId = iconId;
    }

    public static List<String> getProfiles(Context c) {
        List<String> profiles = new ArrayList<>();
        File confDir = new File(PrefStore.getEnvDir(c) + "/config");
        File[] profileFiles = confDir.listFiles();

        if (profileFiles != null) {
            for (File profileFile : profileFiles) {
                if (profileFile.isFile()) {
                    String filename = profileFile.getName();
                    int index = filename.lastIndexOf('.');
                    if (index != -1) filename = filename.substring(0, index);
                    profiles.add(filename);
                }
            }
        }

        return profiles;
    }

    public static List<containerInfor> setInforList(List<String> ctName) {
        ctList.clear();
        for (String name:ctName){
            containerInfor infor=setContainerInfor(name);
            if (infor!=null)
                ctList.add(infor);
        }
        return ctList;
    }

    public static containerInfor setContainerInfor(String ctName) {
        File conf = new File(Init.linuxDeployDirPath + "/config/" + ctName + ".conf");
        String version = "Unknown";

        Map<String, String> ctInfor = ParamUtils.readConf(conf);
        if (ctInfor == null)
            return null;
        String ctPath = ctInfor.get("TARGET_PATH");
        String ctMethod=ctInfor.get("METHOD");
        int ctIcon = iconMap.getOrDefault(ctInfor.get("DISTRIB"), R.drawable.ct_icon_linux);
        File osInfor = new File(ctPath + "/etc/os-release");
        if (osInfor.exists()) {
            Map<String, String> osMap = ParamUtils.readConf(osInfor);
            String osName = osMap.get("NAME");
            String osVersion = osMap.get("VERSION");
            version =  osName+osVersion ;
        }

        return new containerInfor(ctMethod,ctName, version, ctIcon);
    }


    static {
        // 在静态代码块中初始化 Map
        iconMap = new HashMap<>();
        iconMap.put("alpine", R.drawable.ct_icon_linux);
        iconMap.put("archlinux", R.drawable.ct_icon_archlinux);
        iconMap.put("centos", R.drawable.ct_icon_centos);
        iconMap.put("debian", R.drawable.ct_icon_debian);
        iconMap.put("fedora", R.drawable.ct_icon_fedora);
        iconMap.put("kali", R.drawable.ct_icon_kali);
        iconMap.put("slackware", R.drawable.ct_icon_linux);
        iconMap.put("ubuntu", R.drawable.ct_icon_ubuntu);
        iconMap.put("docker", R.drawable.ct_icon_linux);
        iconMap.put("rootfs", R.drawable.ct_icon_linux);


        // 添加其他键值对
    }


}
