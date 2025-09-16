package com.metrobot;

public class Grammar {
    public static void main(String[] args) {
        for (int i = 1; i < 41; i++) {
            System.out.println(getWord2End(i));
        }

    }

    // Метод для определения суффикса глагола и окончания существительного в зависимости от числительного
// Прошёл 1 бой / Прошло 2 боя / Прошло 11 боёв и т.д.
    static String getWordEnd(int number) {
        String noun = "";
        String verb = "";

        if (number < 1) {
            return "Количество боёв - натуральное число";
        }

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
        return "Прош" + verb + " " + number + " бо" + noun;
    }

    static String getWord2End(int number) {
        String noun;
        String verb;

        if (number < 1) {
            return "Количество боёв - натуральное число";
        }

        if (number >= 11 && number <= 15) {
            noun = "ов";
        }

        switch (number % 10) {
            case 1 -> noun = "";
            case 2, 3, 4 -> noun = "а";
            default -> noun = "ов";
        }
        if (noun.isEmpty()){
            verb = " ";
        } else {
            verb = "о ";
        }
        return "Убит" + verb + number + " ящер" + noun;
    }
}
