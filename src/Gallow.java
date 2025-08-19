import java.io.IOException;
import java.util.*;

public class Gallow {

    private static String secretWord = "";
    private static Map<Character, List<Integer>> letterPos = Map.of();
    public static StringBuilder currentState;
    private static final Scanner scanner = new Scanner(System.in);
    public static final String[] gallowState = new String[]{
            "",
            """
            
            
            
            
            
            __ __\n
            """,
            """
              |
              |
              |
              |
              |
            __|__\n
            """,
            """
            ____________
              |
              |
              |
              |
              |
            __|__\n
            """,
            """
            ____________
              |       |
              |      ( )
              |
              |
              |
            __|__\n
            """,
            """
            ____________
              |       |
              |      (_)
              |      |_|
              |
              |
            __|__\n
            """,
            """
            ____________
              |       |
              |      (_)
              |     /|_|\\
              |      | |
              |
            __|__  ВЫ ПРОИГРАЛИ!\n
            """
    };

    public Gallow(String pathToFile) throws IOException {
        this.secretWord = chooseWord(pathToFile);
        this.letterPos = transformWordToMap(secretWord);
        this.currentState = new StringBuilder("- ".repeat(secretWord.length()));
    }

    public static String getState() {
        return currentState.toString();
    }
    private static Map<Character, List<Integer>> getLetterPositions() {
        return Map.copyOf(letterPos);
    }

    static public boolean isValidLetter(char letter) {
        return letter >= 'А' && letter <= 'Я' || letter == 'Ё';
    }

    private String chooseWord(String pathToFile) throws IOException {
        long count = Toolbox.getNumberOfRowsInFile(pathToFile);
        int chosenRow = Toolbox.randomInRange(0, (int)count);
        return Toolbox.getLine(pathToFile, chosenRow);
    }
    private Map<Character, List<Integer>> transformWordToMap(String word) {
        Map<Character, List<Integer>> letterPos = new HashMap<>();
        for (int i = 0; i < word.length(); i++) {
            char l = Character.toUpperCase(word.charAt(i));
            letterPos.computeIfAbsent(l, k -> new ArrayList<>()).add(i);
        }
        return letterPos;
    }

    public static boolean isWordGuessed() {
        return currentState.indexOf("-") == -1;
    }

    public static boolean checkPresenceOfLetter(char letter) {
        char upperLetter = Character.toUpperCase(letter);
        if(!isValidLetter(upperLetter)) return false;
        Map<Character, List<Integer>> copy = getLetterPositions();
        if (copy.containsKey(upperLetter)) {
            for (int i : copy.get(upperLetter)) {
                currentState.setCharAt(i * 2, upperLetter);
            }
            return true;
        }
        return false;
    }

    static public char getLetterFromUser() {
        while (true) {
            System.out.print("\nВведите букву: ");
            String input = scanner.nextLine().trim();

            if (input.isEmpty()) {
                System.out.println("Ошибка: пустой ввод!");
                continue;
            }

            if (input.length() > 1) {
                System.out.println("Ошибка: введите только одну букву!");
                continue;
            }

            char letter = input.charAt(0);

            if (!isValidLetter(letter)) {
                System.out.println("Ошибка: вводите только буквы!");
                continue;
            }

            return Character.toUpperCase(letter);
        }
    }

    private static void clearConsole() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    public static void startGame(String pathToFile) throws IOException {
        String path = Toolbox.cutOffSmallWords(pathToFile);
        Gallow game = new Gallow(path);
        int mistakeCntr = 0;
        while (mistakeCntr < gallowState.length) {
            char currentLetter = getLetterFromUser();
            clearConsole();
            if (!checkPresenceOfLetter(currentLetter)) {
                mistakeCntr++;
            }
            System.out.println(gallowState[mistakeCntr]);
            System.out.print("Слово: ");
            System.out.println(getState());
            if (isWordGuessed()) {
                System.out.println("Вы отгадали!");
            }
            System.out.println("Загаданным словом было: ");
            System.out.print(secretWord);
        }
    }
}
