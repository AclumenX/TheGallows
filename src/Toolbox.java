import java.io.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.ThreadLocalRandom;

public class Toolbox {
    static public String cutOffSmallWords(String pathToFile) throws IOException {
        // Проверка существования исходного файла
        File inputFile = new File(pathToFile);
        if (!inputFile.exists()) {
            throw new FileNotFoundException("Исходный файл не найден: " + pathToFile);
        }

        String temporary_file = findDirectoryPath(pathToFile) + "\\new_file.txt";
        File outputFile = new File(temporary_file);

        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile));
             FileWriter writer = new FileWriter(outputFile)) {

            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().length() >= 3) {
                    writer.write(line + System.lineSeparator());
                }
            }
            return temporary_file;

        } catch (IOException e) {
            System.err.println("Ошибка: " + e.getMessage());
            if (outputFile.exists()) outputFile.delete();
            return null;
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
