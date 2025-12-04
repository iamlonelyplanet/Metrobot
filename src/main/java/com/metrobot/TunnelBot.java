package com.metrobot;

import java.awt.*;
import java.time.*;
import java.util.List;
import java.util.Map;

import com.sun.jna.platform.win32.WinDef.HWND;


import static com.metrobot.Buttons.*;

/**
 * Режим "Туннели": ежедневная прогулка перса по туннелям с особыми монстрами: Ящерами и Пауками. Полное прохождение:
 * порядка 45-47 минут. В этом режиме окна не сворачиваются, работа пользователя в Windows крайне нежелательна, \
 * почти недопустима. Запись счётчика в файл здесь не нужна.
 * Самый некрасивый класс. Зато самый лёгкий для обучения, для перегрузки, сигнатур, ООП и многого-прочего.
 * TODO: Переработать ящеров при помощи ООП, либо для начала перегрузкой методов, ибо убийственно раздуто.
 * TODO: Глянуть счётчики ящеров и пауков, чтобы они складывались для "Проведено боёв в автоматическом режиме:"
 * Пауков можно не трогать.
 * Текст закинуть в единый метод. Подумать о паузах по команде пользователя.
 */

public class TunnelBot extends BaseBot {
    public TunnelBot(List<HWND> windows, LocalTime timeHHmm, String botName, boolean usePet) throws AWTException {
        super(windows);
        {
            this.startTime = timeHHmm;
            this.botName = botName;
            this.usePet = usePet;
        }
    }

    @Override
    protected Map<String, Point> getButtonMap() {
        return Buttons.TUNNEL_BUTTONS;
    }

