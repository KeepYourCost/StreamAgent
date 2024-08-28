package spot.service;

import spot.service.vo.TargetFile;

import java.io.File;
import java.util.List;

public class FileLogger {
    private static final String GREEN = "\u001B[32m";
    private static final String BLUE = "\u001B[34m";
    private static final String RESET = "\u001B[0m";

    public static void logTargetFile(TargetFile targetFile) {
        if (targetFile.type() == TargetFile.FileType.DIR) {
            System.out.printf("[ Type: %sDIR%s, Path: %s%s%s ]%n",
                    BLUE, RESET, BLUE, targetFile.path(), RESET);
        } else {
            String fileName = extractFileName(targetFile.path());
            String filePathWithoutFileName = extractFilePath(targetFile.path());
            System.out.printf("[ Type: %sFILE%s, FileName: %s%s%s, Path: %s%s%s ]%n",
                    BLUE, RESET, GREEN, fileName, RESET, BLUE, filePathWithoutFileName, RESET);
        }
    }

    public static void logTargetFiles(List<TargetFile> targetFiles) {
        for (TargetFile targetFile : targetFiles) {
            if (targetFile.type() == TargetFile.FileType.DIR) {
                System.out.printf("[ Type: %sDIR%s, Path: %s%s%s ]%n",
                        BLUE, RESET, BLUE, targetFile.path(), RESET);
                continue;
            }
            String fileName = extractFileName(targetFile.path());
            String filePathWithoutFileName = extractFilePath(targetFile.path());
            System.out.printf("[ Type: %sFILE%s, FileName: %s%s%s, Path: %s%s%s ]%n",
                    BLUE, RESET, GREEN, fileName, RESET, BLUE, filePathWithoutFileName, RESET);
        }
    }

    // 파일명을 추출
    private static String extractFileName(String path) {
        String separator = File.separator;
        return path.substring(path.lastIndexOf(separator) + 1);
    }

    // 파일명을 제외한 경로 추출
    private static String extractFilePath(String path) {
        String separator = File.separator;
        int lastIndex = path.lastIndexOf(separator);
        if (lastIndex == -1) {
            return ""; // 경로에 구분자가 없는 경우 빈 문자열 반환
        }
        return path.substring(0, lastIndex);
    }

    public static void logProgress(int current, int total, String filePath) {
        int progressBarLength = 50;  // 진행 막대의 길이
        int progress = (int) ((double) current / total * progressBarLength);

        StringBuilder progressBar = new StringBuilder("[");
        for (int i = 0; i < progressBarLength; i++) {
            if (i < progress) {
                progressBar.append(GREEN).append("■").append(RESET);
            } else {
                progressBar.append(" ");
            }
        }
        progressBar.append("]");

        System.out.printf("%s %d/%d files processed - Current file: %s%n",
                progressBar.toString(), current, total, filePath);
    }

}

