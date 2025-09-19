package com.metrobot;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Map;

/**
 * OverlayTest — показывает все точки выбранного режима (берёт Map из WindowConfig).
 * Красные — 100% (оригинал), зелёные — 95% (масштаб).
 */
public class OverlayTest {
    private static final double SCALE = 0.95; // коэффициент для зелёных точек

    public static void main(String[] args) {
        SwingUtilities.invokeLater(OverlayTest::createAndShowGUI);
    }

    private static void createAndShowGUI() {
        // выбор режима
        String[] options = {"Арена", "КВ", "Рейд", "Туннели"};
        int choice = JOptionPane.showOptionDialog(
                null,
                "Выбери режим:",
                "OverlayTest",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]
        );
        String mode = choice >= 0 ? options[choice] : options[0];

        // выбираем Map из WindowConfig в зависимости от режима
        final Map<String, Point> buttonsMap;
        switch (mode) {
            case "КВ":
                buttonsMap = WindowConfig.KV_BUTTONS;
                break;
            case "Рейд":
                buttonsMap = WindowConfig.RAID_BUTTONS;
                break;
            case "Туннели":
                buttonsMap = WindowConfig.TUNNEL_BUTTONS;
                break;
            case "Арена":
            default:
                buttonsMap = WindowConfig.ARENA_BUTTONS;
                break;
        }

        // full-screen transparent window
        GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        int screenW = gd.getDisplayMode().getWidth();
        int screenH = gd.getDisplayMode().getHeight();

        final JWindow window = new JWindow();
        window.setBackground(new Color(0, 0, 0, 0)); // per-pixel transparency
        window.setAlwaysOnTop(true);
        window.setBounds(0, 0, screenW, screenH);

        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setComposite(AlphaComposite.SrcOver);

                // легенда
                g2.setColor(new Color(0, 0, 0, 140));
                g2.fillRoundRect(10, 10, 300, 70, 10, 10);
                g2.setColor(Color.WHITE);
                g2.drawString("Режим: " + mode, 20, 30);
                g2.setColor(new Color(255, 0, 0, 200));
                g2.fillOval(20, 40, 10, 10);
                g2.setColor(Color.WHITE);
                g2.drawString("100% — исходные координаты", 40, 50);
                g2.setColor(new Color(0, 200, 0, 200));
                g2.fillOval(20, 60, 10, 10);
                g2.setColor(Color.WHITE);
                g2.drawString("95% — скорректированные (scale=" + SCALE + ")", 40, 70);

                // рисуем все точки из Map
                if (buttonsMap != null) {
                    g2.setStroke(new BasicStroke(1f));
                    for (Map.Entry<String, Point> e : buttonsMap.entrySet()) {
                        String name = e.getKey();
                        Point p = e.getValue();

                        int rx = p.x;
                        int ry = p.y;
                        int sx = (int) Math.round(p.x * SCALE);
                        int sy = (int) Math.round(p.y * SCALE);

                        // красная — оригинальная
                        g2.setColor(new Color(255, 0, 0, 200));
                        g2.fillOval(rx - 5, ry - 5, 10, 10);

                        // зелёная — масштабированная
                        g2.setColor(new Color(0, 200, 0, 200));
                        g2.fillOval(sx - 5, sy - 5, 10, 10);

                        // подпись рядом с оригинальной точкой
                        g2.setColor(Color.WHITE);
                        g2.drawString(name + "  R(" + rx + "," + ry + ")  G(" + sx + "," + sy + ")", rx + 12, ry + 4);
                    }
                } else {
                    g2.setColor(Color.YELLOW);
                    g2.drawString("Карта точек не найдена для режима: " + mode, 20, 100);
                }

                g2.dispose();
            }
        };

        panel.setOpaque(false);

        // закрытие правым кликом
        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    window.dispose();
                }
            }
        });

        window.add(panel);

        // Esc — закрыть (глобально)
        KeyEventDispatcher ked = e -> {
            if (e.getID() == KeyEvent.KEY_PRESSED && e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                SwingUtilities.invokeLater(window::dispose);
                return true;
            }
            return false;
        };
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(ked);

        // при закрытии очищаем KeyEventDispatcher
        window.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                KeyboardFocusManager.getCurrentKeyboardFocusManager().removeKeyEventDispatcher(ked);
            }

            @Override
            public void windowClosing(WindowEvent e) {
                KeyboardFocusManager.getCurrentKeyboardFocusManager().removeKeyEventDispatcher(ked);
            }
        });

        window.setVisible(true);
    }
}



