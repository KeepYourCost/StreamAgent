package spot.service;

import infrastructure.singleton.Injection;
import infrastructure.singleton.Singleton;
import spot.service.vo.TargetFile;
import spot.service.vo.TargetFile.FileType;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Singleton
public class ManifestReader {
    @Injection
    public ManifestReader() {
    }

    public List<TargetFile> read() throws IOException {
        // 환경 변수 "KYC_MANIFEST"의 값을 읽어옴
        String manifestDirPath = System.getenv("KYC_MANIFEST");
        if (manifestDirPath == null || manifestDirPath.isEmpty()) {
            throw new IllegalArgumentException("Not register KYC_MANIFEST");
        }

        File manifestFile = new File(manifestDirPath, "kyc.manifest");

        List<TargetFile> allFiles = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(manifestFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                File file = new File(line);
                if (file.isDirectory()) {
                    // 디렉토리인 경우, 해당 디렉토리의 모든 파일을 재귀적으로 읽기
                    List<TargetFile> filesInDir = readAllFilesFromDir(file);
                    allFiles.addAll(filesInDir);
                    for (TargetFile f : filesInDir) {
                        FileLogger.logTargetFile(f);
                    }
                    continue;
                }

                // 파일인 경우, 리스트에 추가
                TargetFile targetFile = TargetFile.of(file.getAbsolutePath(), FileType.FILE);
                allFiles.add(targetFile);
                FileLogger.logTargetFile(targetFile);
            }
        }

        System.out.printf("[ \u001B[32mTARGET FILES\u001B[0m, Path: \u001B[34m%s\u001B[0m ]%n", manifestFile.getAbsolutePath());
        return allFiles;
    }

    private List<TargetFile> readAllFilesFromDir(File dir) {
        List<TargetFile> files = new ArrayList<>();
        File[] entries = dir.listFiles();
        if (entries != null) {
            for (File entry : entries) {
                if (!entry.isDirectory()) {
                    TargetFile targetFile = TargetFile.of(entry.getAbsolutePath(), FileType.FILE);
                    files.add(targetFile);
                    FileLogger.logTargetFile(targetFile);
                    continue;
                }
                // 디렉토리인 경우, DIR 타입으로 리스트에 추가
                TargetFile dirFile = TargetFile.of(entry.getAbsolutePath(), FileType.DIR);
                files.add(dirFile);
                FileLogger.logTargetFile(dirFile);
                // 하위 디렉토리와 파일들 재귀적으로 읽기
                List<TargetFile> nestedFiles = readAllFilesFromDir(entry);
                files.addAll(nestedFiles);
            }
        }
        return files;
    }
}
