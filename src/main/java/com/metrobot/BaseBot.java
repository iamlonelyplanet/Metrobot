package com.metrobot;

import java.awt.*;
import java.awt.event.InputEvent;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;

import javax.sound.sampled.*;

import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.platform.win32.WinDef.RECT;
import com.sun.jna.platform.win32.WinUser;

import static com.metrobot.Buttons.*;

/**
 * Родительский класс для всех режимов. Полный комплект унифицированных методов.
 * TODO: переработать закрытие окон.
 */

public abstract class BaseBot {
    // === Общее состояние для всех ботов ===
    protected Robot robot;
    protected List<HWND> activeWindows = new ArrayList<>();
    protected boolean isSilentMode = true;
    protected boolean isPet = false;
    protected boolean closeAfterFinish;
    protected String botName;
    protected LocalTime startTime;
    protected Counter unifiedCounter;

    private static final User32 USER32 = User32.INSTANCE;
    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HH:mm");

    protected Map<String, Counter> counters = CounterStorage.loadCounters(Arrays.asList("Арена", "КВ", "Рейд"));

    protected abstract Map<String, Point> getButtonMap();

    protected abstract void playGame();


    // --- Конструкторы ---
    public BaseBot() throws AWTException {
        robot = new Robot();
    }

    public BaseBot(List<HWND> activeWindows) throws AWTException {
        this();
        if (activeWindows != null) {
            this.activeWindows = new ArrayList<>(activeWindows);
        }
    }

    // Таймер (секунды), отсчитывает короткие промежутки времени, выводит в консоль обновление раз в секунду
    protected void countdown(int seconds) throws InterruptedException {
        for (int s = seconds; s > 0; s--) {
            int m = s / 60;
            int ss = s % 60;
            System.out.printf("\rДо следующего боя: %02d:%02d   ", m, ss);
            Thread.sleep(1000); // Не менять число на переменную, это эталон секунды в счётчике!
        }

        System.out.println();
    }

    // Ожидание времени старта. Отсчитывает большие промежутки времени, без обновляемого вывода в консоль
    protected void waitUntilStartTime(LocalTime startTime) throws InterruptedException {
        System.out.printf("\n=== Бот %s запустится в %s ===", botName, startTime.format(TIME_FMT));
        while (LocalTime.now().isBefore(startTime)) {
            Thread.sleep(1000); // Не менять число на переменную, это эталон секунды в счётчике!
        }
    }

    // Разворачиваем активные окна
    protected void showActiveWindows() throws InterruptedException {
        for (HWND hWnd : activeWindows) {
            if (hWnd == null) continue;
            USER32.ShowWindow(hWnd, WinUser.SW_RESTORE);
            Thread.sleep(PAUSE_BETWEEN_WINDOWS_MS);
            USER32.SetForegroundWindow(hWnd);
        }
        System.out.println("\nРазвернул окна");
    }

    protected void closeActiveWindows() throws InterruptedException {
        for (HWND hwnd : activeWindows) {
            if (hwnd == null) continue;
            User32.INSTANCE.PostMessage(hwnd, WinUser.WM_CLOSE, null, null);
            Thread.sleep(3000);
            clickButton("Закрыть окно");
            Thread.sleep(PAUSE_SHORT_MS);
        }
    }

    // Старт игрового режима
    protected void startGame() throws InterruptedException {
        waitUntilStartTime(startTime);
        System.out.printf("\nСтарт режима %s \n", botName);
        Thread.sleep(PAUSE_SHORT_MS);
        this.unifiedCounter = counters.computeIfAbsent(botName, name -> new Counter(name));
        // TODO изучить Method reference! Прикол про Counter::new == name -> new Counter(name)
    }

    // Конец игрового режима, это не bot.stop()
    protected void endGame() throws InterruptedException {
//        playFinalSound(); // Ненужная свистоперделка
        System.out.printf("\nРежим %s завершён в %s. Проведено боёв в автоматическом режиме: %d\n",
                botName, LocalTime.now().withNano(0), unifiedCounter.getCount());
        if (closeAfterFinish) {
            System.out.println("\nЗакрываю игровые окна... это срабатывает не всегда!");
            closeActiveWindows();
        }
    }

