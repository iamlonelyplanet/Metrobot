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
 * В этом режиме окна не сворачиваются, работа пользователя в Windows крайне нежелательна.
 * Запись счётчика в файл здесь не нужна.
 * Самый некрасивый класс. Зато самый лёгкий для обучения, для перегрузки, сигнатур, ООП и многого-прочего, что и
 * произошло с апрельской версии 1.2.9
 * TODO: Подумать о паузах по команде пользователя. Тестировать в режиме 1 окна (только ящеры)
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

    @Override
    public void playGame() {
        try {
            startGame();

            // === Туннели с пауками ===
            Instant startTime = Instant.now(); // Пока надо для таймера, потом можно удалить
            showActiveWindows();
            Thread.sleep(PAUSE_BETWEEN_WINDOWS_MS);

            fightAllSpiders();

            Instant endSpiderTime = Instant.now(); // Пока надо для таймера, потом можно удалить
            Duration spidersDuration = Duration.between(startTime, endSpiderTime);
            long secondsSpider = spidersDuration.getSeconds();
            System.out.printf("На пауков затрачено: %s", printTime(secondsSpider));

            // === Туннели с Ящерами ===
            showActiveWindows();
            unifiedCounter.setCount(0);
            Thread.sleep(PAUSE_SHORT_TUNNELS_MS);

            fightAllLizards();

            Duration lizardDuration = Duration.between(endSpiderTime, Instant.now()); // Для таймера, потом удалить
            long secondsLizard = lizardDuration.getSeconds();
            System.out.printf("На ящеров затрачено: %s", printTime(secondsLizard));
            System.out.printf("Итого на режим %s затрачено %s", botName, printTime(secondsSpider + secondsLizard));

            endGame();

        } catch (Exception e) {
            handleExceptions(e);
        }
    }

    // Переход на станцию (с пропуском и без)
    private void exitStation(String buttonName,
                             MonsterKind kind,
                             int pauseStationEntrance,
                             boolean isDocumentRequired,
                             int pauseAfterAction) throws InterruptedException {
        intoTunnel(buttonName);
        fightTunnelMonster(kind);
        enterStation(isDocumentRequired, pauseStationEntrance);
        Thread.sleep(pauseAfterAction);
    }

    // Переход на станцию с автоматичесмим входом (без пропуска)
    private void exitStation(String buttonName, MonsterKind kind, int pauseAfterAction) throws InterruptedException {
        intoTunnel(buttonName);
        fightTunnelMonster(kind);
        Thread.sleep(pauseAfterAction);
    }

    // Вход на станцию (с пропуском и без)
    private void enterStation(boolean isDocumentRequired, int pause) throws InterruptedException {
        Thread.sleep(pause);
        if (isDocumentRequired) {
            Thread.sleep(PAUSE_SHORT_TUNNELS_MS);
            clickButton("Войти с пропуском");
            Thread.sleep(PAUSE_SHORT_TUNNELS_MS);
        } else {
            clickButton("Войти");
        }
    }

    // Смена линии метро
    private void changeLine(String stationName, boolean isDocumentRequired) throws InterruptedException {
        intoTunnel(stationName);
        enterStation(isDocumentRequired, PAUSE_SHORT_MS);
    }

    // Выход со станции = вход на следующую станцию
    private void intoTunnel(String stationName) throws InterruptedException {
        Thread.sleep(PAUSE_SHORT_TUNNELS_MS);
        clickButton("В туннель");
        Thread.sleep(PAUSE_SHORT_TUNNELS_MS);
        clickButton(stationName);
    }

    // Бой с туннельным монстром
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

    // Серия боёв с пауками
    private void fightSpiders(String map1, String map2) throws InterruptedException {
        for (int way = 0; way < MAX_TUNNEL_WAYS; way++) {
            exitStation(map1, MonsterKind.SPIDER, 0);
            exitStation(map2, MonsterKind.SPIDER, 0);
        }
    }

    private void fightAllSpiders() throws InterruptedException {
        fightSpiders("Карта ПК-КРО", "Карта КРО-ПК"); // 10 пауков в туннеле Парк Культуры - Кропоткинская
        changeLine("Карта ПКк-ПКг", true); // Переход Парк Культуры 1 - Парк Культуры 2, однократно
        fightSpiders("Карта ПКг-КИЕ", "Карта КИЕ-ПКг"); // 10 пауков в тоннеле Парк Культуры - Киевская
        changeLine("Карта ПКг-ПКк", false); // Переход Парк Культуры Красные - Парк Культуры Ганза, однократно
        System.out.printf("\nПауки закончились, прибито %d. Идём к ящерам\n", unifiedCounter.getCount());
    }

    private void fightAllLizards() throws InterruptedException {
        for (int way = 0; way < MAX_TUNNEL_WAYS; way++) {
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
    }

        // Подсчёт и формирование строки с потраченным временем
        private String printTime ( long seconds){
            return String.format("%d мин %d сек\n", seconds / 60, seconds % 60);
        }
    }