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
 * Самый некрасивый класс. Зато самый лёгкий для обучения, для перегрузки, сигнатур, ООП и многого-прочего, что и
 * произошло с апрельской версии 1.2.9
 * TODO: Паузы второго туннеля с пауками. Текст в единый метод. Подумать о паузах по команде пользователя.
 */

public class TunnelBot extends BaseBot {
    public TunnelBot(List<HWND> windows,
                     LocalTime timeHHmm,
                     String botName,
                     boolean isPet, boolean closeAfterFinish)
            throws AWTException {

        super(windows);

        this.startTime = timeHHmm;
        this.botName = botName;
        this.isPet = isPet;
        this.closeAfterFinish = closeAfterFinish;
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
            Thread.sleep(PAUSE_MICRO_MS);
            for (int way = 0; way < MAX_WAYS_TUNNEL; way++) {
                exitStation("Карта ПК-КРО", MonsterKind.SPIDER, 0);
                exitStation("Карта КРО-ПК", MonsterKind.SPIDER, 0);
            }

            // Переход Парк Культуры Красные - Парк Культуры Ганза, однократно
            changeLine("Карта ПКк-ПКг", true);

            // 10 пауков в тоннеле Парк Культуры - Киевская
            for (int way = 0; way < MAX_WAYS_TUNNEL; way++) {
                exitStation("Карта ПКг-КИЕ", MonsterKind.SPIDER, 0);
                exitStation("Карта КИЕ-ПКг", MonsterKind.SPIDER, 0);
            }

            // Переход Парк Культуры Красные - Парк Культуры Ганза, однократно
            changeLine("Карта ПКг-ПКк", false);

            System.out.printf("\nПауки закончились, прибито %d. Идём к ящерам\n", unifiedCounter.getCount());

            // Пока надо для таймера, потом можно удалить
            Instant endSpiderTime = Instant.now();

            Duration spidersDuration = Duration.between(startTime, endSpiderTime);
            long secondsSpider = spidersDuration.getSeconds();
            System.out.printf("На пауков затрачено: %s", printTime(secondsSpider));

            // === Туннели с Ящерами ===
            showActiveWindows(); // можно удалить, но лучше оставить для внутреннего тестирования
            unifiedCounter.setCount(0);
            Thread.sleep(PAUSE_SHORT_TUNNELS_MS);

            for (int way = 0; way < MAX_WAYS_TUNNEL; way++) {
                // 4 ящерицы в тоннелях Парк Культуры - Проспект Вернадского
                exitStation("Карта-ПК-ФРУ", MonsterKind.LIZARD, PAUSE_SHORT_TUNNELS_MS, true, 0);
                exitStation("Карта-КОМ", MonsterKind.LIZARD, 0);
                exitStation("Карта-УНИ", MonsterKind.LIZARD, PAUSE_SHORT_TUNNELS_MS, true, 0);
                exitStation("Карта-ПВ", MonsterKind.LIZARD, 0);
                System.out.printf("Завершено пробегов до Проспекта Вернадского: %d\n\n", (way + 1));

                // 4 ящерицы в тоннелях Проспект Вернадского - Парк Культуры
                exitStation("Карта-УНИ", MonsterKind.LIZARD, PAUSE_SHORT_MS);
                exitStation("Карта-КОМ", MonsterKind.LIZARD, PAUSE_SHORT_MS, true, PAUSE_SHORT_MS);
                exitStation("Карта-КОМ-ФРУ", MonsterKind.LIZARD, 0);
                exitStation("Карта-ФРУ-ПК", MonsterKind.LIZARD, PAUSE_SHORT_MS, false, PAUSE_SHORT_MS);
                System.out.printf("\nЗавершено пробегов до Парка Культуры: %d\n\n", (way + 1));
            }

            minimizeActiveWindows();

            // Пока надо для таймера, потом можно удалить
            Duration lizardDuration = Duration.between(endSpiderTime, Instant.now());
            long secondsLizard = lizardDuration.getSeconds();
            System.out.printf("На ящеров затрачено: %s", printTime(secondsLizard));
            System.out.printf("Итого на режим %s затрачено %s", botName, printTime(secondsSpider + secondsLizard));

            endGame();

        } catch (Exception e) {
            handleExceptions(e);
        }
    }

    private void enterStation(boolean isDocument, int pause) throws InterruptedException {
        Thread.sleep(pause);
        if (isDocument) {
            clickButton("Войти с пропуском");
            Thread.sleep(PAUSE_SHORT_TUNNELS_MS);
        } else {
            clickButton("Войти");
        }
    }

    // Станции без автовхода (с пропуском и без)
    private void exitStation(String buttonName,
                             MonsterKind kind,
                             int pauseStationEntrance,
                             boolean isDocument,
                             int pauseAfterAction) throws InterruptedException {
        intoTunnel(buttonName);
        fightTunnelMonster(kind);
        enterStation(isDocument, pauseStationEntrance);
        Thread.sleep(pauseAfterAction);
    }

    // Станции с автовходом
    private void exitStation(String buttonName, MonsterKind kind, int pauseAfterAction) throws InterruptedException {
        intoTunnel(buttonName);
        fightTunnelMonster(kind);
        Thread.sleep(pauseAfterAction);
    }

    // Смена линии метро
    private void changeLine(String stationName, boolean isDocument) throws InterruptedException {
        intoTunnel(stationName);
        enterStation(isDocument, PAUSE_SHORT_MS);
    }

    private void intoTunnel(String stationName) throws InterruptedException {
        clickButton("В туннель");
        Thread.sleep(PAUSE_SHORT_TUNNELS_MS);
        clickButton(stationName);
    }

    // Бои с туннельными монстрами
    private void fightTunnelMonster(MonsterKind kind) throws InterruptedException {
        Thread.sleep(PAUSE_TUNNEL_MS);
        if (isPet) {
            clickButton("Питомец");
        }
        clickButton("Пропустить");
        clickButton("Закрыть");
        unifiedCounter.plusOne();
        if (kind == MonsterKind.SPIDER) {
            System.out.printf("Убито пауков: %d%n%n", unifiedCounter.getCount());
            Thread.sleep(PAUSE_SHORT_MS);
        } else {
            System.out.printf("Убито ящеров: %d%n%n", unifiedCounter.getCount());
        }
        Thread.sleep(PAUSE_TUNNEL_MS);
    }

    private String printTime(long seconds) {
        return String.format("%d мин %d сек\n", seconds / 60, seconds % 60);
    }
}