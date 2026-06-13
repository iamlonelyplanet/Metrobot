package com.metrobot.misc;

public class Grammar {
    /* Не особо нужный класс для подгона грамматики в зависимости от падежей, склонений, родов и т.п. Больше для учёбы.
 */

    // Метод для определения суффикса глагола и окончания существительного в зависимости от числительного
    // Прошёл 1 бой / Прошло 2 боя / Прошло 11 боёв и т.д.
    public static String getWordEnd(int number) {
        String noun = "";
        String verb = "";

        int i = number % 100;
        switch (i) {
            case 11, 12, 13, 14:
                verb = "ло";
                noun = "ёв";
                return "Прош" + verb + " " + number + " бо" + noun;
        }

        switch (number % 10) {
            case 1 -> {
                verb = "ёл";
                noun = "й";
            }
            case 2, 3, 4 -> {
                verb = "ло";
                noun = "я";
            }
            default -> {
                verb = "ло";
                noun = "ёв";
            }
        }
        return "\nПрош" + verb + " " + number + " бо" + noun;
    }
}
