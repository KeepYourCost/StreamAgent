package core.file;

public class FilePathConverter {
    public static String convertSafetyPath(String filePath) {
        final String fileSeparator = System.getProperty("file.separator");

        if (fileSeparator.equals("/")) {
            return filePath.replace("\\", fileSeparator);
        }
        if (fileSeparator.equals("\\")) {
            return filePath.replace("/", fileSeparator);
        }
        return filePath;
    }
}
