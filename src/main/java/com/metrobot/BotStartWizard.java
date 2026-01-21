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
            Utilities.ModeSelection selection = Utilities.askModeSelection();
            if (selection == null) return Optional.empty();

            int mode = selection.mode();
            boolean usePet = selection.usePet();

            // 2. Окна
            Utilities.restoreAllGameWindows();
            List<HWND> found = Utilities.findGameWindows();
            List<HWND> active = Utilities.askActiveWindows(found, config.get("activeWindows"));
            if (active == null || active.isEmpty()) return Optional.empty();

            // 3. Время старта
            String botName = resolveBotName(mode);
            LocalTime defaultTime = resolveDefaultTime(mode, config);

            LocalTime startTime = Utilities.askStartTime(botName, defaultTime);
            if (startTime == null) return Optional.empty();

            BotStartConfig cfg = new BotStartConfig(
                    mode,
                    botName,
                    startTime,
                    active,
                    usePet
            );

            ConfigManager.saveConfig(cfg);

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
            case 6 -> "Старт рейда";
            default -> throw new IllegalArgumentException("Неизвестный режим: " + mode);
        };
    }

    private static LocalTime resolveDefaultTime(int mode, Map<String, String> config) {
        return switch (mode) {
            case 1 -> Utilities.parseTime(config.get("kv_start"));
            case 2 -> Utilities.parseTime(config.get("raid_start"));
            case 3 -> Utilities.parseTime(config.get("arena_start"));
            case 4 -> Utilities.parseTime(config.get("tunnel_start"));
            case 5 -> Utilities.parseTime(config.get("rat_start"));
            case 6 -> Utilities.parseTime(config.get("raid2_start"));
            default -> null;
        };
    }
}
