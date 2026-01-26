package com.metrobot;

import java.awt.*;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

import com.sun.jna.platform.win32.WinDef.HWND;

import static com.metrobot.Buttons.*;

/**
 * Режим "Бой с крысами". Пока экспериментальный. Silent mode под вопросом, подумать.
 */

public class RatBot extends BaseBot {
    public RatBot(List<HWND> windows,
                  LocalTime timeHHmm,
                  String botName,
                  boolean usePet,
                  boolean closeAfterFinish) throws AWTException {

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
        return RAT_BUTTONS;
    }

    public void start() {
        try {
            startGame();
            showActiveWindows();

            // Бои
            for (int battle = 1; battle <= MAX_ENERGY; battle++) {
                printBattleNumber(battle, MAX_ENERGY);

                clickButton("Начстанции");
                Thread.sleep(PAUSE_SHORT_MS);
                clickButton("Крыса");

                minimizeActiveWindows();
                countdown(60); // стандарт: 60, ст + штаны: 42, ст + комплект: 30, VIP: 30, VIP + комплект: 20
                showActiveWindows();

                if (usePet) {
                    clickButton("Питомец");
                }
                clickButton("Пропустить");
                clickButton("Закрыть — Победа");
                Thread.sleep(PAUSE_SHORT_MS);
                clickButton("Закрыть — Поражение");
                System.out.println("Убито крыс: " + battle);
            }

            endGame();
        } catch (Exception e) {
            handleExceptions(e);
        }
    }
}
