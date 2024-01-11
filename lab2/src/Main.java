import java.util.Scanner;
import java.util.Formatter;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Введите x : ");
        double x = scanner.nextDouble();

        System.out.print("Введите k (натуральное число): ");
        int k = scanner.nextInt();

        double result = calculateCosine(x, k);

        double standardResult = Math.cos(x);

        System.out.println("Результат через ряд Тейлора: " + result);
        System.out.println("Результат через Math.cos(x): " + standardResult);

        Formatter formatter = new Formatter();
        formatter.format("Результат с точностью %d знаков после запятой: %.6f%n", k + 1, result);
        formatter.format("Значение cos(x) с минимальной шириной поля: %.1f\n", result);
        formatter.format("Значение cos(x) с использованием флага 0: %010.4f\n", result);
        formatter.format("Значение cos(x) с использованием флага +: %+f\n", result);
        formatter.format("Значение cos(x) с использованием флага #: %.3f\n", result);
        System.out.println(formatter);

        runTests();
    }

    public static void runTests() {
        double x1 = 0.5;
        int k1 = 3;
        testCosineCalculation(x1, k1);

        double x2 = 1.2;
        int k2 = 5;
        testCosineCalculation(x2, k2);
    }

    public static void testCosineCalculation(double x, int k) {
        double result = calculateCosine(x, k);
        double standardResult = Math.cos(x);

        System.out.println("Testing for x=" + x + ", k=" + k);
        System.out.println("Result through Taylor series: " + result);
        System.out.println("Result through Math.cos(x): " + standardResult);

        if (Math.abs(result - standardResult) < 1e-6) {
            System.out.println("Test passed!\n");
        } else {
            System.out.println("Test failed!\n");
        }
    }

    public static double calculateCosine(double x, int k) {
        double result = 1.0;
        double term = 1.0;
        int sign = -1;

        for (int n = 2; n <= k * 2; n += 2) {
            term *= x * x / (n * (n - 1));
            result += sign * term;
            sign *= -1;
        }

        return result;
    }
}
