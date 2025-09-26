package com.metrobot;

import java.awt.*;
import java.time.LocalTime;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.time.Instant;

import static com.metrobot.Buttons.*;

/* Режим "Туннели": ежедневная прогулка перса по туннелям с особыми монстрами: Ящерами и Пауками. Полное прохождение:
порядка 45 минут. В этом режиме  окна не сворачиваются, работа пользователя в Windows не допускается. Запись
счётчика в файл здесь не нужна.
Допустимы, уже написаны и протестированы сражения с прокачкой питомца: при необходимости убрать коммент в методах
fightSpiders и fightLizards.

TODO: Переработать ящеров при помощи ООП, либо для начала перегрузкой методов, ибо убийственно раздуто.
 Пауков можно не трогать.
 Текст закинуть в единый метод. Подумать о паузах по команде пользователя.
 */

public class TunnelBot extends BaseBot {
    public TunnelBot(List<Integer> windows, LocalTime timeHHmm, String botName) {
        super(windows);
        this.startTime = timeHHmm;
        this.botName = botName;
    }

    @Override
    protected Map<String, Point> getButtonMap() {
        return Buttons.TUNNEL_BUTTONS;
    }

    int pauseShortForTunnels = PAUSE_SHORT_MS / 2; // TODO: не переделать ли в константу?

    public void start() {
        try {
            startGame();
            Instant startTime = Instant.now(); // Пока надо для таймера, потом можно удалить

            showAllGameWindows();
            // === Туннели с пауками ===
            // 10 пауков в туннеле Парк Культуры - Кропоткинская
            clickAllWindows("В туннель");
            Thread.sleep(pauseShortForTunnels);
            for (int way = 0; way < MAX_WAYS_TUNNEL; way++) {
                clickAllWindows("Карта ПК-КРО");
                fightSpiders(unificatedCounter.getCount());
                unificatedCounter.plusOne();
                clickAllWindows("Карта КРО-ПК");
                fightSpiders(unificatedCounter.getCount());
                unificatedCounter.plusOne();
            }

            // Переход Парк Культуры Красные - Парк Культуры Ганза, однократно
            clickAllWindows("Карта ПКк-ПКг");
            Thread.sleep(PAUSE_SHORT_MS);
            clickAllWindows("Войти с пропуском");
            Thread.sleep(pauseShortForTunnels);

            // 10 пауков в тоннеле Парк Культуры - Киевская
            clickAllWindows("В туннель");
            Thread.sleep(pauseShortForTunnels);
            for (int way = 0; way < MAX_WAYS_TUNNEL; way++) {
                clickAllWindows("Карта ПКг-КИЕ");
                fightSpiders(unificatedCounter.getCount());
                unificatedCounter.plusOne();
                clickAllWindows("Карта КИЕ-ПКг");
                fightSpiders(unificatedCounter.getCount());
                unificatedCounter.plusOne();
            }

            // Переход Парк Культуры Красные - Парк Культуры Ганза, однократно
            clickAllWindows("Карта ПКг-ПКк");
            Thread.sleep(PAUSE_SHORT_MS);
            clickAllWindows("Войти");
            System.out.println("\nПауки закончились, прибито " + unificatedCounter.getCount() + ". Идём к ящерам");

            // Пока надо для таймера, потом можно удалить
            Instant endSpiderTime = Instant.now();
            Duration spidersDuration = Duration.between(startTime, endSpiderTime);
            long secondsSpider = spidersDuration.getSeconds();
            System.out.println("На пауков затрачено: " + (secondsSpider / 60) + " мин " + (secondsSpider % 60) + " сек");

            // === Туннели с Ящерами ===
            showAllGameWindows(); // можно удалить, но лучше оставить для внутреннего тестирования
            Thread.sleep(pauseShortForTunnels);

            for (int way = 0; way < MAX_WAYS_TUNNEL; way++) {
                clickAllWindows("В туннель");
                Thread.sleep(pauseShortForTunnels);
                clickAllWindows("Карта-ПК-ФРУ");
                fightLizards(unificatedCounter.getCount());
                unificatedCounter.plusOne();
                Thread.sleep(pauseShortForTunnels);
                clickAllWindows("Войти с пропуском");
                Thread.sleep(PAUSE_SHORT_MS);

                clickAllWindows("В туннель");
                Thread.sleep(pauseShortForTunnels);
                clickAllWindows("Карта-КОМ");
                fightLizards(unificatedCounter.getCount());
                unificatedCounter.plusOne();
                Thread.sleep(pauseShortForTunnels);

                clickAllWindows("В туннель");
                Thread.sleep(pauseShortForTunnels);
                clickAllWindows("Карта-УНИ");
                fightLizards(unificatedCounter.getCount());
                unificatedCounter.plusOne();
                Thread.sleep(pauseShortForTunnels);
                clickAllWindows("Войти с пропуском");
                Thread.sleep(PAUSE_SHORT_MS);

                clickAllWindows("В туннель");
                Thread.sleep(pauseShortForTunnels);
                clickAllWindows("Карта-ПВ");
                fightLizards(unificatedCounter.getCount());
                unificatedCounter.plusOne();

                System.out.println("\nЗавершено пробегов до Проспекта Вернадского: " + (way + 1));

                clickAllWindows("В туннель");
                Thread.sleep(pauseShortForTunnels);
                clickAllWindows("Карта-УНИ");
                fightLizards(unificatedCounter.getCount());
                unificatedCounter.plusOne();

                clickAllWindows("В туннель");
                Thread.sleep(pauseShortForTunnels);
                clickAllWindows("Карта-КОМ");
                fightLizards(unificatedCounter.getCount());
                unificatedCounter.plusOne();
                Thread.sleep(pauseShortForTunnels);
                clickAllWindows("Войти с пропуском");
                Thread.sleep(PAUSE_SHORT_MS);

                clickAllWindows("В туннель");
                Thread.sleep(pauseShortForTunnels);
                clickAllWindows("Карта-КОМ-ФРУ");
                fightLizards(unificatedCounter.getCount());
                unificatedCounter.plusOne();

                clickAllWindows("В туннель");
                Thread.sleep(pauseShortForTunnels);
                clickAllWindows("Карта-ФРУ-ПК");
                fightLizards(unificatedCounter.getCount());
                unificatedCounter.plusOne();
                Thread.sleep(pauseShortForTunnels);
                clickAllWindows("Войти");

                System.out.println("\nЗавершено пробегов до Парка Культуры: " + (way + 1));
                Thread.sleep(pauseShortForTunnels);
            }

            minimizeAllGameWindows();

            // Пока надо для таймера, потом можно удалить
            Duration lizardDuration = Duration.between(endSpiderTime, Instant.now());
            long secondsLizard = lizardDuration.getSeconds();
            System.out.println("\nНа ящеров затрачено: " + secondsLizard / 60 + " мин " + secondsLizard % 60 + " сек");
            System.out.println("Итого на режим " + botName + " затрачено " +
                    (secondsSpider + secondsLizard) / 60 + " мин " + (secondsSpider + secondsLizard) % 60 + " сек");

            endGame();
        } catch (Exception e) {
            handleExceptions(e);
        }
    }
}
