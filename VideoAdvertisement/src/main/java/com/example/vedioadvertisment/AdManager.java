package com.example.vedioadvertisment;

import java.io.*;
import java.util.*;

public class AdManager {

    private String baseDir;
    private Random random = new Random();

    public AdManager(String baseDir) {
        this.baseDir = baseDir;
    }

    // 读取 playlist.txt
    public List<String> load(String type, String id) throws Exception {
        List<String> list = new ArrayList<>();
        File file = new File(baseDir + "/" + type + "/" + id + "/playlist.txt");

        BufferedReader reader = new BufferedReader(new FileReader(file));
        String line;
        while((line = reader.readLine()) != null) {
            list.add(line);
        }
        reader.close();
        return list;
    }

    // 随机插播广告
    public List<String> build(String video, String ad) throws Exception {
        List<String> main = load("videos", video);
        List<String> ads = load("ads", ad);
        List<String> result = new ArrayList<>();

        int mode = random.nextInt(3); // 0头 1中 2尾

        if (mode == 0) result.addAll(ads);

        if (mode == 1) {
            int mid = main.size() / 2;
            result.addAll(main.subList(0, mid));
            result.addAll(ads);
            result.addAll(main.subList(mid, main.size()));
            return result;
        }

        result.addAll(main);
        if (mode == 2) result.addAll(ads);

        return result;
    }
}