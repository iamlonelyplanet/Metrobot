package com.metrobot;

import java.awt.*;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

import static com.metrobot.Buttons.*;
/* Режим "Клановые войны": бои перса в коллективной ("клановой") движухе.
Вручную занимало у пользователей до 1 часа, раз в 5 минут требуя внимания, притом сильно требуя: коллектив же.

Полное прохождение: тоже до 1 часа, но автоматически.
В этом режиме работает silent mode: окна разворачиваются перед серией кликов, затем сворачиваются обратно.
Повседневная работа пользователей в Windows прерывается всего на 10-12 секунд. Счётчик режима записывается в файл.

Приличное количество методов для трёх классов-ботов унифицировано и вынесено в родительский BaseBot.

TODO: совместить 3 основных класса (боты Арена, КВ и Рейд) в единый. ООП же!
 */

public class RaidBot extends BaseBot {

    public RaidBot(List<Integer> windows, LocalTime timeHHmm, String botName) {
        super(windows);
        this.startTime = timeHHmm;
        this.botName = botName;
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
                showAllGameWindows();
                clickAllWindows("Клан");
                clickAllWindows("Война");
                clickAllWindows("Обновить");
                Thread.sleep(PAUSE_LONG_MS);
                clickAllWindows("Рейды");
                Thread.sleep(PAUSE_SHORT_MS);
            }

            // Бои
            for (int battle = (unificatedCounter.getCount() + 1); battle <= MAX_BATTLES_RAID; battle++) {
                System.out.println("\n=== Бой " + battle + " из " + MAX_BATTLES_RAID + " ===");
                showAllGameWindows();
                Thread.sleep(PAUSE_SHORT_MS);

                // TODO: Обдумать 2 нижние строки на предмет ненужного/вредного повтора при battle == 1
                clickAllWindows("Клан");
                clickAllWindows("Рейды");

                clickAllWindows("Атаковать");
                Thread.sleep(PAUSE_BEFORE_BOSS_MS);
                clickAllWindows("Пропустить");
                Thread.sleep(PAUSE_LONG_MS);
                clickAllWindows("Закрыть");
                minimizeAllGameWindows();

                unificatedCounter.plusOne();
                CounterStorage.saveCounters(counters);
                System.out.println(Grammar.getWordEnd(unificatedCounter.getCount()));

                if (battle < MAX_BATTLES_RAID) {
                    countdown(FIVE_MINUTES_PAUSE_SECONDS - activeWindows.size() - 7);
                }
            }
            endGame();
        } catch (Exception e) {
            handleExceptions(e);
        }
    }
}
