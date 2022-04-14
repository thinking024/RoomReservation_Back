package com.example.roomreservation.util;

import org.springframework.util.FileSystemUtils;

import java.io.File;

public class FileUtil {
    private static final String basePath = "E:\\Java_program\\RoomReservation\\src\\main\\resources\\img\\";

    public static boolean deleteFile(String name) {
        return FileSystemUtils.deleteRecursively(new File(basePath + name));
    }
}
