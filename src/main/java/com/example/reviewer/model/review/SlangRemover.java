package com.example.reviewer.model.review;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class SlangRemover {
    private static List<String> blackList;
    private static SlangRemover instance;
    private static final String SYMBOLS = "***";

    private SlangRemover() {
        blackList = new ArrayList<>();
        try(Scanner scanner = new Scanner(new File("data/blacklist.txt"))) {
            while(scanner.hasNext()) {
                blackList.add(scanner.next());
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static SlangRemover getInstance() {
        if(instance == null) {
            instance = new SlangRemover();
        }
        return instance;
    }

    public static String removeSlang(String text) {
        for(String word : blackList) {
            text = text.replaceAll(word, SYMBOLS);
        }
        return text;
    }
}
