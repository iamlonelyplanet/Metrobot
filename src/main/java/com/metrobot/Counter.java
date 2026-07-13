package com.metrobot;

/** Класс-счётчик количества боёв. Практически копия урока 8!
 */
public class Counter {
    private final String counterName;
    private int battleNumber;

    public Counter(String counterName) {
        this(counterName, 0);
    }

    public Counter(String counterName, int battleNumber) {
        this.counterName = counterName;
        this.battleNumber = battleNumber;
    }

    public void plusOne() {
        battleNumber++;
    }

    public String getCounterName() {
        return counterName;
    }

    public int getBattleNumber() {
        return battleNumber;
    }

    public void setBattleNumber(int battleNumber) {
        this.battleNumber = battleNumber;
    }

}
