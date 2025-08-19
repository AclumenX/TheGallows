import java.io.IOException;
import java.util.*;

public class Gallow {
    private String secretWord = "";
    private Map<Character, List<Integer>> letterPos = new HashMap<>();
    public StringBuilder currentState;
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
        String chosen = chooseWord(pathToFile);
        if (chosen == null || chosen.isBlank()) {
            throw new IOException("Не получилось выбрать слово (пустой словарь).");
        }

        this.secretWord = chosen.toUpperCase(Locale.ROOT);
        this.letterPos = transformWordToMap(this.secretWord);

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < this.secretWord.length(); ++i) {
            char c = this.secretWord.charAt(i);
            if (isValidLetter(c)) {
                sb.append("- ");
            } else {
                sb.append(c).append(' ');
            }
        }
        this.currentState = sb;
    }

    public String getState() {
        return currentState.toString();
    }

    private Map<Character, List<Integer>> getLetterPositions() {
        return Map.copyOf(letterPos);
    }

    static public boolean isValidLetter(char letter) {
        char up = Character.toUpperCase(letter);
        return (up >= 'А' && up <= 'Я') || up == 'Ё';
    }

    private String chooseWord(String pathToFile) throws IOException {
        long count = Toolbox.getNumberOfRowsInFile(pathToFile);
        if (count <= 0) {
            throw new IOException("Файл слов пуст или недоступен: " + pathToFile);
        }
        int maxIndex = (int) Math.max(0, count - 1);
        int chosenRow = Toolbox.randomInRange(0, maxIndex);
        return Toolbox.getLine(pathToFile, chosenRow);
    }

    private Map<Character, List<Integer>> transformWordToMap(String word) {
        Map<Character, List<Integer>> letterPosLocal = new HashMap<>();
        for (int i = 0; i < word.length(); i++) {
            char ch = Character.toUpperCase(word.charAt(i));
            if (!isValidLetter(ch)) continue;
            letterPosLocal.computeIfAbsent(ch, k -> new ArrayList<>()).add(i);
        }
        return letterPosLocal;
    }

    public boolean isWordGuessed() {
        return currentState.indexOf("-") == -1;
    }

    public boolean checkPresenceOfLetter(char letter) {
        char upperLetter = Character.toUpperCase(letter);
        if (!isValidLetter(upperLetter)) return false;
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
                System.out.println("Ошибка: вводите только буквы русского алфавита!");
                continue;
            }

            return Character.toUpperCase(letter);
        }
    }

    public static void startGame(String pathToFile) throws IOException {
        String path = Toolbox.cutOffSmallWords(pathToFile);
        Gallow game = new Gallow(path);

        int mistakeCntr = 0;
        final int maxMistakes = gallowState.length - 1;
        Set<Character> guessedLetters = new HashSet<>();

        while (true) {
            Toolbox.clearConsole();
            int displayIndex = Math.min(mistakeCntr, maxMistakes);
            System.out.println(gallowState[displayIndex]);
            System.out.print("Слово: ");
            System.out.println(game.getState());

            if (game.isWordGuessed()) {
                System.out.println("\nВы отгадали! Поздравляем!");
                break;
            }

            if (mistakeCntr >= maxMistakes) {
                System.out.println("\nПревышено количество ошибок. Игра окончена.");
                break;
            }

            char currentLetter = getLetterFromUser();

            if (guessedLetters.contains(currentLetter)) {
                System.out.println("Эта буква уже была. Попробуйте другую.");
                continue;
            }

            guessedLetters.add(currentLetter);

            boolean present = game.checkPresenceOfLetter(currentLetter);
            if (!present) {
                mistakeCntr++;
            }

            if (game.isWordGuessed()) {
                Toolbox.clearConsole();
                System.out.println(gallowState[Math.min(mistakeCntr, maxMistakes)]);
                System.out.println("Слово: " + game.getState());
                System.out.println("\nВы отгадали! Поздравляем!");
                break;
            }

            if (mistakeCntr >= maxMistakes) {
                Toolbox.clearConsole();
                System.out.println(gallowState[maxMistakes]);
                System.out.println("\nВы проиграли!");
                break;
            }
        }

        System.out.println("\nЗагаданное слово: " + game.secretWord);
    }
}
