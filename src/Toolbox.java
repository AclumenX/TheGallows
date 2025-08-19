import java.io.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.ThreadLocalRandom;

public class Toolbox {
    static public String cutOffSmallWords(String pathToFile) throws IOException {
        java.nio.charset.Charset cs = java.nio.charset.StandardCharsets.UTF_8;
        java.nio.file.Path input = java.nio.file.Paths.get(pathToFile);
        if (!java.nio.file.Files.exists(input)) {
            throw new FileNotFoundException("Исходный файл не найден: " + pathToFile);
        }

        java.nio.file.Path dir = input.getParent();
        if (dir == null) dir = java.nio.file.Paths.get(".");

        java.nio.file.Path tmp = dir.resolve("new_file.txt");

        java.util.List<String> filtered;
        try (java.util.stream.Stream<String> lines = java.nio.file.Files.lines(input, cs)) {
            filtered = lines
                    .map(s -> s == null ? "" : s.replace("\r", "").trim())
                    .filter(s -> !s.isEmpty())
                    .map(String::toLowerCase)
                    .filter(s -> s.length() >= 4)
                    .distinct()
                    .toList();
        }

        if (filtered.isEmpty()) {
            System.err.println("[Toolbox] Warning: filtering produced 0 words. Leaving original file.");
            try { java.nio.file.Files.deleteIfExists(tmp); } catch (Exception ignore) {}
            return pathToFile;
        }

        java.nio.file.Files.write(tmp, filtered, cs, java.nio.file.StandardOpenOption.CREATE, java.nio.file.StandardOpenOption.TRUNCATE_EXISTING);

        java.nio.file.Path finalPath = dir.resolve("new_file.txt");
        try {
            java.nio.file.Files.move(tmp, finalPath, java.nio.file.StandardCopyOption.REPLACE_EXISTING, java.nio.file.StandardCopyOption.ATOMIC_MOVE);
        } catch (java.nio.file.AtomicMoveNotSupportedException ame) {
            java.nio.file.Files.move(tmp, finalPath, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
        }

        System.out.println("[Toolbox] Filtered words written to: " + finalPath.toAbsolutePath() + " (count=" + filtered.size() + ")");
        return finalPath.toString();
    }
    public static void clearConsole() {
        String os = System.getProperty("os.name").toLowerCase();
        try {
            if (os.contains("win")) {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                System.out.print("\u001b[2J\u001b[3J\u001b[H");
                System.out.flush();
                try {
                    new ProcessBuilder("sh", "-c", "clear").inheritIO().start().waitFor();
                } catch (Exception ignore) {}
            }
        } catch (Exception e) {
            System.out.println("\n".repeat(80));
        }
    }
    static public long getNumberOfRowsInFile(String pathToFile) {
        try {
            return Files.readAllLines(Paths.get(pathToFile)).size();
        } catch (IOException e) {
            System.err.println("Ошибка: " + e.getMessage());
            return (long)0;
        }
    }
    static public String findDirectoryPath(String pathToFile) {
        int lastBackSlashIndex = pathToFile.lastIndexOf('\\');
        String res = pathToFile.substring(0, lastBackSlashIndex);
        return res;
    }
    static public int randomInRange(int min, int max) {
        return ThreadLocalRandom.current().nextInt(min, max + 1);
    }
    static public String getLine(String pathToFile, int lineIndex) throws IOException {
        return Files.lines(Paths.get(pathToFile))
                .skip(lineIndex)
                .findFirst()
                .orElse("NULL");
    }
}
