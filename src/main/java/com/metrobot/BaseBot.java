package com.metrobot;

import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;

import com.metrobot.misc.Grammar;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.platform.win32.WinDef.RECT;
import com.sun.jna.platform.win32.WinUser;

import static com.metrobot.Buttons.*;

/**
 * Родительский класс для всех режимов. Полный комплект унифицированных методов.
 */

public abstract class BaseBot {
    // === Общее состояние для всех ботов ===
    protected Robot robot;
    protected List<HWND> activeWindows = new ArrayList<>();
    protected boolean isPet = false;
    protected boolean isCloseAfterFinish;
    protected String botName;
    protected LocalTime startTime;
    protected Counter unifiedCounter;

    private static final User32 USER32 = User32.INSTANCE;
    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HH:mm");

    protected Map<String, Counter> counters = CounterStorage.loadCounters(Arrays.asList("Арена", "КВ", "Рейд"));

    private static final Set<String> LONG_PAUSE_BUTTONS = Set.of(
            "Обновить",
            "Атаковать врага",
            "Пропустить",
            "Атаковать",
            "Арена"
    );

    private static final Set<String> FINAL_BUTTONS = Set.of(
            "Закрыть 2",
            "Крыса",
            "Клан - Выход"
    );

    private static final Set<String> PET_PAUSE_BUTTONS = Set.of(
            "Питомец",
            "Погон 3",
            "Закрыть 1",
            "Нежданчик - снять",
            "Закрыть - Рейд" // Подумать
    );

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

    // Подсчёт и формирование строки с потраченным временем
    protected String printTime(long seconds) {
        return String.format("%d мин %d сек\n", seconds / 60, seconds % 60);
    }

