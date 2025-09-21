package com.metrobot;

import java.awt.*;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

import static com.metrobot.WindowConfig.*;

public class TunnelBot extends BaseBot {
    // TODO: переработать по "десятке" (невозможно для пауков). Переработать ящеров при помощи ООП.

    public TunnelBot(List<Integer> windows, LocalTime timeHHmm, String botName) {
        super(windows);
        this.startTime = timeHHmm;
        this.botName = botName;
    }

    @Override
    protected Map<String, Point> getButtonMap() {
        return WindowConfig.TUNNEL_BUTTONS;
    }
    int pauseShortForTunnels = PAUSE_SHORT_MS / 2;

    public void start() {
        try {
            startGame();
            // === Туннели с пауками ===
            // Внимание, тут другой способ, чем в случае с ящерами
            Counter spiders = new Counter("Пауки");
            showAllGameWindows();

//            // 10 пауков в туннеле Парк Культуры - Кропоткинская
//            for (int waySpiders1 = 0; waySpiders1 < MAX_WAYS_TUNNEL; waySpiders1++) {
//                clickAllWindows("В туннель");
//                Thread.sleep(pauseShortForTunnels);
//                clickAllWindows("Карта ПК-КРО");
//                tunnelBattle(spiders.getCount());
//                spiders.plusOne();
//                clickAllWindows("Карта КРО-ПК");
//                tunnelBattle(spiders.getCount());
//                spiders.plusOne();
//            }
//
//            // Переход Парк Культуры Ганза - Парк Культуры Красные
//            clickAllWindows("В туннель");
//            Thread.sleep(pauseShortForTunnels);
//            clickAllWindows("Карта ПКк-ПКг");
//            Thread.sleep(pauseShortForTunnels);
//            clickAllWindows("Войти с пропуском");
//            Thread.sleep(pauseShortForTunnels);
//
//            // 10 пауков в тоннеле Парк Культуры - Киевская
//            for (int waySpiders2 = 0; waySpiders2 < MAX_WAYS_TUNNEL; waySpiders2++) {
//                clickAllWindows("В туннель");
//                Thread.sleep(pauseShortForTunnels);
//                clickAllWindows("Карта ПКг-КИЕ");
//                tunnelBattle(spiders.getCount());
//                spiders.plusOne();
//                clickAllWindows("Карта КИЕ-ПКг");
//                tunnelBattle(spiders.getCount());
//                spiders.plusOne();
//            }
//
//            // Переход Парк Культуры Красные - Парк Культуры Ганза
//            clickAllWindows("В туннель");
//            Thread.sleep(pauseShortForTunnels);
//            clickAllWindows("Карта ПКг-ПКк");
//            Thread.sleep(pauseShortForTunnels);
//            clickAllWindows("Войти");
//            System.out.println("Пауки закончились, идём к ящерам");

            // === Туннели с Ящерами ===
            int lizards = 0;
            showAllGameWindows();
            for (int way = 0; way < MAX_WAYS_TUNNEL; way++) {
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
