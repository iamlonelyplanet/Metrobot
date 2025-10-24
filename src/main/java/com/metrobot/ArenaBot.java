package com.metrobot;

import java.awt.*;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

import com.sun.jna.platform.win32.WinDef.HWND;

import static com.metrobot.Buttons.*;

/**
 * Режим "Арена": ежедневные бои перса. Самый первый режим работы программы :-)
 * Вручную занимало у пользователей более 5 часов, раз в 5 минут требуя внимания.
 * <p>
 * Полное прохождение в полностью автоматическом режиме: порядка 4,5 часов = 50 боёв * 5 мин 10 сек = 260 минут.
 * В режиме "Арена" работает silent mode: окна разворачиваются перед серией кликов, затем сворачиваются обратно.
 * Повседневная работа пользователей в Windows прерывается раз в 5 минут всего на 10-12 секунд.
 * Счётчик режима записывается в файл.
 * Написаны и протестированы бои с прокачкой питомца, можно вынести в отдельный режим (либо прямо сюда добавить
 * аргумент).
 * Приличное количество методов для трёх классов-ботов унифицировано и вынесено в родительский BaseBot.
 * <p>
 * TODO: совместить 3 основных класса (боты Арена, КВ и Рейд) в единый. ООП же! Глянуть на "завершение работы" у
 * начстанции.
 */

public class ArenaBot extends BaseBot {
    public ArenaBot(List<HWND> windows, LocalTime timeHHmm, String botName, boolean usePet) throws AWTException {
        super(windows);
        {
            this.startTime = timeHHmm;
            this.botName = botName;
            this.usePet = usePet;
        }
    }

    @Override
    protected Map<String, Point> getButtonMap() {
        return Buttons.ARENA_BUTTONS;
    }

    public void start() {
        try {
            startGame();

            // Бои
            for (int battle = (unificatedCounter.getCount() + 1); battle <= MAX_BATTLES_ARENA; battle++) {
                System.out.println("\n=== Бой " + battle + " из " + MAX_BATTLES_ARENA + " ==="); // глянуть battle = 0
                showActiveWindows();
                clickButton("Клан - Выход");
                clickButton("Арена");
                Thread.sleep(PAUSE_LONG_MS);
                clickButton("Атаковать");
                Thread.sleep(PAUSE_LONG_MS);
                if (usePet) {
                    clickButton("Питомец");
                }
                clickButton("Пропустить");
                Thread.sleep(PAUSE_LONG_MS);
                clickButton("Закрыть — Победа");
                Thread.sleep(PAUSE_SHORT_MS);
                clickButton("Закрыть — Поражение");
                minimizeActiveWindows();

                unificatedCounter.plusOne();
                CounterStorage.saveCounters(counters);
                System.out.println(Grammar.getWordEnd(unificatedCounter.getCount()));

                if (battle < MAX_BATTLES_ARENA) {
                    countdown(FIVE_MINUTES_PAUSE_SECONDS - activeWindows.size() + 1);
                }
            }

            endGame();
        } catch (Exception e) {
            handleExceptions(e);
        }
    }
}
