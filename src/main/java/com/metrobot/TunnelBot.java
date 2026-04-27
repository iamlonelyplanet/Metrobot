package com.metrobot;

import java.awt.*;
import java.time.*;
import java.util.List;
import java.util.Map;

import com.sun.jna.platform.win32.WinDef.HWND;


import static com.metrobot.Buttons.*;

/**
 * Режим "Туннели": ежедневная прогулка перса по туннелям с особыми монстрами: Ящерами и Пауками. Полное прохождение:
 * порядка 45 минут (23 при VIP, 12 при VIP + скоростной одежде).
 * В этом режиме окна не сворачиваются, работа пользователя в Windows крайне нежелательна/почти недопустима.
 * Запись счётчика в файл здесь не нужна.
 * Самый некрасивый класс. Зато самый лёгкий для обучения, для перегрузки, сигнатур, ООП и многого-прочего.
 * TODO: Пауза после входа в туннель на Кропоткинскую (готово?)
 * Текст закинуть в единый метод. Подумать о паузах по команде пользователя.
 */

public class TunnelBot extends BaseBot {
    public TunnelBot(List<HWND> windows,
                     LocalTime timeHHmm,
                     String botName,
                     boolean isPet, boolean closeAfterFinish)
            throws AWTException {

        super(windows);

        {
            this.startTime = timeHHmm;
            this.botName = botName;
            this.isPet = isPet;
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
            showActiveWindows();

            // === Туннели с пауками ===
            // 10 пауков в туннеле Парк Культуры - Кропоткинская
            intoTunnel();
            Thread.sleep(PAUSE_MICRO_MS);

            for (int way = 0; way < MAX_WAYS_TUNNEL; way++) {
                clickButton("Карта ПК-КРО");
                fightTunnelMonster(MonsterKind.SPIDER);
                Thread.sleep(PAUSE_SHORT_TUNNELS_MS);
                Thread.sleep(PAUSE_MICRO_MS);
                clickButton("Карта КРО-ПК");
                fightTunnelMonster(MonsterKind.SPIDER);
            }

            // Переход Парк Культуры Красные - Парк Культуры Ганза, однократно
            clickButton("Карта ПКк-ПКг");
            enterStation(true, PAUSE_SHORT_MS);

            // 10 пауков в тоннеле Парк Культуры - Киевская
            intoTunnel();

            for (int way = 0; way < MAX_WAYS_TUNNEL; way++) {
                clickButton("Карта ПКг-КИЕ");
                fightTunnelMonster(MonsterKind.SPIDER);
                clickButton("Карта КИЕ-ПКг");
                fightTunnelMonster(MonsterKind.SPIDER);
            }

            // Переход Парк Культуры Красные - Парк Культуры Ганза, однократно
            clickButton("Карта ПКг-ПКк");
            enterStation(false, PAUSE_SHORT_MS);

            System.out.printf("\nПауки закончились, прибито %d. Идём к ящерам\n", unifiedCounter.getCount());

            // Пока надо для таймера, потом можно удалить
            Instant endSpiderTime = Instant.now();

            Duration spidersDuration = Duration.between(startTime, endSpiderTime);
            long secondsSpider = spidersDuration.getSeconds();
            System.out.printf("На пауков затрачено %d мин %d сек\n", (secondsSpider / 60), (secondsSpider % 60));
            System.out.printf("На пауков затрачено %d мин %d сек\n", (secondsSpider / 60), (secondsSpider % 60));

            // === Туннели с Ящерами ===
            showActiveWindows(); // можно удалить, но лучше оставить для внутреннего тестирования
            unifiedCounter.setCount(0);
            Thread.sleep(PAUSE_SHORT_TUNNELS_MS);

            for (int way = 0; way < MAX_WAYS_TUNNEL; way++) {
                // 4 ящерицы в тоннелях Парк Культуры - Проспект Вернадского
                go("Карта-ПК-ФРУ", MonsterKind.LIZARD, PAUSE_SHORT_TUNNELS_MS, true, 0);
                go("Карта-КОМ", MonsterKind.LIZARD,0);
                go("Карта-УНИ", MonsterKind.LIZARD, PAUSE_SHORT_TUNNELS_MS, true, 0);
                go("Карта-ПВ", MonsterKind.LIZARD, 0);
                System.out.printf("Завершено пробегов до Проспекта Вернадского: %d\n\n", (way + 1));

                // 4 ящерицы в тоннелях Проспект Вернадского - Парк Культуры
                go("Карта-УНИ", MonsterKind.LIZARD, PAUSE_SHORT_MS);
                go("Карта-КОМ", MonsterKind.LIZARD, PAUSE_SHORT_MS, true, PAUSE_SHORT_MS);
                go("Карта-КОМ-ФРУ", MonsterKind.LIZARD, 0);
                go("Карта-ФРУ-ПК", MonsterKind.LIZARD, PAUSE_SHORT_MS, false, PAUSE_SHORT_MS);
                System.out.printf("\nЗавершено пробегов до Парка Культуры: %d\n\n", (way + 1));
            }

            minimizeActiveWindows();

            // Пока надо для таймера, потом можно удалить
            Duration lizardDuration = Duration.between(endSpiderTime, Instant.now());
            long secondsLizard = lizardDuration.getSeconds();
            System.out.printf("На ящеров затрачено: %d мин %d сек", secondsLizard / 60, secondsLizard % 60);
            System.out.printf("Итого на режим %s затрачено %d мин %d сек",
                    botName, (secondsSpider + secondsLizard) / 60, (secondsSpider + secondsLizard) % 60);

            endGame();

        } catch (Exception e) {
            handleExceptions(e);
        }
    }

