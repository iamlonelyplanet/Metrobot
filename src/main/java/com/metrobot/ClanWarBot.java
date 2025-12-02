package com.metrobot;

import java.awt.*;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

import com.sun.jna.platform.win32.WinDef.HWND;

import static com.metrobot.Buttons.*;

/**
 * Режим "Клановые войны": бои перса в коллективной ("клановой") движухе.
 * Вручную занимало у пользователей порядка 2 часов, раз в 5 минут требуя внимания, притом сильно требуя: коллектив же.
 * <p>
 * Полное прохождение: тоже 2 часа, но полностью автоматически.
 * В режиме "КВ" работает silent mode: окна разворачиваются перед серией кликов, затем сворачиваются обратно.
 * Повседневная работа пользователей в Windows прерывается раз в 5 минут всего на 10-12 секунд.
 * Счётчик режима записывается в файл.
 * <p>
 * Приличное количество методов для трёх классов-ботов унифицировано и вынесено в родительский BaseBot.
 */

public class ClanWarBot extends BaseBot {
    public ClanWarBot(List<HWND> windows, LocalTime timeHHmm, String botName) throws AWTException {
        super(windows);
        {
            this.startTime = timeHHmm;
            this.botName = botName;
        }
    }

    @Override
    protected Map<String, Point> getButtonMap() {
        return Buttons.KV_BUTTONS;
    }

    public void start() {
        try {
            startGame();

            LocalTime endTime = startTime.plusHours(2);
            endTime = endTime.minusSeconds(FIVE_MINUTES_PAUSE_SECONDS); // проверить
            boolean isGameGoingOn = LocalTime.now().isBefore(endTime)
                    && ((unifiedCounter.getCount() < MAX_BATTLES_CW));

            // Бои
            while (isGameGoingOn) {
                System.out.println("\n=== Бой " + (unifiedCounter.getCount() + 1) + " из " + MAX_BATTLES_CW + " ===");
                showActiveWindows();
                clickButton("Клан");
                clickButton("Война");
                clickButton("Атаковать");
                Thread.sleep(PAUSE_LONG_MS);
                clickButton("Пропустить");
                Thread.sleep(PAUSE_LONG_MS);
                clickButton("Закрыть");
                clickButton("Погон");
                clickButton("Погон 2");
                clickButton("Погон - Коллекция");
                minimizeActiveWindows();

                unifiedCounter.plusOne();
                CounterStorage.saveCounters(counters);
                System.out.println(Grammar.getWordEnd(unifiedCounter.getCount()));
                isGameGoingOn = LocalTime.now().isBefore(endTime) && (unifiedCounter.getCount() < MAX_BATTLES_CW);

                if (isGameGoingOn) {
                    countdown(FIVE_MINUTES_PAUSE_SECONDS - activeWindows.size() - 1);
                }
            }

            endGame();
        } catch (Exception e) {
            handleExceptions(e);
        }
    }
}
