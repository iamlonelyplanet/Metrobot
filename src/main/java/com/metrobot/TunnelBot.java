package com.metrobot;

import java.awt.*;
import java.time.*;
import java.util.List;
import java.util.Map;

import com.sun.jna.platform.win32.WinDef.HWND;

import static com.metrobot.Buttons.*;

/**
 * Режим "Туннели": ежедневная прогулка перса по туннелям с особыми монстрами - Ящерами и Пауками. Полное прохождение:
 * порядка 55 минут (28 при VIP, 14 при VIP + скоростной одежде).
 * В этом режиме окна не сворачиваются, работа пользователя в Windows крайне нежелательна, т.к. клики идут непрерывно.
 * Запись счётчика в файл здесь не нужна.
 * Некогда был самым некрасивым классом. Зато самый интересный для обучения, перегрузки, сигнатур, ООП и
 * многого-прочего, что и происходит с апрельской версии 1.2.9
 * TODO: Подумать о паузах по команде пользователя
 * TODO:
 * 1) Режим кикимор невозможен при первых двух - надо выбирать место старта.
 * 2) Смотреть тайминги
 */

public class TunnelBot extends BaseBot {
    public TunnelBot(List<HWND> windows,
                     LocalTime timeHHmm,
                     String botName,
                     boolean isPet, boolean isCloseAfterFinish) throws AWTException {

        super(windows);

        this.startTime = timeHHmm;
        this.botName = botName;
        this.isPet = isPet;
        this.isCloseAfterFinish = isCloseAfterFinish;
    }

    @Override
    protected Map<String, Point> getButtonMap() {
        return TUNNEL_BUTTONS;
    }

    // Думать. С питомцем: опаздывает на втором пауьчем переходе.
    private int pauseTunnelMS = 8_500; // Альтернативные времена: 16_000, 4_700, 9_000 (?), 11_500, 6_500

    private static final byte MAX_TUNNEL_WAYS = 4;
    private Duration timer = Duration.ZERO;

    @Override
    public void playGame() {
        try {
            startGame();

            showActiveWindows();
            Thread.sleep(PAUSE_SHORT_TUNNELS_MS);
            Instant startTunnelTime = Instant.now();
            if (!isPet) {
                pauseTunnelMS += 200;
            }

            killSpiders(); // Бои в туннелях с пауками

            unifiedCounter.setBattleNumber(0);
            Thread.sleep(PAUSE_SHORT_TUNNELS_MS);

            killLizards(); // Бои в туннелях с ящерами

//            killKikimoras();

            System.out.printf("Итого на режим %s затрачено %s", botName, printTime(timer.getSeconds()));

            endGame();
        } catch (Exception e) {
            handleExceptions(e);
        }
    }

    // Режим пауков
    private void killSpiders() throws InterruptedException {
        Instant startSpiderTime = Instant.now();
        fightSpiders("Карта ПК-КРО", "Карта КРО-ПК"); // 10 пауков в туннеле Парк Культуры - Кропоткинская
        changeLine("Карта ПКк-ПКг", true); // Переход Парк Культуры 1 - Парк Культуры 2, однократно
        fightSpiders("Карта ПКг-КИЕ", "Карта КИЕ-ПКг"); // 10 пауков в тоннеле Парк Культуры - Киевская
        changeLine("Карта ПКг-ПКк", false); // Переход Парк Культуры Красные - Парк Культуры Ганза, однократно

        System.out.printf("\nПауки закончились, прибито %d. Идём к ящерам\n", unifiedCounter.getBattleNumber());
        timer = Duration.between(startSpiderTime, Instant.now());
        long secondsSpider = timer.getSeconds();
        System.out.printf("На пауков затрачено: %s", printTime(secondsSpider));

    }

    // Серия боёв с пауками
    private void fightSpiders(String map1, String map2) throws InterruptedException {
        for (int way = 0; way < MAX_TUNNEL_WAYS; way++) {
            exitStation(map1, MonsterKind.SPIDER, 0);
            exitStation(map2, MonsterKind.SPIDER, 0);
        }
    }

