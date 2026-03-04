package com.metrobot;

import java.util.*;

/**
 * Главный класс. Обнуляет счётчики боёв раз в сутки после 03:00 Мск, так нужно по логике игры, загружает последнюю
 * конфигурацию, включает диалог с пользователем про режим игры, количество игровых окон и время старта.
 * Здесь и в следующих классах прошу ориентироваться на комментарии перед методами.
 */

public class Main {
    public static void main(String[] args) {
        try {
            // === Обнуляем счётчики в файле ежедневно при первом запуске после 03:00 по Мск, так надо ===
            ConfigManager.autoResetCounters();

            // === Загружаем конфиг из файла при наличии ===
            Map<String, String> config = ConfigManager.loadConfig();

            // === Спрашиваем у пользователя режим, рабочие окна, время старта ===
            Optional<BotStartConfig> result = BotStartWizard.askUser(config);

            if (result.isEmpty()) {
                System.out.println("Запуск отменён пользователем"); // Cancel/крестик на любом этапе - остановка работы
                return;
            }

            BotStartConfig cfg = result.get();
            BotFactory.start(cfg);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}