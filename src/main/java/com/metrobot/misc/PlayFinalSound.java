package com.metrobot.misc;


import com.metrobot.BaseBot;

import javax.sound.sampled.*;
import java.io.IOException;
import java.io.InputStream;

// Проигрываем звук по окончанию режима игры. Бесполезная свистоперделка ради учёбы и пасхалка для олдов.
public class PlayFinalSound {
    public static void playFinalSound() {
        try (InputStream inputStream = BaseBot.class.getResourceAsStream("/sound.wav")) {
            if (inputStream == null) {
                System.err.println("Файл звука sound.wav не найден!");
                return;
            }

            try (AudioInputStream audioIn = AudioSystem.getAudioInputStream(inputStream)) {
                Clip clip = AudioSystem.getClip();
                clip.open(audioIn);
                clip.start();
            }
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }
}