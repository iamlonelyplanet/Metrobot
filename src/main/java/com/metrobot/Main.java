package com.metrobot;

import java.time.LocalTime;
import java.util.*;

/**
 * Главный класс. Спрашивает в GUI режим игры, активные окна, время старта каждого режима. Обнуляет счётчики
 * раз в сутки после 03:00 Мск, так нужно по логике игры.
 * Здесь и в следующих классах прошу ориентироваться на комментарии перед методами.
 */

public class Main {
    public static void main(String[] args) {
        try {
            // === Обнуляем файл счётчиков каждый день при первом запуске программы после 03:00 по Мск, так надо. ===
            ConfigManager.autoResetCounters();

            // === Загружаем конфиг из файла при наличии ===
            Map<String, String> config = ConfigManager.loadConfig();

            // === Спрашиваем у пользователя режим, рабочие окна, время старта ===
            Optional<BotStartConfig> result =
                    BotStartWizard.askUser(config);

            if (result.isEmpty()) {
                System.out.println("Запуск отменён пользователем."); // Cancel/крестик на любом этапе - остановка работы
                return;
            }

            BotStartConfig cfg = result.get();
            BotFactory.start(cfg);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

//            System.out.println("=== Проверка конфигурации ===");
//            System.out.println("mode = " + cfg.getMode());
//            System.out.println("botName = " + cfg.getBotName());
//            System.out.println("startTime = " + cfg.getStartTime());
//            System.out.println("usePet = " + cfg.isUsePet());
//            System.out.println("windows = " + cfg.getActiveWindows().size());

            // === Запрашиваем режим игры в режиме GUI ===
//            Utilites.ModeSelection selection = Utilites.askModeSelection();
//
//            // === Разворачиваем окна игры по заголовку. Каждое окно = перс/боец ===
//            Utilites.restoreAllGameWindows();
//
//            // === Получаем координаты каждого окна среди развёрнутых ===
//            List<HWND> foundWindows = Utilites.findGameWindows();
//
//            // === Запрашиваем в режиме GUI активные окна из числа найденных, с ними будет работать программа ===
//            List<HWND> activeWindows = Utilites.askActiveWindows(foundWindows, config.get("activeWindows"));
//
//            // === Читаем времена стартов из конфига (если есть). Не трогать, пока хоть как-то работает ===
//            LocalTime arenaDefault = Utilites.parseTime(config.get("arena_start"));
//            LocalTime kvDefault = Utilites.parseTime(config.get("kv_start"));
//            LocalTime raidDefault = Utilites.parseTime(config.get("raid_start"));
//            LocalTime tunnelDefault = Utilites.parseTime(config.get("tunnel_start"));
//            LocalTime ratDefault = Utilites.parseTime(config.get("rat_start"));
//
//            // === Готовим переменные времени для записи обратно в конфиг. Не трогать, пока хоть как-то работает ===
//            LocalTime arenaStart = arenaDefault;
//            LocalTime kvStart = kvDefault;
//            LocalTime raidStart = raidDefault;
//            LocalTime tunnelStart = tunnelDefault;
//            LocalTime ratStart = ratDefault;