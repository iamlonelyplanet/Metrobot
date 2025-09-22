package com.metrobot;

import java.awt.*;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

import static com.metrobot.WindowConfig.*;

public class TunnelBot extends BaseBot {
    // TODO: Переработать ящеров при помощи ООП.

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

            Counter spiders = new Counter("Пауки");
            Counter lizards = new Counter("Ящеры");
            showAllGameWindows();

            // === Туннели с пауками ===
            // 10 пауков в туннеле Парк Культуры - Кропоткинская
            for (int waySpiders1 = 0; waySpiders1 < MAX_WAYS_TUNNEL; waySpiders1++) {
                clickAllWindows("В туннель");
                Thread.sleep(pauseShortForTunnels);
                clickAllWindows("Карта ПК-КРО");
                tunnelBattleSpiders(spiders.getCount());
                spiders.plusOne();
                clickAllWindows("Карта КРО-ПК");
                tunnelBattleSpiders(spiders.getCount());
                spiders.plusOne();
            }

            // Переход Парк Культуры Ганза - Парк Культуры Красные
            clickAllWindows("В туннель");
            Thread.sleep(pauseShortForTunnels);
            clickAllWindows("Карта ПКк-ПКг");
            Thread.sleep(pauseShortForTunnels);
            clickAllWindows("Войти с пропуском");
            Thread.sleep(pauseShortForTunnels);

            // 10 пауков в тоннеле Парк Культуры - Киевская
            for (int waySpiders2 = 0; waySpiders2 < MAX_WAYS_TUNNEL; waySpiders2++) {
                clickAllWindows("В туннель");
                Thread.sleep(pauseShortForTunnels);
                clickAllWindows("Карта ПКг-КИЕ");
                tunnelBattleSpiders(spiders.getCount());
                spiders.plusOne();
                clickAllWindows("Карта КИЕ-ПКг");
                tunnelBattleSpiders(spiders.getCount());
                spiders.plusOne();
            }

            // Переход Парк Культуры Красные - Парк Культуры Ганза
            clickAllWindows("В туннель");
            Thread.sleep(pauseShortForTunnels);
            clickAllWindows("Карта ПКг-ПКк");
            Thread.sleep(pauseShortForTunnels);
            clickAllWindows("Войти");
            System.out.println("Пауки закончились, идём к ящерам");

            // === Туннели с Ящерами ===
            showAllGameWindows();

            for (int way = 0; way < MAX_WAYS_TUNNEL; way++) {
                clickAllWindows("В туннель");
                Thread.sleep(pauseShortForTunnels);
                clickAllWindows("Карта-ПК-ФРУ");
                tunnelBattleLizards(lizards.getCount());
                lizards.plusOne();
                Thread.sleep(pauseShortForTunnels);
                clickAllWindows("Войти с пропуском");
                Thread.sleep(PAUSE_SHORT_MS);

                clickAllWindows("В туннель");
                Thread.sleep(pauseShortForTunnels);
                clickAllWindows("Карта-КОМ");
                tunnelBattleLizards(lizards.getCount());
                lizards.plusOne();
                Thread.sleep(pauseShortForTunnels);

                clickAllWindows("В туннель");
                Thread.sleep(pauseShortForTunnels);
                clickAllWindows("Карта-УНИ");
                tunnelBattleLizards(lizards.getCount());
                lizards.plusOne();
                Thread.sleep(pauseShortForTunnels);
                clickAllWindows("Войти с пропуском");
                Thread.sleep(PAUSE_SHORT_MS);

                clickAllWindows("В туннель");
                Thread.sleep(pauseShortForTunnels);
                clickAllWindows("Карта-ПВ");
                tunnelBattleLizards(lizards.getCount());
                lizards.plusOne();

                System.out.println("Завершено пробегов до Проспекта Вернадского: " + (way + 1));

                clickAllWindows("В туннель");
                Thread.sleep(pauseShortForTunnels);
                clickAllWindows("Карта-УНИ");
                tunnelBattleLizards(lizards.getCount());
                lizards.plusOne();

                clickAllWindows("В туннель");
                Thread.sleep(pauseShortForTunnels);
                clickAllWindows("Карта-КОМ");
                tunnelBattleLizards(lizards.getCount());
                lizards.plusOne();
                Thread.sleep(pauseShortForTunnels);
                clickAllWindows("Войти с пропуском");
                Thread.sleep(PAUSE_SHORT_MS);

                clickAllWindows("В туннель");
                Thread.sleep(pauseShortForTunnels);
                clickAllWindows("Карта-КОМ-ФРУ");
                tunnelBattleLizards(lizards.getCount());
                lizards.plusOne();

                clickAllWindows("В туннель");
                Thread.sleep(pauseShortForTunnels);
                clickAllWindows("Карта-ФРУ-ПК");
                tunnelBattleLizards(lizards.getCount());
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
