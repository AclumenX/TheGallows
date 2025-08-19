import java.io.IOException;
import java.nio.file.Paths;
import java.util.Scanner;

public class ConsoleMenu {
    private static final String[] MENU_ITEMS = {"Начать игру", "Выход"};
    private static int selectedItem = 0;

    public static void startGame() {
        try {
            showMenu();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void setTerminalRaw(boolean raw) {
        String os = System.getProperty("os.name").toLowerCase();
        boolean isWindows = os.contains("win");
        if (isWindows) return;

        String cmd = raw ? "stty raw </dev/tty" : "stty sane </dev/tty";
        try {
            String[] shellCmd = {"/bin/sh", "-c", cmd};
            Runtime.getRuntime().exec(shellCmd).waitFor();
        } catch (Exception e) {
            System.err.println("[ConsoleMenu] Warning: cannot change terminal mode: " + e.getMessage());
        }
    }

    private static void showMenu() throws IOException, InterruptedException {
        Scanner scanner = new Scanner(System.in);
        setTerminalRaw(true);
        try {
            while (true) {
                Toolbox.clearConsole();
                System.out.println("=== ВИСЕЛИЦА ===");
                for (int i = 0; i < MENU_ITEMS.length; i++) {
                    if (i == selectedItem) {
                        System.out.print("> ");
                        System.out.print("\u001B[32m");
                    } else {
                        System.out.print("  ");
                    }
                    System.out.println(MENU_ITEMS[i] + "\u001B[0m");
                }
                System.out.println("\nИспользуйте ↑/↓ или W/S для выбора, Enter для подтверждения");

                int key = System.in.read();
                if (key == 27) {
                    int next = System.in.read();
                    if (next == 91) { // '['
                        int arrow = System.in.read();
                        switch (arrow) {
                            case 65 -> selectedItem = Math.max(0, selectedItem - 1);
                            case 66 -> selectedItem = Math.min(MENU_ITEMS.length - 1, selectedItem + 1);
                        }
                    }
                } else if (key == '\n' || key == '\r') {
                    handleSelection(scanner);
                    break;
                } else if (key == 'w' || key == 'W') {
                    selectedItem = Math.max(0, selectedItem - 1);
                } else if (key == 's' || key == 'S') {
                    selectedItem = Math.min(MENU_ITEMS.length - 1, selectedItem + 1);
                } else {
                }
            }
        } finally {
            setTerminalRaw(false);
        }
    }

    private static void handleSelection(Scanner scanner) {
        switch (selectedItem) {
            case 0 -> {
                System.out.print("Введите путь к файлу слов (Enter — использовать ./russian_nouns.txt): ");
                String userInput = scanner.nextLine().trim();
                String pathToFile;
                if (userInput.isEmpty()) {
                    pathToFile = Paths.get("C:/Users/Alex/Desktop/russian_nouns.txt").toAbsolutePath().toString();
                } else {
                    pathToFile = userInput;
                }

                System.out.println("Файл слов: " + pathToFile);
                try {
                    String filteredPath = Toolbox.cutOffSmallWords(pathToFile);
                    System.out.println("Запуск игры...");
                    Gallow.startGame(filteredPath);
                } catch (IOException e) {
                    System.err.println("Ошибка при обработке словаря: " + e.getMessage());
                }
            }
            case 1 -> {
                System.out.println("Выход из программы");
                try {
                    setTerminalRaw(false);
                } catch (Exception ignored) {
                }
                System.exit(0);
            }
        }
    }
}
