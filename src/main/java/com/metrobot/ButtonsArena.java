package com.metrobot;

public enum ButtonsArena {
        ARENA ("Арена", 360, 285), // ПМ-г
        ARENA_2 ("Арена 2", 320, 303), // Основные станции: ПРМ-К
        ATTACK ("Атаковать", 200, 513),
        PET ("Питомец", 80, 505),
        SKIP("Пропустить", 385, 33),
        CLOSE_1("Закрыть 1", 460, 520),
        CLOSE_2("Закрыть 2", 480, 463);

        final String name;
        final int x;
        final int y;
//        final int pause;

    ButtonsArena(String name, int x, int y) {
            this.name = name;
            this.x = x;
            this.y = y;
//            this.pause = pause;
    }
}
