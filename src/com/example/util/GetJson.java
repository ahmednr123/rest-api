package com.example.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class GetJson {
    public static String from (String filePath) {
        String content = null;

        try {
            content = new String(Files.readAllBytes(Paths.get(filePath)));
        } catch (IOException e) {
            System.out.println("File " + filePath + " doesExist? " + (new File(filePath)).exists());
            e.printStackTrace();
        }

        return content;
    }
}