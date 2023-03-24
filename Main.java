import java.io.Serializable;
import java.lang.IllegalStateException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.ClassNotFoundException;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Main {
    public static Counter COUNTER;
    public static final String FILE_TO_SERIALIZE = "counter";

    public static void main(String[] args) {
        CounterHandler handler = new CounterHandler();
        COUNTER = handler.load(FILE_TO_SERIALIZE);
        handler.printInfo();
        Scanner scanner = new Scanner(System.in);
        while (handler.toContinue()) {
            System.out.print(" > ");
            handler.handle(scanner.nextLine(), COUNTER);
        }
        handler.store(COUNTER, FILE_TO_SERIALIZE);
    }
}

class Counter implements Serializable {
    private static final long SerialVersionUID = 1L;
    private int value;

    public void increment() {
        ++value;
    }

    public void reset() {
        value = 0;
    }

    public int getValue() {
        return value;
    }
}

class CounterHandler {
    private boolean continueProcess = true;

    public void handle(String inputCommand, Counter counter) {
        switch (inputCommand) {
            case "/inc" -> {
                counter.increment();
                print("    Счетчик инкрементирован. Текущее значение счетчика - " + counter.getValue());
            }
            case "/reset" -> {
                counter.reset();
                print("    Счетчик обнулен.");
            }
            case "/stop" -> {
                continueProcess = false;
                print("\n    Завершаю работу.");
            }
            default -> print("    Комманда " + inputCommand + " не существует.");
        }
    }

    public Counter load(String filename) {
        try (ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(filename))) {
            Counter counter = (Counter) inputStream.readObject();
            print("\n    Значение счетчика загружено из файла \"" + filename + "\"");
            print("    Текущее значение счетчика - " + counter.getValue());
            return counter;
        } catch (FileNotFoundException e) {
            print("Файл " + filename + " не найден. Счетчик не загружен.");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return new Counter();
    }

    public void store(Counter counter, String filename) {
        try (ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(filename))) {
            outputStream.writeObject(counter);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            print("    Значение счетчика сохранено в файл \"" + filename + "\"\n");
        }
    }

    public boolean toContinue() {
        return continueProcess;
    }

    public void printInfo() {
        print("\n---Поддерживаемые команды---\n" +
                " - /inc - инкрементировать счетчик\n" +
                " - /reset - обнулить счетчик\n" +
                " - /stop - остановить программу\n " +
                "----------------------------");
    }

    private void print(String str) {
        System.out.println(str);
    }
}