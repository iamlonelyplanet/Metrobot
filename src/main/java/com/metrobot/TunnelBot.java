package com.metrobot;

import java.awt.*;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

import static com.metrobot.WindowConfig.*;

public class TunnelBot extends BaseBot {

    public TunnelBot(List<Integer> windows, LocalTime timeHHmm, String botName) {
        super(windows);
        this.startTime = timeHHmm;
    }

    @Override
    protected Map<String, Point> getButtonMap() {
        return WindowConfig.TUNNEL_BUTTONS;
    }

    public void start() {
        try {
            startGame();
//            Map<String, Counter> counters = CounterStorage.loadCounters(Arrays.asList("Арена", "КВ", "Рейд"));
//            Counter arenaCounter = counters.get("Арена");
            int lizards = 0;
            showAllGameWindows();

            // Бои
            for (int way = 0; way < 5; way++) {
                clickAllWindows("В туннель");
                Thread.sleep(PAUSE_SHORT_MS);
                clickAllWindows("Карта-ПК-ФРУ");
                Thread.sleep(PAUSE_TUNNEL_MS);
                clickAllWindows("Пропустить");
                Thread.sleep(PAUSE_LONG_MS - 1200);
                clickAllWindows("Закрыть");
                lizards++;

                System.out.println("Убито Ящеров: " + lizards);
                System.out.println(Grammar.getWord2End(lizards));

                Thread.sleep(PAUSE_TUNNEL_MS);
                clickAllWindows("Войти - с пропуском");
                Thread.sleep(PAUSE_LONG_MS - 1200);

                clickAllWindows("В туннель");
                Thread.sleep(PAUSE_SHORT_MS);
                clickAllWindows("Карта-КОМ");
                Thread.sleep(PAUSE_TUNNEL_MS);
                clickAllWindows("Пропустить");
                Thread.sleep(PAUSE_LONG_MS - 1200);
                clickAllWindows("Закрыть");
                lizards++;
                System.out.println("Убито Ящеров: " + lizards);
                Thread.sleep(PAUSE_TUNNEL_MS);

                clickAllWindows("В туннель");
                Thread.sleep(PAUSE_SHORT_MS);
                clickAllWindows("Карта-УНИ");
                Thread.sleep(PAUSE_TUNNEL_MS);
                clickAllWindows("Пропустить");
                Thread.sleep(PAUSE_LONG_MS - 1200);
                clickAllWindows("Закрыть");
                lizards++;
                System.out.println("Убито Ящеров: " + lizards);
                Thread.sleep(PAUSE_SHORT_MS);
                Thread.sleep(PAUSE_TUNNEL_MS);
                clickAllWindows("Войти - с пропуском");
                Thread.sleep(PAUSE_LONG_MS - 1200);

                clickAllWindows("В туннель");
                Thread.sleep(PAUSE_SHORT_MS);
                clickAllWindows("Карта-ПВ");
                Thread.sleep(PAUSE_TUNNEL_MS);
                clickAllWindows("Пропустить");
                Thread.sleep(PAUSE_LONG_MS - 1200);
                clickAllWindows("Закрыть");
                lizards++;
                System.out.println("Убито Ящеров: " + lizards);
                Thread.sleep(PAUSE_TUNNEL_MS);
                Thread.sleep(PAUSE_LONG_MS - 1200);

                System.out.println("Завершён " + (way + 1) + " пробег до Проспекта Вернадского");

                clickAllWindows("В туннель");
                Thread.sleep(PAUSE_SHORT_MS);
                clickAllWindows("Карта-УНИ");
                Thread.sleep(PAUSE_TUNNEL_MS);
                clickAllWindows("Пропустить");
                Thread.sleep(PAUSE_LONG_MS - 1200);
                clickAllWindows("Закрыть");
                lizards++;
                System.out.println("Убито Ящеров: " + lizards);
                Thread.sleep(PAUSE_TUNNEL_MS);

                clickAllWindows("В туннель");
                Thread.sleep(PAUSE_SHORT_MS);
                clickAllWindows("Карта-КОМ");
                Thread.sleep(PAUSE_TUNNEL_MS);
                clickAllWindows("Пропустить");
                Thread.sleep(PAUSE_LONG_MS - 1200);
                clickAllWindows("Закрыть");
                lizards++;
                System.out.println("Убито Ящеров: " + lizards);
                Thread.sleep(PAUSE_TUNNEL_MS);
                clickAllWindows("Войти - с пропуском");
                Thread.sleep(PAUSE_LONG_MS - 1200);

                clickAllWindows("В туннель");
                Thread.sleep(PAUSE_SHORT_MS);
                clickAllWindows("Карта-КОМ-ФРУ");
                Thread.sleep(PAUSE_TUNNEL_MS);
                clickAllWindows("Пропустить");
                Thread.sleep(PAUSE_LONG_MS - 1200);
                clickAllWindows("Закрыть");
                lizards++;
                System.out.println("Убито Ящеров: " + lizards);
                Thread.sleep(PAUSE_TUNNEL_MS);

                clickAllWindows("В туннель");
                Thread.sleep(PAUSE_SHORT_MS);
                clickAllWindows("Карта-ФРУ-ПК");
                Thread.sleep(PAUSE_TUNNEL_MS);
                clickAllWindows("Пропустить");
                Thread.sleep(PAUSE_LONG_MS - 1200);
                clickAllWindows("Закрыть");
                lizards++;
                System.out.println("Убито Ящеров: " + lizards);
                Thread.sleep(PAUSE_SHORT_MS);
                Thread.sleep(PAUSE_TUNNEL_MS);
                clickAllWindows("Войти - без пропуска");
                Thread.sleep(PAUSE_LONG_MS - 1200);

                System.out.println("Завершён " + (way + 1) + " пробег до Парка Культуры");
                Thread.sleep(PAUSE_SHORT_MS);

//              Задел на будущее - счётчик Ящеров
//                arenaCounter.plusOne();
//                CounterStorage.saveCounters(counters);
//                System.out.println(Grammar.getWordEnd(arenaCounter.getCount()));

//                if (way < MAX_BATTLES_ARENA) {
//                    System.out.println("Пауза между боями...");
//                    countdown(FIVE_MINUTES_PAUSE_SECONDS);
//                }
            }

            minimizeAllGameWindows();
            System.out.println("\nПоход по тоннелям завершён. Убито Ящеров: " + lizards);

        } catch (InterruptedException e) {
            System.out.println("Прервано — выхожу.");
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