    // Режим ящеров
    private void killLizards() throws InterruptedException {
        Instant startLizardTime = Instant.now();

        for (int way = 0; way < MAX_TUNNEL_WAYS; way++) {
            // 4 ящерицы в тоннелях Парк Культуры - Проспект Вернадского
            Instant startLizard1stWayTime = Instant.now();
            exitStation("Карта-ПК-ФРУ", MonsterKind.LIZARD, PAUSE_SHORT_TUNNELS_MS, true, 0);
            exitStation("Карта-КОМ", MonsterKind.LIZARD, 0);
            exitStation("Карта-УНИ", MonsterKind.LIZARD, PAUSE_SHORT_TUNNELS_MS, true, 200);
            exitStation("Карта-ПВ", MonsterKind.LIZARD, 0);
            Duration oneWayDuration = Duration.between(startLizard1stWayTime, Instant.now());
            System.out.printf("Завершено пробегов до %s: %d. Затрачено: %s\n", MonsterKind.LIZARD.stationName1, way + 1, printTime(oneWayDuration.getSeconds()));

            // 4 ящерицы в тоннелях Проспект Вернадского - Парк Культуры
            Instant startLizard2ndWayTime = Instant.now();
            exitStation("Карта-УНИ", MonsterKind.LIZARD, PAUSE_SHORT_MS);
            exitStation("Карта-КОМ", MonsterKind.LIZARD, PAUSE_SHORT_MS, true, PAUSE_SHORT_MS);
            exitStation("Карта-КОМ-ФРУ", MonsterKind.LIZARD, PAUSE_SHORT_MS);
            exitStation("Карта-ФРУ-ПК", MonsterKind.LIZARD, PAUSE_SHORT_MS, false, PAUSE_SHORT_MS);
            oneWayDuration = Duration.between(startLizard2ndWayTime, Instant.now());
            System.out.printf("Завершено пробегов до %s: %d. Затрачено: %s\n", MonsterKind.LIZARD.stationName2, way + 1, printTime(oneWayDuration.getSeconds()));

            Duration lizardDuration = Duration.between(startLizardTime, Instant.now()); // Для таймера, потом удалить
            long secondsLizard = lizardDuration.getSeconds();
            System.out.printf("На ящеров затрачено: %s", printTime(secondsLizard));
            timer = timer.plusSeconds(secondsLizard);
        }
    }

    // Режим кикимор
    private void killKikimoras() throws InterruptedException {
        Instant startKikimoraTime = Instant.now();

        for (int way = 0; way < MAX_TUNNEL_WAYS; way++) {
            // 3 кикиморы в тоннелях Рижская - Тургеневская
            Instant startKikimora1stWayTime = Instant.now();
            exitStation("Карта Риж-ПМ", MonsterKind.KIK, PAUSE_SHORT_TUNNELS_MS, true, 0);
            exitStation("Карта ПМ-Сух", MonsterKind.KIK, PAUSE_SHORT_TUNNELS_MS);
            exitStation("Карта Сух-Тур", MonsterKind.KIK, 0);
            Duration oneWayDuration = Duration.between(startKikimora1stWayTime, Instant.now());
            System.out.printf("Завершено пробегов до %s: %d. Затрачено: %s\n", MonsterKind.KIK.stationName1, way + 1, printTime(oneWayDuration.getSeconds()));

            // 3 кикиморы в тоннелях Тургеневская - Рижская
            Instant startKikimora2ndWayTime = Instant.now();
            exitStation("Карта Тур-Сух", MonsterKind.KIK, 0);
            exitStation("Карта Сух-ПМ", MonsterKind.KIK, PAUSE_SHORT_MS, true, 0);
            exitStation("Карта ПМ-Риж", MonsterKind.KIK, PAUSE_SHORT_TUNNELS_MS, false, PAUSE_SHORT_MS);
            oneWayDuration = Duration.between(startKikimora2ndWayTime, Instant.now());
            System.out.printf("Завершено пробегов до %s: %d. Затрачено: %s\n", MonsterKind.KIK.stationName2, way + 1, printTime(oneWayDuration.getSeconds()));

            Duration kikDuration = Duration.between(startKikimoraTime, Instant.now());
            long secondsKik = kikDuration.getSeconds();
            System.out.printf("На кикимор затрачено: %s", printTime(secondsKik));
            timer = timer.plusSeconds(secondsKik);
        }
    }

    // Единичный бой с любым туннельным монстром
    private void fightTunnelMonster(MonsterKind kind) throws InterruptedException {
        Thread.sleep(pauseTunnelMS);
        if (isPet) {
            clickButton("Питомец");
        }
        clickButton("Пропустить");
        clickButton("Закрыть");

        unifiedCounter.plusOne();
        System.out.printf("Убито %s: %d%n%n", kind.monsterName , unifiedCounter.getBattleNumber());
        Thread.sleep(PAUSE_SHORT_MS + pauseTunnelMS);
    }

    // Переход на станцию с автоматичесмим входом (без пропуска)
    private void exitStation(String buttonName, MonsterKind kind, int pauseAfterAction) throws InterruptedException {
        intoTunnel(buttonName);
        fightTunnelMonster(kind);
        Thread.sleep(pauseAfterAction);
    }

    // Переход на станцию (с пропуском и без), перегрузка метода
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
        String intoTunnelButton = stationName.equals("Карта Тур-Сух")
                ? "Выход Тургеневская"
                : "В туннель";

        Thread.sleep(PAUSE_SHORT_TUNNELS_MS);
        clickButton(intoTunnelButton);
        Thread.sleep(PAUSE_SHORT_TUNNELS_MS + 300);
        clickButton(stationName);
    }
}