    // Вывод в консоль номера боя
    protected void printBattleNumber(int battle, int total) throws InterruptedException {
        System.out.printf("\n=== Бой %d из %d ===", battle, total);
        showActiveWindows();
    }

    // Единый метод кликов по всем выбранным окнам. Центр всей проги.
    protected void clickButton(String buttonName) throws InterruptedException {
        Map<String, Point> buttonMap = getButtonMap();
        Point rel = buttonMap.get(buttonName);
        Set<String> LONG_PAUSE_BUTTONS = Set.of(
                "Обновить",
                "Атаковать врага",
                "Пропустить",
                "Атаковать",
                "Арена"
        );

        Set<String> FINAL_BUTTONS = Set.of(
                "Закрыть — Поражение",
                "Крыса",
                "Клан - Выход"
        );

        Set<String> SHORT_PAUSE_BUTTONS = Set.of(
//                "Закрыть — Поражение",
                "Питомец",
                "Погон 3"
        );

        if (rel == null) {
            System.err.println("Кнопка \"" + buttonName + "\" среди кнопок не найдена.");
            return;
        }

        for (int i = 0; i < activeWindows.size(); i++) {
            HWND hWnd = activeWindows.get(i);
            if (hWnd == null) continue;

            RECT rect = new RECT();
            USER32.GetWindowRect(hWnd, rect);

            int x = rect.left + Buttons.xMoveRight + rel.x;
            int y = rect.top + Buttons.yMoveDown + rel.y;


            System.out.printf("Боец %d нажал \"%s\" (%d, %d)%n", i + 1, buttonName, x, y);
            clickAt(x, y);

            if (FINAL_BUTTONS.contains(buttonName)) {
                Thread.sleep(PAUSE_BETWEEN_WINDOWS_MS);
                USER32.ShowWindow(hWnd, WinUser.SW_MINIMIZE);
                if (i == activeWindows.size() - 1) {
                    System.out.println("Свернул окна");
                }
            }

            if (SHORT_PAUSE_BUTTONS.contains(buttonName)) {
                Thread.sleep(PAUSE_SHORT_MS);
            }

            Thread.sleep(100); // пока не менять значение 100
        }

        Thread.sleep(PAUSE_SHORT_MS);

        if (LONG_PAUSE_BUTTONS.contains(buttonName)) {
            Thread.sleep(PAUSE_LONG_MS);
        }
    }

    // Обработка исключений. Учебная штука.
    protected void handleExceptions(Exception e) {
        if (e instanceof InterruptedException) {
            System.out.println("Прервано — выхожу.");
            Thread.currentThread().interrupt();
        } else {
            e.printStackTrace();
        }
    }

    // Клик. Собственно, ядро всей программы. Расширить паузами?
    protected void clickAt(int x, int y) {
        if (robot == null) return;
        robot.mouseMove(x, y);
        robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
        robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
    }

    /**
     * Метод пока устаревший и не используется. Оставлен для дальнейшей разработки (с версии 1.2.9)
     * Сворачиваем активные окна при silentMode == true. После сворачивания до следующего события проходит 5 минут,
     * в это время пользователь продолжает заниматься своей работой.
     */
    protected void minimizeActiveWindows() throws InterruptedException {
        if (!isSilentMode) return;
        for (HWND hWnd : activeWindows) {
            if (hWnd == null) continue;
            USER32.ShowWindow(hWnd, WinUser.SW_MINIMIZE);
        }
        System.out.println("Свернул окна\n");
    }

    // Проигрываем звук по окончанию режима игры. Бесполезная свистоперделка ради учёбы и пасхалка для олдов.
    protected static void playFinalSound() {
        try (InputStream inputStream = BaseBot.class.getResourceAsStream("/sound.wav")) {
            if (inputStream == null) {
                System.err.println("Файл звука не найден: sound.wav");
                return;
            }
            try (AudioInputStream audioIn = AudioSystem.getAudioInputStream(inputStream)) {
                Clip clip = AudioSystem.getClip();
                clip.open(audioIn);
                clip.start();
            }
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }
}