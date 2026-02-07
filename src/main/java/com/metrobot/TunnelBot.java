package com.metrobot;

import java.awt.*;
import java.time.*;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.sun.jna.platform.win32.WinDef.HWND;


import static com.metrobot.Buttons.*;

/**
 * Режим "Туннели": ежедневная прогулка перса по туннелям с особыми монстрами: Ящерами и Пауками. Полное прохождение:
 * порядка 45 минут (23 при VIP, 12 при VIP + скоростной одежде).
 * В этом режиме окна не сворачиваются, работа пользователя в Windows крайне нежелательна/почти недопустима.
 * Запись счётчика в файл здесь не нужна.
 * Самый некрасивый класс. Зато самый лёгкий для обучения, для перегрузки, сигнатур, ООП и многого-прочего.
 * TODO: Переработать ящеров при помощи ООП, либо для начала перегрузкой методов, ибо убийственно раздуто.
 * Текст закинуть в единый метод. Подумать о паузах по команде пользователя.
 */

public class TunnelBot extends BaseBot {
    public TunnelBot(List<HWND> windows,
                     LocalTime timeHHmm,
                     String botName,
                     boolean usePet, boolean closeAfterFinish)
            throws AWTException {

        super(windows);

        {
            this.startTime = timeHHmm;
            this.botName = botName;
            this.usePet = usePet;
            this.closeAfterFinish = closeAfterFinish;
        }
    }

    @Override
    protected Map<String, Point> getButtonMap() {
        return TUNNEL_BUTTONS;
    }
    private enum MonsterKind {SPIDER, LIZARD}

    public void start() {
        try {
            startGame();

            Instant startTime = Instant.now(); // Пока надо для таймера, потом можно удалить
            MonsterKind spider = MonsterKind.SPIDER;
            MonsterKind lizard = MonsterKind.LIZARD;

            showActiveWindows();
            // === Туннели с пауками ===
            // 10 пауков в туннеле Парк Культуры - Кропоткинская
            intoTunnel();

            for (int way = 0; way < MAX_WAYS_TUNNEL; way++) {
                clickButton("Карта ПК-КРО");
                fightMonsters(spider, usePet);
                Thread.sleep(PAUSE_SHORT_TUNNELS_MS);
                clickButton("Карта КРО-ПК");
                fightMonsters(spider, usePet);
            }

            // Переход Парк Культуры Красные - Парк Культуры Ганза, однократно
            clickButton("Карта ПКк-ПКг");
            enterStation("Войти с пропуском", PAUSE_SHORT_MS);

            // 10 пауков в тоннеле Парк Культуры - Киевская
            intoTunnel();

            for (int way = 0; way < MAX_WAYS_TUNNEL; way++) {
                clickButton("Карта ПКг-КИЕ");
                fightMonsters(spider, usePet);
                clickButton("Карта КИЕ-ПКг");
                fightMonsters(spider, usePet);
            }

            // Переход Парк Культуры Красные - Парк Культуры Ганза, однократно
            clickButton("Карта ПКг-ПКк");
            enterStation("Войти", PAUSE_SHORT_MS);

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
                intoTunnel("Карта-ПК-ФРУ");
                fightMonsters(lizard, usePet);
                enterStation("Войти с пропуском", PAUSE_SHORT_TUNNELS_MS);

                intoTunnel("Карта-КОМ");
                fightMonsters(lizard, usePet);
                Thread.sleep(PAUSE_SHORT_TUNNELS_MS);

                intoTunnel("Карта-УНИ");
                fightMonsters(lizard, usePet);
                enterStation("Войти с пропуском", PAUSE_SHORT_TUNNELS_MS);

                intoTunnel("Карта-ПВ");
                fightMonsters(lizard, usePet);
                Thread.sleep(PAUSE_SHORT_TUNNELS_MS);

                System.out.printf("Завершено пробегов до Проспекта Вернадского: %d\n\n", (way + 1));

                // 4 ящерицы в тоннеле Проспект Вернадского - Парк Культуры
                intoTunnel("Карта-УНИ");
                fightMonsters(lizard, usePet);

                intoTunnel("Карта-КОМ");
                fightMonsters(lizard, usePet);
                enterStation("Войти с пропуском", PAUSE_SHORT_TUNNELS_MS);

                intoTunnel("Карта-КОМ-ФРУ");
                fightMonsters(lizard, usePet);

                intoTunnel("Карта-ФРУ-ПК");
                fightMonsters(lizard, usePet);
                enterStation("Войти", PAUSE_SHORT_TUNNELS_MS);

                System.out.printf("\nЗавершено пробегов до Парка Культуры: %d\n\n", (way + 1));
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

    // Бои с туннельными монстрами
    private void fightMonsters(MonsterKind kind, boolean usePet) throws InterruptedException {
        Thread.sleep(PAUSE_TUNNEL_MS);
        if (usePet)
            clickButton("Питомец");
        clickButton("Пропустить");
        clickButton("Закрыть");
        unifiedCounter.plusOne();
        if (kind == MonsterKind.SPIDER) {
            System.out.printf("Убито пауков: %d%n%n", unifiedCounter.getCount());
            Thread.sleep(PAUSE_TUNNEL_MS);
            clickButton("В туннель");
            Thread.sleep(PAUSE_SHORT_MS);
        } else {
            System.out.printf("Убито ящеров: %d%n%n", unifiedCounter.getCount());
            Thread.sleep(PAUSE_TUNNEL_MS);
        }
    }

    private void intoTunnel(String buttonName) throws InterruptedException {
        clickButton("В туннель");
        Thread.sleep(Buttons.PAUSE_SHORT_TUNNELS_MS);
        clickButton(buttonName);
    }

    private void intoTunnel() throws InterruptedException {
        clickButton("В туннель");
        Thread.sleep(Buttons.PAUSE_SHORT_TUNNELS_MS);
    }

    private void enterStation(String document, int PAUSE) throws InterruptedException {
        Thread.sleep(PAUSE);
        clickButton(document);

        if (Objects.equals(document, "Войти с пропуском")) {
            Thread.sleep(PAUSE_SHORT_TUNNELS_MS);
        }
    }
}
