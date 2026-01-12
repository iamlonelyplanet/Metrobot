package com.metrobot;

import com.sun.jna.platform.win32.WinDef.HWND;

import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class BotStartWizard {
    public static Optional<BotStartConfig> askUser(Map<String, String> config) {
        try {
            // 1. Выбор режима
            Utilites.ModeSelection selection = Utilites.askModeSelection();
            if (selection == null) return Optional.empty();

            int mode = selection.mode();
            boolean usePet = selection.usePet();

            // 2. Окна
            Utilites.restoreAllGameWindows();
            List<HWND> found = Utilites.findGameWindows();
            List<HWND> active = Utilites.askActiveWindows(found, config.get("activeWindows"));
            if (active == null || active.isEmpty()) return Optional.empty();

            // 3. Время старта
            String botName = resolveBotName(mode);
            LocalTime defaultTime = resolveDefaultTime(mode, config);

            LocalTime startTime = Utilites.askStartTime(botName, defaultTime);
            if (startTime == null) return Optional.empty();

            return Optional.of(
                    new BotStartConfig(
                            mode,
                            botName,
                            startTime,
                            active,
                            usePet
                    )
            );

        } catch (Exception e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    private static String resolveBotName(int mode) {
        return switch (mode) {
            case 1 -> "КВ";
            case 2 -> "Рейд";
            case 3 -> "Арена";
            case 4 -> "Туннель";
            case 5 -> "Крысы";
            default -> throw new IllegalArgumentException("Неизвестный режим: " + mode);
        };
    }

    private static LocalTime resolveDefaultTime(int mode, Map<String, String> config) {
        return switch (mode) {
            case 1 -> Utilites.parseTime(config.get("kv_start"));
            case 2 -> Utilites.parseTime(config.get("raid_start"));
            case 3 -> Utilites.parseTime(config.get("arena_start"));
            case 4 -> Utilites.parseTime(config.get("tunnel_start"));
            case 5 -> Utilites.parseTime(config.get("rat_start"));
            default -> null;
        };
    }
}
