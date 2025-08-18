package com.metrobot;

import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Metrobot {

    private static Robot robot;

    // Координаты для кнопки Начстанции
    private static final int HOME_X = 176;  // центр кнопки (130 + 93/2)
    private static final int HOME_Y = 392;  // центр кнопки (290 + 204/2)

    // Координаты прямоугольника с заданиями
    private static final int TASKS_LEFT = 695;
    private static final int TASKS_TOP = 313;
    private static final int TASKS_WIDTH = 114;
    private static final int TASKS_HEIGHT = 249;

    // Координаты для кликов по заданиям
    private static final Point FIRST_TASK = new Point(700, 333);
    private static final Point SECOND_TASK = new Point(700, 422);
    private static final Point THIRD_TASK = new Point(700, 512);

    // Координаты кнопки "Пропустить бой"
    private static final Point SKIP_BATTLE = new Point(510, 120);

    // Координаты кнопки "Закрыть"
    private static final Point CLOSE_BUTTON = new Point(645, 620);


    public static void main(String[] args) throws Exception {
        System.out.println("Бот запустится через 5 секунд, подготовь окно игры...");
        Thread.sleep(5000);

        // создаём объект ScreenReader
        ScreenReader reader = new ScreenReader();

        // читаем текст с экрана
        String screenText = reader.readScreen();

        // выводим результат
        System.out.println("Распознанный текст: " + screenText);

        robot = new Robot();

        // Клик по Начстанции
        System.out.println("Кликаю по Начстанции...");
        clickAt(HOME_X, HOME_Y);
        Thread.sleep(1000);

        // Скриншот заданий
        System.out.println("Делаю скриншот заданий...");
        captureTasksArea();

        // Анализируем задания и выбираем лучшее
        System.out.println("Анализирую задания...");
        BufferedImage tasksScreenshot = new Robot().createScreenCapture(
                new Rectangle(TASKS_LEFT, TASKS_TOP, TASKS_WIDTH, TASKS_HEIGHT)
        );
        int bestTaskIndex = analyzeTasks(tasksScreenshot, 30); // 30 — изначальное количество энергии
        System.out.println("Выбрано задание №" + (bestTaskIndex + 1));

        Point[] taskPoints = new Point[]{FIRST_TASK, SECOND_TASK, THIRD_TASK};
        clickAt(taskPoints[bestTaskIndex].x, taskPoints[bestTaskIndex].y);

        // Ждём завершения задания (3 минуты = 180000 мс)
        System.out.println("Ожидаю завершения задания (1 минута и пара секунд)...");
        Thread.sleep(62000);

        // Клик по кнопке "Пропустить бой"
        System.out.println("Нажимаю 'Пропустить бой'...");
        clickAt(SKIP_BATTLE.x, SKIP_BATTLE.y);
        Thread.sleep(3000);

        // Клик по кнопке "Закрыть"
        System.out.println("Нажимаю 'Закрыть'...");
        clickAt(CLOSE_BUTTON.x, CLOSE_BUTTON.y);
        Thread.sleep(1000);

        // Снова запускаем следующее задание (тестово, ещё раз)
        System.out.println("Цикл: запускаю следующее задание...");
        clickAt(HOME_X, HOME_Y);
        Thread.sleep(1000);
        captureTasksArea();
        clickAt(FIRST_TASK.x, FIRST_TASK.y);

        System.out.println("Готово!");
    }

    private static void clickAt(int x, int y) throws InterruptedException {
        robot.mouseMove(x, y);
        Thread.sleep(200);
        robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
        robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
        Thread.sleep(500);
    }

    private static void captureTasksArea() throws Exception {
        Rectangle captureRect = new Rectangle(TASKS_LEFT, TASKS_TOP, TASKS_WIDTH, TASKS_HEIGHT);
        BufferedImage screenshot = robot.createScreenCapture(captureRect);
        ImageIO.write(screenshot, "png", new File("tasks_screenshot.png"));
        System.out.println("Скриншот сохранён в tasks_screenshot.png");
    }
    private static int analyzeTasks(BufferedImage fullImage, int availableEnergy) {
        final int TASK_COUNT = 3;

        // Настройка Tesseract
        Tesseract tesseract = new Tesseract();
        tesseract.setDatapath("C:\\Tess4J\\tessdata"); // твой путь
        tesseract.setLanguage("eng");
        tesseract.setTessVariable("tessedit_char_whitelist", "0123456789");
        tesseract.setPageSegMode(6); // PSM_SINGLE_BLOCK — для небольших областей

        int sliceHeight = fullImage.getHeight() / TASK_COUNT;

        for (int i = 0; i < TASK_COUNT; i++) {
            int y = i * sliceHeight;
            int h = (i == TASK_COUNT - 1) ? fullImage.getHeight() - y : sliceHeight;
            BufferedImage slice = fullImage.getSubimage(0, y, fullImage.getWidth(), h);

            // Предобработка для повышения шансов OCR
            BufferedImage proc = preprocessForOCR(slice);

            // Для отладки: сохраняем слайс (можешь потом удалить)
            try {
                ImageIO.write(proc, "png", new File("task_slice_" + (i + 1) + ".png"));
            } catch (Exception e) {
                // не критично
            }

            String ocrText = "";
            try {
                ocrText = tesseract.doOCR(proc);
            } catch (TesseractException e) {
                System.out.println("Tesseract failed on slice " + (i + 1));
                e.printStackTrace();
                continue; // идём к следующему заданию
            }

            System.out.println("OCR (task " + (i + 1) + "):\n" + ocrText);

            // Парсим все найденные числа
            int[] nums = parseNumbers(ocrText);

            // Эвристика для выбора bullets и xp:
            Integer bullets = null;
            Integer xp = null;

            // Вариант A: если найдено >=3 чисел и первое маленькое (вероятно энергия), используем 1-й и 2-й после энергии
            if (nums.length >= 3 && nums[0] <= 10) {
                bullets = nums[1];
                xp = nums[2];
            }
            // Вариант B: если найдено >=2 чисел — берем последние два (часто справа расположены пули и XP)
            else if (nums.length >= 2) {
                bullets = nums[nums.length - 2];
                xp = nums[nums.length - 1];
            }
            // Иначе — не получилось прочитать корректно
            if (bullets == null || xp == null || xp == 0) {
                System.out.println("Не удалось извлечь bullets/xp для задания " + (i + 1));
                continue;
            }

            double ratio = bullets / (double) xp;
            System.out.printf("Задание %d: bullets=%d, xp=%d, ratio=%.2f%n", i + 1, bullets, xp, ratio);

            // Алгоритм принятия решения, как ты предложил:
            if (ratio > 3.0) {
                System.out.println("Выбрано задание №" + (i + 1) + " по условию ratio>3");
                return i;
            }
            // иначе — идём к следующему заданию
        }

        // Если ни одно не подошло — выполняем третье (индекс 2)
        System.out.println("Ни одно задание не удовлетворило условию; выбираем третье.");
        return 2;
    }
    // Вспомогательный метод: простая предобработка (масштаб + бинаризация)
    private static BufferedImage preprocessForOCR(BufferedImage src) {
        // масштабируем x2 и переводим в grayscale
        int scale = 2;
        int w = src.getWidth() * scale;
        int h = src.getHeight() * scale;
        BufferedImage scaled = new BufferedImage(w, h, BufferedImage.TYPE_BYTE_GRAY);
        Graphics2D g = scaled.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g.drawImage(src, 0, 0, w, h, null);
        g.dispose();

        // простая пороговая бинаризация — помогает убрать шум
        int threshold = 140; // можно подбирать
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int gray = scaled.getRaster().getSample(x, y, 0); // 0..255
                int rgb = (gray < threshold) ? 0xFF000000 : 0xFFFFFFFF;
                scaled.setRGB(x, y, rgb);
            }
        }
        return scaled;
    }

    // Вспомогательный метод: извлекает все целые числа из текста в массив int[]
    private static int[] parseNumbers(String text) {
        List<Integer> list = new ArrayList<>();
        Matcher m = Pattern.compile("\\d+").matcher(text);
        while (m.find()) {
            try {
                list.add(Integer.parseInt(m.group()));
            } catch (NumberFormatException ignored) { }
        }
        int[] arr = new int[list.size()];
        for (int i = 0; i < list.size(); i++) arr[i] = list.get(i);
        return arr;
    }
}
