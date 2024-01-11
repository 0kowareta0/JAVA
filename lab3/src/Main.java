import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

            // Шаг 1: Запрос двух строк
            System.out.println("Введите первую строку:");
            String inputString = reader.readLine();

            System.out.println("Введите вторую строку с разделителями:");
            String delimiterString = reader.readLine();

            // Шаг 2: Разбиение первой строки на лексемы с использованием разделителей
            StringTokenizer tokenizer = new StringTokenizer(inputString, delimiterString);
            List<String> tokens = new ArrayList<>();
            while (tokenizer.hasMoreTokens()) {
                tokens.add(tokenizer.nextToken());
            }

            // Шаг 3: Найти лексемы из латинских символов и цифр
            List<String> alphanumericTokens = new ArrayList<>();
            for (String token : tokens) {
                if (token.matches("[a-zA-Z0-9]+")) {
                    alphanumericTokens.add(token);
                }
            }

            // Шаг 4: Найти и обработать лексемы-даты
            SimpleDateFormat sdf = new SimpleDateFormat("MM-HH-ss");
            List<String> dateTokens = new ArrayList<>();
            for (String token : tokens) {
                try {
                    Date date = sdf.parse(token);
                    dateTokens.add(token);
                } catch (ParseException ignored) {
                }
            }

            // Шаг 5: Добавить случайное число после лексемы-даты или в середину строки
            Random random = new Random();
            String randomNum = String.valueOf(random.nextInt(100));
            int index = dateTokens.isEmpty() ? inputString.length() / 2 : tokens.indexOf(dateTokens.get(0));
            inputString = inputString.substring(0, index) + randomNum + inputString.substring(index);

            // Шаг 6: Удалить подстроку (самую большую лексему)
            String largestToken = Collections.max(tokens, Comparator.comparingInt(String::length));
            inputString = inputString.replace(largestToken, "");

            // Шаг 7: Вывести результаты
            System.out.println("Результаты:");
            System.out.println("Лексемы из латинских символов и цифр: " + alphanumericTokens);
            System.out.println("Лексемы-даты: " + dateTokens);
            System.out.println("Строка с добавленным случайным числом и удаленной подстрокой: " + inputString);

            // Шаг 8: Записать результаты в файл
            BufferedWriter writer = new BufferedWriter(new FileWriter("output.txt"));
            writer.write("Лексемы из латинских символов и цифр: " + alphanumericTokens + "\n");
            writer.write("Лексемы-времена: " + dateTokens + "\n");
            writer.write("Строка с добавленным случайным числом и удаленной подстрокой: " + inputString + "\n");
            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
