package com.metrobot;

import java.time.LocalTime;
import java.util.List;
import com.sun.jna.platform.win32.WinDef.HWND;

/**
Учимся работать с DTO.
 DTO — это класс, который только хранит данные, не принимает решений, не открывает окна, не пишет в файлы
 */
public class BotStartConfig {

    private final int mode;
    private final String botName;
    private final String boss;
    private final LocalTime startTime;
    private final List<HWND> activeWindows;
    private final boolean usePet;
    private final boolean closeAfterFinish;

    public BotStartConfig(int mode,
                          String botName, String bossName,
                          LocalTime startTime,
                          List<HWND> activeWindows,
                          boolean usePet,
                          boolean closeAfterFinish) {
        this.mode = mode;
        this.botName = botName;
        this.boss = bossName;
        this.startTime = startTime;
        this.activeWindows = activeWindows;
        this.usePet = usePet;
        this.closeAfterFinish = closeAfterFinish;
    }

    public int getMode() {
        return mode;
    }

    public String getBotName() {
        return botName;
    }

    public String getBoss() {
        return boss;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public List<HWND> getActiveWindows() {
        return activeWindows;
    }

    public boolean isUsePet() {
        return usePet;
    }

    public boolean isCloseAfterFinish() {return closeAfterFinish; }
}
