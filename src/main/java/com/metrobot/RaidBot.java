package com.metrobot;

import java.awt.*;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

import com.sun.jna.platform.win32.WinDef.HWND;

import static com.metrobot.Buttons.*;

/**
 * Режим "Клановые войны": бои перса в коллективной ("клановой") движухе.
 * Вручную занимало у пользователей до 1 часа, раз в 5 минут требуя внимания, притом сильно требуя: коллектив же.
 * <p>
 * Полное прохождение: тоже до 1 часа, но полностью автоматически.
 * В режиме "Рейд" работает silent mode: окна разворачиваются перед серией кликов, затем сворачиваются обратно.
 * Повседневная работа пользователей в Windows прерывается раз в 5 минут всего на 10-12 секунд.
 * Счётчик режима записывается в файл.
 * <p>
 * Приличное количество методов для трёх классов-ботов унифицировано и вынесено в родительский BaseBot.
 * <p>
 * TODO: совместить 3 основных класса (боты Арена, КВ и Рейд) в единый. ООП же!
 */

public class RaidBot extends BaseBot {

    public RaidBot(List<HWND> windows, LocalTime timeHHmm, String botName) throws AWTException {
        super(windows);
        {
            this.startTime = timeHHmm;
            this.botName = botName;
        }
    }

    @Override
    protected Map<String, Point> getButtonMap() {
        return Buttons.RAID_BUTTONS;
    }

    public void start() {
        try {
            startGame();

            // Подготовительные клики (разово, если надо)
            if (unificatedCounter.getCount() == 0) {
                showActiveWindows();
                clickButton("Клан");
                clickButton("Война");
                clickButton("Обновить");
                Thread.sleep(PAUSE_LONG_MS);
                clickButton("Рейды");
                Thread.sleep(PAUSE_SHORT_MS);
            }

            // Бои
            for (int battle = (unificatedCounter.getCount() + 1); battle <= MAX_BATTLES_RAID; battle++) {
                System.out.println("\n=== Бой " + battle + " из " + MAX_BATTLES_RAID + " ===");
                showActiveWindows();
                Thread.sleep(PAUSE_SHORT_MS);

                if (battle != 1) {
                    clickButton("Клан");
                    clickButton("Рейды");
                }

                clickButton("Атаковать");
                Thread.sleep(PAUSE_BEFORE_BOSS_MS);
                clickButton("Пропустить");
                Thread.sleep(PAUSE_LONG_MS);
                clickButton("Закрыть");
                minimizeActiveWindows();

                unificatedCounter.plusOne();
                CounterStorage.saveCounters(counters);
                System.out.println(Grammar.getWordEnd(unificatedCounter.getCount()));

                if (battle < MAX_BATTLES_RAID) {
                    countdown(FIVE_MINUTES_PAUSE_SECONDS - activeWindows.size() - 6);
                }
            }

            endGame();
        } catch (Exception e) {
            handleExceptions(e);
        }
    }
}
