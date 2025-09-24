package com.metrobot;

import java.awt.*;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

import static com.metrobot.Buttons.*;

public class TunnelBot extends BaseBot {
    // TODO: Переработать ящеров при помощи ООП.

    public TunnelBot(List<Integer> windows, LocalTime timeHHmm, String botName) {
        super(windows);
        this.startTime = timeHHmm;
        this.botName = botName;
    }

    @Override
    protected Map<String, Point> getButtonMap() {
        return Buttons.TUNNEL_BUTTONS;
    }
    int pauseShortForTunnels = PAUSE_SHORT_MS / 2; // TODO: не переделать ли в константу?

    public void start() {
        try {
            startGame();
            Counter spiders = new Counter("Пауки");
            Counter lizards = new Counter("Ящеры");

            showAllGameWindows();
            // === Туннели с пауками ===
            // 10 пауков в туннеле Парк Культуры - Кропоткинская
            clickAllWindows("В туннель");
            Thread.sleep(pauseShortForTunnels);
            for (int waySpiders1 = 0; waySpiders1 < MAX_WAYS_TUNNEL; waySpiders1++) {
                clickAllWindows("Карта ПК-КРО");
                fightSpiders(spiders.getCount());
                spiders.plusOne();
                clickAllWindows("Карта КРО-ПК");
                fightSpiders(spiders.getCount());
                spiders.plusOne();
            }

            // Переход Парк Культуры Красные - Парк Культуры Ганза, однократно
//            clickAllWindows("В туннель");
//            Thread.sleep(PAUSE_SHORT_MS);
            clickAllWindows("Карта ПКк-ПКг");
            Thread.sleep(PAUSE_SHORT_MS);
            clickAllWindows("Войти с пропуском");
            Thread.sleep(pauseShortForTunnels);

            // 10 пауков в тоннеле Парк Культуры - Киевская
            clickAllWindows("В туннель");
            Thread.sleep(pauseShortForTunnels);
            for (int waySpiders2 = 0; waySpiders2 < MAX_WAYS_TUNNEL; waySpiders2++) {
                clickAllWindows("Карта ПКг-КИЕ");
                fightSpiders(spiders.getCount());
                spiders.plusOne();
                clickAllWindows("Карта КИЕ-ПКг");
                fightSpiders(spiders.getCount());
                spiders.plusOne();
            }

            // Переход Парк Культуры Красные - Парк Культуры Ганза, однократно
//            clickAllWindows("В туннель");
//            Thread.sleep(pauseShortForTunnels);
            clickAllWindows("Карта ПКг-ПКк");
            Thread.sleep(PAUSE_SHORT_MS);
            clickAllWindows("Войти");
            System.out.println("Пауки закончились, идём к ящерам");

            // === Туннели с Ящерами ===
            showAllGameWindows();
            Thread.sleep(pauseShortForTunnels);

            for (int way = 0; way < MAX_WAYS_TUNNEL; way++) {
                clickAllWindows("В туннель");
                Thread.sleep(pauseShortForTunnels);
                clickAllWindows("Карта-ПК-ФРУ");
                fightLizards(lizards.getCount());
                lizards.plusOne();
                Thread.sleep(pauseShortForTunnels);
                clickAllWindows("Войти с пропуском");
                Thread.sleep(PAUSE_SHORT_MS);

                clickAllWindows("В туннель");
                Thread.sleep(pauseShortForTunnels);
                clickAllWindows("Карта-КОМ");
                fightLizards(lizards.getCount());
                lizards.plusOne();
                Thread.sleep(pauseShortForTunnels);

                clickAllWindows("В туннель");
                Thread.sleep(pauseShortForTunnels);
                clickAllWindows("Карта-УНИ");
                fightLizards(lizards.getCount());
                lizards.plusOne();
                Thread.sleep(pauseShortForTunnels);
                clickAllWindows("Войти с пропуском");
                Thread.sleep(PAUSE_SHORT_MS);

                clickAllWindows("В туннель");
                Thread.sleep(pauseShortForTunnels);
                clickAllWindows("Карта-ПВ");
                fightLizards(lizards.getCount());
                lizards.plusOne();

                System.out.println("Завершено пробегов до Проспекта Вернадского: " + (way + 1));

                clickAllWindows("В туннель");
                Thread.sleep(pauseShortForTunnels);
                clickAllWindows("Карта-УНИ");
                fightLizards(lizards.getCount());
                lizards.plusOne();

                clickAllWindows("В туннель");
                Thread.sleep(pauseShortForTunnels);
                clickAllWindows("Карта-КОМ");
                fightLizards(lizards.getCount());
                lizards.plusOne();
                Thread.sleep(pauseShortForTunnels);
                clickAllWindows("Войти с пропуском");
                Thread.sleep(PAUSE_SHORT_MS);

                clickAllWindows("В туннель");
                Thread.sleep(pauseShortForTunnels);
                clickAllWindows("Карта-КОМ-ФРУ");
                fightLizards(lizards.getCount());
                lizards.plusOne();

                clickAllWindows("В туннель");
                Thread.sleep(pauseShortForTunnels);
                clickAllWindows("Карта-ФРУ-ПК");
                fightLizards(lizards.getCount());
                lizards.plusOne();
                Thread.sleep(pauseShortForTunnels);
                clickAllWindows("Войти");

                System.out.println("Завершено пробегов до Парка Культуры: " + (way + 1));
                Thread.sleep(pauseShortForTunnels);
            }

            minimizeAllGameWindows();
            System.out.println("\nРежим " + botName + " завершён. " + "Убито пауков: " + spiders.getCount() +
                    " . Убито Ящеров: " + lizards.getCount());

        } catch (InterruptedException e) {
            System.out.println("Прервано — выхожу.");
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
