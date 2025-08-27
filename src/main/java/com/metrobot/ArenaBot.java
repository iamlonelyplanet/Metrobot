package com.metrobot;

import java.awt.*;
import java.util.List;
import java.util.Map;

import static com.metrobot.WindowConfig.PAUSE_MS;

public class ArenaBot extends BaseBot {

    private static final int TOTAL_BATTLES = 50;
    private static final long FIVE_MINUTES_PAUSE_SECONDS = 285; // 4:45

    public ArenaBot(List<Integer> windows) {
        super(windows); // важно: заполняем базовые windows/windowsMap
    }

    @Override
    protected Map<String, Point> getButtonMap() {
        return WindowConfig.ARENA_BUTTONS;
    }

    public void start() {
        try {
            System.out.println("Бот запустится через 5 секунд, подготовь окна игры...");
            countdown(5);

            for (int battle = 1; battle <= TOTAL_BATTLES; battle++) {
                System.out.println("\n=== Бой №" + battle + " ===");

                showAllGameWindows();

                clickAllWindows("Арена");
                Thread.sleep(PAUSE_MS);
                clickAllWindows("Атаковать");
                Thread.sleep(PAUSE_MS);
                clickAllWindows("Пропустить бой");
                Thread.sleep(PAUSE_MS);

                clickAllWindows("Забрать коллекцию");
                Thread.sleep(PAUSE_MS);
                clickAllWindows("Закрыть — Победа");
                Thread.sleep(PAUSE_MS);
                clickAllWindows("Закрыть — Поражение");

                System.out.println("Бой " + battle + " завершён.");

                minimizeAllGameWindows();

                if (battle < TOTAL_BATTLES) {
                    System.out.println("Пауза между боями...");
                    countdown(FIVE_MINUTES_PAUSE_SECONDS);
                }
            }

            System.out.println(TOTAL_BATTLES + " боёв завершены.");
        } catch (InterruptedException e) {
            System.out.println("Interrupted — завершаю.");
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
