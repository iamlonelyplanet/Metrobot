package com.metrobot;

import java.awt.*;
import java.awt.event.InputEvent;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalTime;
import java.util.*;
import java.util.List;

import javax.sound.sampled.*;

import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.platform.win32.WinDef.RECT;

import static com.metrobot.Buttons.*;

/* Родительский класс для четырёх режимов. Здесь находится набор унифицированных методов.
TODO: надо бы унифицировать параметр в Thread.sleep(200). Разобраться в импортах.
 */

public abstract class BaseBot {

    // === Общее состояние для всех ботов ===
    protected Robot robot;
    protected List<HWND> activeWindows = new ArrayList<>();
    protected boolean silentMode = true;
    protected String botName;
    protected LocalTime startTime;
    protected Counter unificatedCounter;
    protected Map<String, Counter> counters = CounterStorage.loadCounters(Arrays.asList("Арена", "КВ", "Рейд"));

    protected abstract Map<String, Point> getButtonMap();

    // --- Конструкторы ---
    public BaseBot() throws AWTException {
        robot = new Robot();
    }

    public BaseBot(List<HWND> activeWindows) throws AWTException {
        this();
        if (activeWindows != null) this.activeWindows = new ArrayList<>(activeWindows);
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

    // Ожидание времени запуска. Отсчитывает большие промежутки времени, без обновляемого вывода в консоль
    protected void waitUntilStartTime(LocalTime startTime) throws InterruptedException {
        System.out.println("Бот запустится в " + startTime);
        while (LocalTime.now().isBefore(startTime)) {
            Thread.sleep(1000); // Не менять число на переменную, это эталон секунды в счётчике!
        }
    }

    // Разворачиваем все игровые окна, даже незадействованные. Так надо.
    protected void showAllGameWindows() {
        for (HWND hWnd : activeWindows) {
            if (hWnd == null) continue;
            User32.INSTANCE.ShowWindow(hWnd, User32.SW_RESTORE);
            User32.INSTANCE.SetForegroundWindow(hWnd);
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        System.out.println("Развернул окна");
    }


    // Сворачиваем все игровые окна (даже незадействованные), если включён silentMode. После сворачивания до следующего
    // события проходит почти 5 минут, в это время пользователь продолжает заниматься своей работой.
    protected void minimizeAllGameWindows() {
        if (!silentMode) return;
        for (HWND hWnd : activeWindows) {
            if (hWnd == null) continue;
            User32.INSTANCE.ShowWindow(hWnd, User32.SW_MINIMIZE);
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        System.out.println("Свернул окна");
    }

    // Старт любого игрового режима
    protected void startGame() throws InterruptedException {
        waitUntilStartTime(startTime);
        System.out.println("Старт режима " + botName);
        Thread.sleep(PAUSE_SHORT_MS);
        this.unificatedCounter = counters.computeIfAbsent(botName, name -> new Counter(name));
        // TODO изучить Method reference! Прикол про Counter::new == name -> new Counter(name)
    }

    // Конец любого игрового режима, это не bot.stop(). Проверить туннельный режим
    protected void endGame() {
        playFinalSound();
        System.out.println("\nРежим " + botName + " завершён. " +
                "Проведено боёв в автоматическом режиме: " + unificatedCounter.getCount());
    }


    // Два метода для дурного режима про туннели
    //TODO: совместить бы два следующих метода (туннели). Но надо курить игровую механику.
    protected void fightSpiders(int tunnelMonsters) throws InterruptedException {
        Thread.sleep(PAUSE_TUNNEL_MS);
//        clickAllWindows("Питомец"); // опционально
        clickAllWindows("Пропустить");
        Thread.sleep(PAUSE_LONG_MS);
        clickAllWindows("Закрыть");
        tunnelMonsters++;
        System.out.println("Убито пауков: " + tunnelMonsters);
        Thread.sleep(PAUSE_TUNNEL_MS);
        clickAllWindows("В туннель");
        Thread.sleep(PAUSE_SHORT_MS);
    }

    protected void fightLizards(int tunnelMonsters) throws InterruptedException {
        Thread.sleep(PAUSE_TUNNEL_MS);
//        clickAllWindows("Питомец"); // опционально
        clickAllWindows("Пропустить");
        Thread.sleep(PAUSE_LONG_MS);
        clickAllWindows("Закрыть");
        tunnelMonsters++;
        System.out.println("Убито ящеров: " + tunnelMonsters);
        Thread.sleep(PAUSE_TUNNEL_MS);
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

    // Единый метод кликов по всем выбранным окнам. Ох, и долго же я его писал и переписывал. Учился. Это самый центр
    // всей проги.
    protected void clickAllWindows(String buttonName) throws InterruptedException {
        Map<String, Point> buttonMap = getButtonMap();
        Point rel = buttonMap.get(buttonName);
        if (rel == null) {
            System.err.println("Кнопка \"" + buttonName + "\" среди кнопок не найдена.");
            return;
        }
        for (int i = 0; i < activeWindows.size(); i++) {
            HWND hWnd = activeWindows.get(i);
            if (hWnd == null) continue;

            int fighterNum = i + 1; // индексация от 1

            RECT rect = new RECT();
            User32.INSTANCE.GetWindowRect(hWnd, rect);

            int x = rect.left + Buttons.xMoveRight + rel.x;
            int y = rect.top + Buttons.yMoveDown + rel.y;

            System.out.printf("Боец %d нажал \"%s\" (%d, %d)%n", fighterNum, buttonName, x, y);
            clickAt(x, y);
            Thread.sleep(400);
        }
        Thread.sleep(PAUSE_SHORT_MS);
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

    // Клик. Собственно, ядро всей программы. Интерфейс? Изучить, подумать. Расширить паузами, сведя их в этот метод.
    protected void clickAt(int x, int y) {
        if (robot == null) return;
        robot.mouseMove(x, y);
        robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
        robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
    }
}
