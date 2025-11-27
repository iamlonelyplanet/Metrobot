package com.metrobot;

import java.awt.*;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

import com.sun.jna.platform.win32.WinDef.HWND;

import static com.metrobot.Buttons.*;

/**
 * Режим "Бой с крысами". Пока экспериментальный

 */

public class RatBot extends BaseBot {
    public RatBot(List<HWND> windows, LocalTime timeHHmm, String botName, boolean usePet) throws AWTException {
        super(windows);
        {
            this.startTime = timeHHmm;
            this.botName = botName;
            this.usePet = usePet;
        }
    }

    @Override
    protected Map<String, Point> getButtonMap() {
        return Buttons.RAT_BUTTONS;
    }

    public void start() {
        try {
            startGame();
            showActiveWindows();

            // Бои
            for (int battle = 1; battle <= 50; battle++) {
                System.out.println("\n=== Бой " + battle + " из " + 50 + " ===");
//                showActiveWindows();
//                clickButton("Клан - Выход");
                clickButton("Начстанции");
                Thread.sleep(PAUSE_SHORT_MS);
                clickButton("Крыса");
//                minimizeActiveWindows();
                Thread.sleep(20_000); // обычно 60_000, VIP: 30_000
//                showActiveWindows();
                if (usePet) {
                    clickButton("Питомец");
                }
                clickButton("Пропустить");
                Thread.sleep(PAUSE_LONG_MS);
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
