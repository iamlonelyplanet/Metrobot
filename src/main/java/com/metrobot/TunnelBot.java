package com.metrobot;

import java.awt.*;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

import static com.metrobot.WindowConfig.*;

public class TunnelBot extends BaseBot {
    // TODO: переработать по "десятке"

    public TunnelBot(List<Integer> windows, LocalTime timeHHmm, String botName) {
        super(windows);
        this.startTime = timeHHmm;
        this.botName = botName;
    }

    @Override
    protected Map<String, Point> getButtonMap() {
        return WindowConfig.TUNNEL_BUTTONS;
    }

    public void start() {
        try {
            startGame();
            // Туннели с пауками
            // Внимание, тут другой способ, чем в случае с ящерами
            int spiders = 0;
            showAllGameWindows();
            // 10 пауков в туннеле Парк Культуры - Кропоткинская
            for (int waySpiders1 = 0; waySpiders1 < 5; waySpiders1++) {
                clickAllWindows("В туннель");
                Thread.sleep(PAUSE_SHORT_MS);
                clickAllWindows("Карта ПК-КРО");
                Thread.sleep(PAUSE_TUNNEL_MS);
                clickAllWindows("Пропустить");
                Thread.sleep(PAUSE_LONG_MS);
                clickAllWindows("Закрыть");

                spiders++;
                System.out.println(Grammar.getWord2End2(spiders, "паук"));

                Thread.sleep(PAUSE_TUNNEL_MS);
                clickAllWindows("Войти"); //TODO: ориентир на Ф1, глянуть с недостатком красных
                Thread.sleep(PAUSE_SHORT_MS);
                clickAllWindows("В туннель");
                Thread.sleep(PAUSE_SHORT_MS);
                clickAllWindows("Карта КРО-ПК");
                Thread.sleep(PAUSE_TUNNEL_MS);
                clickAllWindows("Пропустить");
                Thread.sleep(PAUSE_LONG_MS);
                clickAllWindows("Закрыть");

                spiders++;
                System.out.println(Grammar.getWord2End2(spiders, "паук"));

                Thread.sleep(PAUSE_TUNNEL_MS);
            }

            // переход Парк Культуры ганза - Парк Культуры Красные
            showAllGameWindows();
            clickAllWindows("В туннель");
            Thread.sleep(PAUSE_SHORT_MS);
            clickAllWindows("Карта ПКк-ПКг");
            Thread.sleep(PAUSE_LONG_MS);
            clickAllWindows("Войти с пропуском");
            Thread.sleep(PAUSE_SHORT_MS);


            // 10 Парк Культуры - Киевская, возврат


            // Туннели с Ящерами
            int lizards = 0;
            showAllGameWindows();
            for (int way = 0; way < 5; way++) {
                clickAllWindows("В туннель");
                Thread.sleep(PAUSE_SHORT_MS);
                clickAllWindows("Карта-ПК-ФРУ");
                Thread.sleep(PAUSE_TUNNEL_MS);
                clickAllWindows("Пропустить");
                Thread.sleep(PAUSE_LONG_MS);
                clickAllWindows("Закрыть");

                lizards++;
                System.out.println(Grammar.getWord2End2(lizards, "ящер"));

                Thread.sleep(PAUSE_TUNNEL_MS);
                clickAllWindows("Войти с пропуском");
                Thread.sleep(PAUSE_SHORT_MS);
                clickAllWindows("В туннель");
                Thread.sleep(PAUSE_SHORT_MS);
                clickAllWindows("Карта-КОМ");
                Thread.sleep(PAUSE_TUNNEL_MS);
                clickAllWindows("Пропустить");
                Thread.sleep(PAUSE_LONG_MS);
                clickAllWindows("Закрыть");
                lizards++;
                System.out.println(Grammar.getWord2End2(lizards, "ящер"));
                Thread.sleep(PAUSE_TUNNEL_MS);

                clickAllWindows("В туннель");
                Thread.sleep(PAUSE_SHORT_MS);
                clickAllWindows("Карта-УНИ");
                Thread.sleep(PAUSE_TUNNEL_MS);
                clickAllWindows("Пропустить");
                Thread.sleep(PAUSE_LONG_MS);
                clickAllWindows("Закрыть");
                lizards++;
                System.out.println(Grammar.getWord2End2(lizards, "ящер"));
                Thread.sleep(PAUSE_SHORT_MS);
                Thread.sleep(PAUSE_TUNNEL_MS);
                clickAllWindows("Войти с пропуском");
                Thread.sleep(PAUSE_SHORT_MS);

                clickAllWindows("В туннель");
                Thread.sleep(PAUSE_SHORT_MS);
                clickAllWindows("Карта-ПВ");
                Thread.sleep(PAUSE_TUNNEL_MS);
                clickAllWindows("Пропустить");
                Thread.sleep(PAUSE_LONG_MS);
                clickAllWindows("Закрыть");
                lizards++;
                System.out.println(Grammar.getWord2End2(lizards, "ящер"));
                Thread.sleep(PAUSE_TUNNEL_MS);
                Thread.sleep(PAUSE_SHORT_MS);

                System.out.println("Завершён " + (way + 1) + " пробег до Проспекта Вернадского");

                clickAllWindows("В туннель");
                Thread.sleep(PAUSE_SHORT_MS);
                clickAllWindows("Карта-УНИ");
                Thread.sleep(PAUSE_TUNNEL_MS);
                clickAllWindows("Пропустить");
                Thread.sleep(PAUSE_LONG_MS);
                clickAllWindows("Закрыть");
                lizards++;
                System.out.println(Grammar.getWord2End2(lizards, "ящер"));
                Thread.sleep(PAUSE_TUNNEL_MS);

                clickAllWindows("В туннель");
                Thread.sleep(PAUSE_SHORT_MS);
                clickAllWindows("Карта-КОМ");
                Thread.sleep(PAUSE_TUNNEL_MS);
                clickAllWindows("Пропустить");
                Thread.sleep(PAUSE_LONG_MS);
                clickAllWindows("Закрыть");
                lizards++;
                System.out.println(Grammar.getWord2End2(lizards, "ящер"));
                Thread.sleep(PAUSE_TUNNEL_MS);
                clickAllWindows("Войти с пропуском");
                Thread.sleep(PAUSE_SHORT_MS);

                clickAllWindows("В туннель");
                Thread.sleep(PAUSE_SHORT_MS);
                clickAllWindows("Карта-КОМ-ФРУ");
                Thread.sleep(PAUSE_TUNNEL_MS);
                clickAllWindows("Пропустить");
                Thread.sleep(PAUSE_LONG_MS);
                clickAllWindows("Закрыть");
                lizards++;
                System.out.println(Grammar.getWord2End2(lizards, "ящер"));
                Thread.sleep(PAUSE_TUNNEL_MS);

                clickAllWindows("В туннель");
                Thread.sleep(PAUSE_SHORT_MS);
                clickAllWindows("Карта-ФРУ-ПК");
                Thread.sleep(PAUSE_TUNNEL_MS);
                clickAllWindows("Пропустить");
                Thread.sleep(PAUSE_LONG_MS);
                clickAllWindows("Закрыть");
                lizards++;
                System.out.println(Grammar.getWord2End2(lizards, "ящер"));
                Thread.sleep(PAUSE_SHORT_MS);
                Thread.sleep(PAUSE_TUNNEL_MS);
                clickAllWindows("Войти");
                Thread.sleep(PAUSE_LONG_MS);

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
