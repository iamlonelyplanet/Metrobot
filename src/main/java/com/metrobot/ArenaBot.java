package com.metrobot;

import java.awt.*;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import com.sun.jna.platform.win32.WinDef.HWND;

import static com.metrobot.Buttons.*;

/* Режим "Арена": ежедневные бои перса. Самый первый режим работы программы :-)
Вручную занимало у пользователей порядка 4 часов, раз в 5 минут требуя внимания.

Полное прохождение: порядка 4,5 часов. В этом режиме работает silent mode: окна разворачиваются перед серией кликов,
затем сворачиваются обратно. Повседневная работа пользователей в Windows прерывается всего на 10-12 секунд.
Счётчик режима записывается в файл.
Написаны и протестированы сражения с прокачкой питомца, можно вынести в отдельный режим (либо прямо сюда добавить
аргумент).
Приличное количество методов для трёх классов-ботов унифицировано и вынесено в родительский BaseBot.

TODO: совместить 3 основных класса (боты Арена, КВ и Рейд) в единый. ООП же!
 */

public class ArenaBot extends BaseBot {
    public ArenaBot(List<HWND> windows, LocalTime timeHHmm, String botName) throws AWTException {
        super(windows);
        {
            this.startTime = timeHHmm;
            this.botName = botName;
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
                showAllGameWindows();
                clickAllWindows("Клан - Выход");
                clickAllWindows("Арена");
                Thread.sleep(PAUSE_LONG_MS);
                clickAllWindows("Атаковать");
                Thread.sleep(PAUSE_LONG_MS);
//                clickAllWindows("Питомец"); // Опционально. Можно сделать целый отдельный режим, перегрузкой методов!
                clickAllWindows("Пропустить");
                Thread.sleep(PAUSE_LONG_MS);
                clickAllWindows("Закрыть — Победа");
                Thread.sleep(PAUSE_SHORT_MS);
                clickAllWindows("Закрыть — Поражение");
                minimizeAllGameWindows();

                unificatedCounter.plusOne();
                CounterStorage.saveCounters(counters);
                System.out.println(Grammar.getWordEnd(unificatedCounter.getCount()));

                if (battle < MAX_BATTLES_ARENA) {
                    countdown(FIVE_MINUTES_PAUSE_SECONDS - activeWindows.size());
                }
            }

            endGame();
        } catch (Exception e) {
            handleExceptions(e);
        }
    }
}