    private void intoTunnel(String buttonName) throws InterruptedException {
        clickButton("В туннель");
        Thread.sleep(PAUSE_SHORT_TUNNELS_MS);
        clickButton(buttonName);
    }

    private void intoTunnel() throws InterruptedException {
        clickButton("В туннель");
        Thread.sleep(PAUSE_SHORT_TUNNELS_MS);
    }

    private void enterStation(boolean isDocument, int pause) throws InterruptedException {
        Thread.sleep(pause);
        if (isDocument) {
            clickButton("Войти с пропуском");
        } else clickButton("Войти");

        if (isDocument) {
            Thread.sleep(PAUSE_SHORT_TUNNELS_MS);
        }
    }

    // Станции без автовхода (с пропуском и без него)
    private void go(String buttonName,
                    MonsterKind kind,
                    int pauseStationEntrance,
                    boolean isDocument,
                    int additionalPause) throws InterruptedException {
        intoTunnel(buttonName);
        fightTunnelMonster(kind);
        enterStation(isDocument, pauseStationEntrance);
        Thread.sleep(additionalPause);
    }

    // Станции с автовходом
    private void go(String buttonName, MonsterKind kind, int additionalPause) throws InterruptedException {
        intoTunnel(buttonName);
        fightTunnelMonster(kind);

        Thread.sleep(additionalPause);
    }

    // Бои с туннельными монстрами
    private void fightTunnelMonster(MonsterKind kind) throws InterruptedException {
        Thread.sleep(PAUSE_TUNNEL_MS);
        if (isPet)
            clickButton("Питомец");
        clickButton("Пропустить");
        clickButton("Закрыть");
        unifiedCounter.plusOne();
        if (kind == MonsterKind.SPIDER) {
            System.out.printf("Убито пауков: %d%n%n", unifiedCounter.getCount());
            Thread.sleep(PAUSE_TUNNEL_MS);
            Thread.sleep(PAUSE_SHORT_MS);
            clickButton("В туннель");
            Thread.sleep(PAUSE_SHORT_MS);
        } else {
            System.out.printf("Убито ящеров: %d%n%n", unifiedCounter.getCount());
            Thread.sleep(PAUSE_TUNNEL_MS);
        }
    }
}
