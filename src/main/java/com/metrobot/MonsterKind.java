package com.metrobot;

public enum MonsterKind {
    SPIDER("пауков"),
    LIZARD("ящеров", "Проспекта Вернадского", "Парка культуры"),
    KIK("кикимор", "Тургеневской", "Рижской");

    final String monsterName;
    final String stationName1;
    final String stationName2;

    MonsterKind(String monsterName) {
        this(monsterName, null, null);
    }

    MonsterKind(String monsterName, String stationName1, String stationName2) {
        this.monsterName = monsterName;
        this.stationName1 = stationName1;
        this.stationName2 = stationName2;
    }
}
