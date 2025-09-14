package com.metrobot;

public class Grammar {
//    public static void main(String[] args) {
//        for (int i = 0; i < 128; i++) {
//            System.out.println(getWordEnd(i));
//        }
//
//    }

// Метод для определения суффикса глагола и окончания существительного в зависимости от числительного
// Прошёл 1 бой / Прошло 2 боя / Прошло 11 боёв и т.д.

    static String getWordEnd(int battle) {
        String verb = "";
        String noun = "";

        if (battle < 1) {
            return "Количество боёв - натуральное число";
        }

        int i = battle % 100;
        switch (i) {
            case 11, 12, 13, 14:
                verb = "ло";
                noun = "ёв";
                return "Прош" + verb + " " + battle + " бо" + noun;
        }

        switch (battle % 10) {
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
        return "Прош" + verb + " " + battle + " бо" + noun;
    }
}
