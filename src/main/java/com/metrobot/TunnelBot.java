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
 * Допустимы, уже написаны и протестированы сражения с прокачкой питомца: при необходимости убрать коммент в методах
 * fightMonsters и fightMonsters.
 * Самый некрасивый класс. Зато самый лёгкий для обучения, для перегрузки, сигнатур, ООП и многого-прочего.
 * <p>
 * TODO: Переработать ящеров при помощи ООП, либо для начала перегрузкой методов, ибо убийственно раздуто.
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

    int pauseShortForTunnels = PAUSE_SHORT_MS / 2; // TODO: не переделать ли в константу?

    public void start() {
        try {
            startGame();
            Instant startTime = Instant.now(); // Пока надо для таймера, потом можно удалить

            showActiveWindows();
            // === Туннели с пауками ===
            // 10 пауков в туннеле Парк Культуры - Кропоткинская
            clickButton("В туннель");
            Thread.sleep(pauseShortForTunnels);
            for (int way = 0; way < MAX_WAYS_TUNNEL; way++) {
                clickButton("Карта ПК-КРО");
                fightMonsters(unificatedCounter.getCount(), usePet);
                unificatedCounter.plusOne();
                Thread.sleep(pauseShortForTunnels);
                clickButton("Карта КРО-ПК");
                fightMonsters(unificatedCounter.getCount(), usePet);
                unificatedCounter.plusOne();
            }

            // Переход Парк Культуры Красные - Парк Культуры Ганза, однократно
            clickButton("Карта ПКк-ПКг");
            Thread.sleep(PAUSE_SHORT_MS);
            clickButton("Войти с пропуском");
            Thread.sleep(pauseShortForTunnels);

            // 10 пауков в тоннеле Парк Культуры - Киевская
            clickButton("В туннель");
            Thread.sleep(pauseShortForTunnels);
            for (int way = 0; way < MAX_WAYS_TUNNEL; way++) {
                clickButton("Карта ПКг-КИЕ");
                fightMonsters(unificatedCounter.getCount(), usePet);
                unificatedCounter.plusOne();
                clickButton("Карта КИЕ-ПКг");
                fightMonsters(unificatedCounter.getCount(), usePet);
                unificatedCounter.plusOne();
            }

            // Переход Парк Культуры Красные - Парк Культуры Ганза, однократно
            clickButton("Карта ПКг-ПКк");
            Thread.sleep(PAUSE_SHORT_MS);
            clickButton("Войти");
            System.out.println("\nПауки закончились, прибито " + unificatedCounter.getCount() + ". Идём к ящерам");

            // Пока надо для таймера, потом можно удалить
            Instant endSpiderTime = Instant.now();
            Duration spidersDuration = Duration.between(startTime, endSpiderTime);
            long secondsSpider = spidersDuration.getSeconds();
            System.out.println("На пауков затрачено " + (secondsSpider / 60) + " мин " + (secondsSpider % 60) + " сек");

            // === Туннели с Ящерами ===
//            unificatedCounter.setCount(0);
            showActiveWindows(); // можно удалить, но лучше оставить для внутреннего тестирования
            Thread.sleep(pauseShortForTunnels);

            for (int way = 0; way < MAX_WAYS_TUNNEL; way++) {
                // 4 ящерицы в тоннеле Парк Культуры - Проспект Вернадского
                clickButton("В туннель");
                Thread.sleep(pauseShortForTunnels);
                clickButton("Карта-ПК-ФРУ");
                fightMonsters(unificatedCounter.getCount(), usePet);
                unificatedCounter.plusOne();
                Thread.sleep(pauseShortForTunnels);
                clickButton("Войти с пропуском");
                Thread.sleep(PAUSE_SHORT_MS);

                clickButton("В туннель");
                Thread.sleep(pauseShortForTunnels);
                clickButton("Карта-КОМ");
                fightMonsters(unificatedCounter.getCount(), usePet);
                unificatedCounter.plusOne();
                Thread.sleep(pauseShortForTunnels);

                clickButton("В туннель");
                Thread.sleep(pauseShortForTunnels);
                clickButton("Карта-УНИ");
                fightMonsters(unificatedCounter.getCount(), usePet);
                unificatedCounter.plusOne();
                Thread.sleep(pauseShortForTunnels);
                clickButton("Войти с пропуском");
                Thread.sleep(PAUSE_SHORT_MS);

                clickButton("В туннель");
                Thread.sleep(pauseShortForTunnels);
                clickButton("Карта-ПВ");
                fightMonsters(unificatedCounter.getCount(), usePet);
                unificatedCounter.plusOne();
                Thread.sleep(pauseShortForTunnels);

                System.out.println("\nЗавершено пробегов до Проспекта Вернадского: " + (way + 1));

                // 4 ящерицы в тоннеле Проспект Вернадского - Парк Культуры
                clickButton("В туннель");
                Thread.sleep(pauseShortForTunnels);
                clickButton("Карта-УНИ");
                fightMonsters(unificatedCounter.getCount(), usePet);
                unificatedCounter.plusOne();

                clickButton("В туннель");
                Thread.sleep(pauseShortForTunnels);
                clickButton("Карта-КОМ");
                fightMonsters(unificatedCounter.getCount(), usePet);
                unificatedCounter.plusOne();
                Thread.sleep(pauseShortForTunnels);
                clickButton("Войти с пропуском");
                Thread.sleep(PAUSE_SHORT_MS);

                clickButton("В туннель");
                Thread.sleep(pauseShortForTunnels);
                clickButton("Карта-КОМ-ФРУ");
                fightMonsters(unificatedCounter.getCount(), usePet);
                unificatedCounter.plusOne();

                clickButton("В туннель");
                Thread.sleep(pauseShortForTunnels);
                clickButton("Карта-ФРУ-ПК");
                fightMonsters(unificatedCounter.getCount(), usePet);
                unificatedCounter.plusOne();
                Thread.sleep(pauseShortForTunnels);
                clickButton("Войти");

                System.out.println("\nЗавершено пробегов до Парка Культуры: " + (way + 1));
                Thread.sleep(pauseShortForTunnels);
            }

            minimizeActiveWindows();

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
