import java.io.IOException;
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

    private static void showMenu() throws IOException, InterruptedException {
        final Scanner scanner = new Scanner(System.in);
        try {
            String[] cmd = {"/bin/sh", "-c", "stty raw </dev/tty"};
            Runtime.getRuntime().exec(cmd).waitFor();
        } catch (Exception e) {

        }

        while (true) {
            clearConsole();
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
            System.out.println("\nИспользуйте ↑/↓ для выбора, Enter для подтверждения");

            int key = System.in.read();
            if (key == 27) {
                System.in.read();
                int arrow = System.in.read();

                switch (arrow) {
                    case 'A' -> {
                        selectedItem = Math.max(0, selectedItem - 1);
                    }
                    case 'B' -> {
                        selectedItem = Math.min(MENU_ITEMS.length - 1, selectedItem + 1);
                    }
                }
            } else if (key == 10) {
                handleSelection();
                break;
            }
        }

        try {
            String[] cmd = {"/bin/sh", "-c", "stty sane </dev/tty"};
            Runtime.getRuntime().exec(cmd).waitFor();
        } catch (Exception e) {
        }
        scanner.close();
    }

    private static void handleSelection() throws IOException {
        switch (selectedItem) {
            case 0 -> {
                String pathToFile = Toolbox.cutOffSmallWords("C:\\Users\\Alex\\Desktop\\russian_nouns.txt");
                System.out.println("Запуск игры...");
                Gallow.startGame(pathToFile);
            }
            case 1 -> {
                System.out.println("Выход из программы");
                System.exit(0);
            }
        }
    }

    private static void clearConsole() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }
}