    public void start() {
        try {
            startGame();
            Instant startTime = Instant.now(); // Пока надо для таймера, потом можно удалить

            showActiveWindows();
            // === Туннели с пауками ===
            // 10 пауков в туннеле Парк Культуры - Кропоткинская
            clickButton("В туннель");
            Thread.sleep(Buttons.PAUSE_SHORT_TUNNELS_MS);
            for (int way = 0; way < MAX_WAYS_TUNNEL; way++) {
                clickButton("Карта ПК-КРО");
                fightMonsters(MonsterKind.SPIDER, usePet);
                Thread.sleep(PAUSE_SHORT_TUNNELS_MS);
                clickButton("Карта КРО-ПК");
                fightMonsters(MonsterKind.SPIDER, usePet);
            }

            // Переход Парк Культуры Красные - Парк Культуры Ганза, однократно
            clickButton("Карта ПКк-ПКг");
            Thread.sleep(PAUSE_SHORT_MS);
            clickButton("Войти с пропуском");
            Thread.sleep(PAUSE_SHORT_TUNNELS_MS);

            // 10 пауков в тоннеле Парк Культуры - Киевская
            clickButton("В туннель");
            Thread.sleep(PAUSE_SHORT_TUNNELS_MS);
            for (int way = 0; way < MAX_WAYS_TUNNEL; way++) {
                clickButton("Карта ПКг-КИЕ");
                fightMonsters(MonsterKind.SPIDER, usePet);
                clickButton("Карта КИЕ-ПКг");
                fightMonsters(MonsterKind.SPIDER, usePet);
            }

            // Переход Парк Культуры Красные - Парк Культуры Ганза, однократно
            clickButton("Карта ПКг-ПКк");
            Thread.sleep(PAUSE_SHORT_MS);
            clickButton("Войти");
            System.out.printf("\nПауки закончились, прибито %d. Идём к ящерам\n", unifiedCounter.getCount());

            // Пока надо для таймера, потом можно удалить
            Instant endSpiderTime = Instant.now();
            Duration spidersDuration = Duration.between(startTime, endSpiderTime);
            long secondsSpider = spidersDuration.getSeconds();
            System.out.printf("На пауков затрачено %d мин %d сек\n", (secondsSpider / 60), (secondsSpider % 60));

            // === Туннели с Ящерами ===
            showActiveWindows(); // можно удалить, но лучше оставить для внутреннего тестирования
            unifiedCounter.setCount(0);
            Thread.sleep(PAUSE_SHORT_TUNNELS_MS);

            for (int way = 0; way < MAX_WAYS_TUNNEL; way++) {
                // 4 ящерицы в тоннеле Парк Культуры - Проспект Вернадского
                clickButton("В туннель");
                Thread.sleep(PAUSE_SHORT_TUNNELS_MS);
                clickButton("Карта-ПК-ФРУ");
                fightMonsters(MonsterKind.LIZARD, usePet);
                Thread.sleep(PAUSE_SHORT_TUNNELS_MS);
                clickButton("Войти с пропуском");
                Thread.sleep(PAUSE_SHORT_MS);

                clickButton("В туннель");
                Thread.sleep(PAUSE_SHORT_TUNNELS_MS);
                clickButton("Карта-КОМ");
                fightMonsters(MonsterKind.LIZARD, usePet);
                Thread.sleep(PAUSE_SHORT_TUNNELS_MS);

                clickButton("В туннель");
                Thread.sleep(PAUSE_SHORT_TUNNELS_MS);
                clickButton("Карта-УНИ");
                fightMonsters(MonsterKind.LIZARD, usePet);
                Thread.sleep(PAUSE_SHORT_TUNNELS_MS);
                clickButton("Войти с пропуском");
                Thread.sleep(PAUSE_SHORT_MS);

                clickButton("В туннель");
                Thread.sleep(PAUSE_SHORT_TUNNELS_MS);
                clickButton("Карта-ПВ");
                fightMonsters(MonsterKind.LIZARD, usePet);
                Thread.sleep(PAUSE_SHORT_TUNNELS_MS);

                System.out.printf("Завершено пробегов до Проспекта Вернадского: %d\n", (way + 1));

                // 4 ящерицы в тоннеле Проспект Вернадского - Парк Культуры
                clickButton("В туннель");
                Thread.sleep(PAUSE_SHORT_TUNNELS_MS);
                clickButton("Карта-УНИ");
                fightMonsters(MonsterKind.LIZARD, usePet);

                clickButton("В туннель");
                Thread.sleep(PAUSE_SHORT_TUNNELS_MS);
                clickButton("Карта-КОМ");
                fightMonsters(MonsterKind.LIZARD, usePet);
                Thread.sleep(PAUSE_SHORT_TUNNELS_MS);
                clickButton("Войти с пропуском");
                Thread.sleep(PAUSE_SHORT_MS);

                clickButton("В туннель");
                Thread.sleep(PAUSE_SHORT_TUNNELS_MS);
                clickButton("Карта-КОМ-ФРУ");
                fightMonsters(MonsterKind.LIZARD, usePet);

                clickButton("В туннель");
                Thread.sleep(PAUSE_SHORT_TUNNELS_MS);
                clickButton("Карта-ФРУ-ПК");
                fightMonsters(MonsterKind.LIZARD, usePet);
                Thread.sleep(PAUSE_SHORT_TUNNELS_MS);
                clickButton("Войти");

                System.out.printf("\nЗавершено пробегов до Парка Культуры: %d%n", (way + 1));
                Thread.sleep(PAUSE_SHORT_TUNNELS_MS);
            }

            minimizeActiveWindows();

            // Пока надо для таймера, потом можно удалить
            Duration lizardDuration = Duration.between(endSpiderTime, Instant.now());
            long secondsLizard = lizardDuration.getSeconds();
            System.out.println("На ящеров затрачено: " + secondsLizard / 60 + " мин " + secondsLizard % 60 + " сек");
            System.out.println("Итого на режим " + botName + " затрачено " +
                    (secondsSpider + secondsLizard) / 60 + " мин " + (secondsSpider + secondsLizard) % 60 + " сек");

            endGame();

        } catch (Exception e) {
            handleExceptions(e);
        }
    }
}