    // Обновление файла счётчиков, вывод в консоль номера боя и потраченного времени (до 0,01 сек)
    protected int fightEnd(Instant battleStartTime) {
        unifiedCounter.plusOne();
        CounterStorage.saveCounters(counters);
        System.out.println(Grammar.getWordEnd(unifiedCounter.getBattleNumber()));

        Duration duration = Duration.between(battleStartTime, Instant.now());
        double secondsForBattle = duration.toMillis() / 1000.0;
        System.out.printf("На бой затрачено %.2f сек%n", secondsForBattle);
        return (int) secondsForBattle;
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

    // Сворачиваем активное окна
    protected void minimizeActiveWindow(HWND hWnd, int i) throws InterruptedException {
        Thread.sleep(PAUSE_BETWEEN_WINDOWS_MS);
        USER32.ShowWindow(hWnd, WinUser.SW_MINIMIZE);
        if (i == activeWindows.size() - 1) {
            System.out.println("Свернул окна");
        }
    }

    // Закрытие окон + подтверждение
    protected void closeActiveWindows() throws InterruptedException {
        System.out.println("\nЗакрываю игровые окна...");

        for (HWND hwnd : activeWindows) {
            if (hwnd == null) continue;
            User32.INSTANCE.PostMessage(hwnd, WinUser.WM_CLOSE, null, null);
            Thread.sleep(PAUSE_SHORT_MS);
            robot.keyPress(KeyEvent.VK_ENTER);
            robot.keyRelease(KeyEvent.VK_ENTER);
            Thread.sleep(PAUSE_SHORT_MS);
        }

        System.out.println("Окна закрыты");
    }

    // Старт игрового режима
    protected void startGame() throws InterruptedException {
        waitUntilStartTime(startTime);
        System.out.printf("\nСтарт режима %s \n", botName);
        Thread.sleep(PAUSE_SHORT_MS);
        this.unifiedCounter = counters.computeIfAbsent(botName, name -> new Counter(name));
        // TODO: изучить Method reference! Прикол про Counter::new == name -> new Counter(name)
    }

    // Завершение игровых режимов, это не bot.stop()
    protected void endGame() throws InterruptedException {
        System.out.printf("\nРежим %s завершён в %s. Проведено боёв в автоматическом режиме: %d\n",
                botName, LocalTime.now().withNano(0), unifiedCounter.getBattleNumber());
        if (isCloseAfterFinish) {
            closeActiveWindows();
//            PlayFinalSound.playFinalSound();
        }
    }

    // Вывод в консоль номера боя + разворачивание активных окон
    protected void printBattleNumber(int battle, int total) throws InterruptedException {
        System.out.printf("\n=== %s: бой %d из %d ===", botName, battle, total);
        showActiveWindows();
    }

    // Выход из режима "Автобой" (при VIP) перед ClanBot. Не сработает, если нижние окна пересекаются с верхними.
    protected void uncheckAutoFight() throws InterruptedException {
        clickButton("Автобой");
        clickButton("Арена - закрыть");
    }

    // Снятие галочки с неожиданного окна (выполнение квестов).
    protected void uncheckUnexpected() throws InterruptedException {
        if (unifiedCounter.getBattleNumber() % 5 == 0) {
            System.out.println("Снимаю галочку с нежданчика после каждого 5го боя, начиная с первого");
            Thread.sleep(PAUSE_SHORT_MS);
            clickButton("Нежданчик - снять");
            clickButton("Нежданчик - закрыть");
        }
    }

    // Единый метод кликов по всем выбранным окнам. Центр всей проги.
    protected void clickButton(String buttonName) throws InterruptedException {
        Map<String, Point> buttonMap = getButtonMap();
        Point rel = buttonMap.get(buttonName);
        if (rel == null) {
            System.err.println("Кнопка \"" + buttonName + "\" среди кнопок не найдена.");
            return;
        }

        for (int i = 0; i < activeWindows.size(); i++) {
            HWND hWnd = activeWindows.get(i);
            if (hWnd == null) continue;
            RECT rect = new RECT();
            USER32.GetWindowRect(hWnd, rect);

            calculateCoordinates(rect, rel, i, buttonName);

            if (FINAL_BUTTONS.contains(buttonName)) {
//                uncheckUnexpected();
                minimizeActiveWindow(hWnd, i);
            }

            if (PET_PAUSE_BUTTONS.contains(buttonName)) {
                Thread.sleep(PAUSE_PET_MS);
            }

            Thread.sleep(100); // пока не менять значение 100
        }

        Thread.sleep(PAUSE_SHORT_TUNNELS_MS);

        if (LONG_PAUSE_BUTTONS.contains(buttonName)) {
            Thread.sleep(PAUSE_LONG_MS);
        }
    }

    // Перегрузка метода для нескольких кнопок подряд (без переключений на другие рабочие окна)
    protected void clickButtons(String... buttonNames) throws InterruptedException {
        if (buttonNames == null || buttonNames.length == 0) {
            return;
        }

        Map<String, Point> buttonMap = getButtonMap();
        String lastButton = buttonNames[buttonNames.length - 1];

        for (int i = 0; i < activeWindows.size(); i++) {
            HWND hWnd = activeWindows.get(i);
            if (hWnd == null) continue;
            RECT rect = new RECT();
            USER32.GetWindowRect(hWnd, rect);

            for (String buttonName : buttonNames) {
                Point rel = buttonMap.get(buttonName);

                if (rel == null) {
                    System.err.println("Кнопка \"" + buttonName + "\" среди кнопок не найдена.");
                    continue;
                }

                calculateCoordinates(rect, rel, i, buttonName);

                if (FINAL_BUTTONS.contains(buttonName)) {
                    minimizeActiveWindow(hWnd, i);
                }

                Thread.sleep(100); // пока не менять значение 100
            }

            if (PET_PAUSE_BUTTONS.contains(lastButton)) {
                Thread.sleep(PAUSE_PET_MS);
            }
        }

        Thread.sleep(PAUSE_SHORT_MS);

        if (LONG_PAUSE_BUTTONS.contains(lastButton)) {
            Thread.sleep(PAUSE_LONG_MS);
        }
    }

    protected void calculateCoordinates(RECT rect, Point rel, int i, String buttonName) {
        int x = rect.left + Buttons.xMoveRight + rel.x;
        int y = rect.top + Buttons.yMoveDown + rel.y;
        clickAt(x, y);
        System.out.printf("Боец %d нажал \"%s\" (%d, %d)%n", i + 1, buttonName, x, y);
    }

    // Обработка исключений. Учебная штука.
    protected void handleExceptions(Exception e) {
        if (e instanceof InterruptedException) {
            System.out.println("Прервано. Завершаю работу");
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

    // Активируем режим "Автобой" на Арене после выполнения других ботов, на будущее
    protected void autoArena() throws InterruptedException {
        getButtonMap();
        if (isCloseAfterFinish) {
            return;
        }

        System.out.println("\nАктивирую галочку Автобой для Арены...\n");
        clickButtons("Арена 2", "Арена");
        clickButton("Автобой");
    }
}