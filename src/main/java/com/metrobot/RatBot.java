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
        return RAT_BUTTONS;
    }

    public void start() {
        try {
            startGame();
            showActiveWindows();

            // Бои
            for (int battle = 1; battle <= MAX_ENERGY; battle++) {
                System.out.println("\n=== Бой " + battle + " из " + MAX_ENERGY + " ===");
                if (isSilentMode) {
                    showActiveWindows();
                }
//                clickButton("Клан - Выход");
                clickButton("Начстанции");
                Thread.sleep(PAUSE_SHORT_MS);
                clickButton("Крыса");
                if (isSilentMode) {
                    minimizeActiveWindows();
                }
                countdown(42); // стандарт: 60, ст + штаны: 42, ст + комплект: 30, VIP: 30, VIP + комплект: 20
                if (isSilentMode) {
                    showActiveWindows();
                }
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
