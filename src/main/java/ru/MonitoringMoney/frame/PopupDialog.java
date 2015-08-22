package ru.MonitoringMoney.frame;

import com.sun.awt.AWTUtilities;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.GeneralPath;


/**
 * Всплывающее окно
 */
public class PopupDialog {

    private JDialog dialog = null;
    private Timer timer;
    private float opacity = 0.1f;
    private JPanel contentPanel;

    /**
     * Отрисовывает диалог выше переданного компонента
     *
     * @param source        компонент выше которого нужно отрисовать диалог
     * @param size          размер диалога
     * @param components    компоненты, которые нужно добавить в диалог
     * @param useCloseCross при {true}, добавить крестик скрывающий диалог
     */
    public PopupDialog(JComponent source, Dimension size, Component[] components, boolean useCloseCross, boolean closeUnfocus) {
        if (dialog != null) {
            dialog.setVisible(false);
        }

        dialog = new JDialog();
        dialog.getContentPane().setLayout(new BorderLayout(0, 0));

        // Основная панель - на ней отрисован особый фон окна
        contentPanel = new JPanel() {
            private Color border = new Color(157, 157, 157);
            private Color top = new Color(250, 250, 250);
            private Color bottom = new Color(238, 238, 238);

            {
                dialog.addComponentListener(new ComponentAdapter() {
                    public void componentShown(ComponentEvent e) {
                        if (timer != null && timer.isRunning()) {
                            timer.stop();
                        }
                        opacity = 0.1f;
                        timer = new Timer(1000 / 24, e1 -> {
                            opacity += 0.1f;
                            if (opacity >= 1f) {
                                opacity = 1f;
                                timer.stop();
                            }
                            refresh();
                        });
                        timer.start();
                    }
                });
            }

            private void refresh() {
                repaint();
            }

            public void paint(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity));

                // Полигон для отрисовки формы окна (в виде тултипа)
                GeneralPath gp = new GeneralPath(GeneralPath.WIND_EVEN_ODD);
                gp.moveTo(5, 5);
                gp.quadTo(5, 0, 10, 0);
                gp.lineTo(getWidth() - 11, 0);
                gp.quadTo(getWidth() - 6, 0, getWidth() - 6, 5);
                gp.lineTo(getWidth() - 6, getHeight() - 16);
                gp.quadTo(getWidth() - 6, getHeight() - 11, getWidth() - 11,
                        getHeight() - 11);
                gp.lineTo(getWidth() / 2 + 10, getHeight() - 11);
                gp.lineTo(getWidth() / 2, getHeight() - 1);
                gp.lineTo(getWidth() / 2 - 10, getHeight() - 11);
                gp.lineTo(10, getHeight() - 11);
                gp.quadTo(5, getHeight() - 11, 5, getHeight() - 16);
                gp.lineTo(5, 5);

                g2d.setPaint(new GradientPaint(0, 0, top, 0, getHeight() - 11, bottom));
                g2d.fill(gp);

                g2d.setPaint(border);
                g2d.draw(gp);

                super.paint(g);
            }
        };
        contentPanel.setOpaque(false);
        contentPanel.setLayout(null);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(6, 10, 15, 10));

        JPanel closePanel = new JPanel(new BorderLayout(0, 0)) {{
            setOpaque(false);
            setSize(10, 10);
            setLocation((int) size.getWidth() - 20, 5);

            // Отдельно отрисованный крест для закрытия окна
            add(new JComponent() {
                {
                    setOpaque(false);
                    addMouseListener(new MouseAdapter() {
                        public void mouseEntered(MouseEvent e) {
                            mouseOver = true;
                            repaint();
                        }

                        public void mouseExited(MouseEvent e) {
                            mouseOver = false;
                            repaint();
                        }

                        public void mousePressed(MouseEvent e) {
                            closeDialog();
                        }
                    });
                }

                private boolean mouseOver = false;
                private Color out = new Color(162, 162, 162);
                private Color over = new Color(122, 122, 122);
                private Stroke stroke = new BasicStroke(2f);

                public void paint(Graphics g) {
                    super.paint(g);

                    Graphics2D g2d = (Graphics2D) g;
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                            RenderingHints.VALUE_ANTIALIAS_ON);

                    // Отрисовка кнопки для закрытия окна
                    g2d.setStroke(stroke);
                    g2d.setPaint(mouseOver ? over : out);
                    g2d.drawLine(1, 1, getWidth() - 2, getHeight() - 2);
                    g2d.drawLine(getWidth() - 2, 1, 1, getHeight() - 2);
                }

                public Dimension getPreferredSize() {
                    return new Dimension(10, 10);
                }
            }, BorderLayout.NORTH);
        }};

        if (useCloseCross)
            contentPanel.add(closePanel);

        for (Component component : components) {
            contentPanel.add(component);
        }
        dialog.getContentPane().add(contentPanel, BorderLayout.CENTER);

        // Закрытие окна при потере фокуса
        if (closeUnfocus) {
            dialog.addWindowFocusListener(new WindowFocusListener() {
                public void windowGainedFocus(WindowEvent e) {
                }

                public void windowLostFocus(WindowEvent e) {
                    closeDialog();
                }
            });
        }
        // Отмена стандартной декорации окна и его прозрачности
        dialog.setUndecorated(true);
        AWTUtilities.setWindowOpaque(dialog, false);

        dialog.setSize(size);
        dialog.setLocation(
                source.getLocationOnScreen().x + source.getWidth() / 2 - dialog.getWidth() / 2,
                source.getLocationOnScreen().y - dialog.getHeight());
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setVisible(true);
    }

    /**
     * Таймер для плавного исчезновения при закрытии
     */
    public void closeDialog() {
        if (timer != null && timer.isRunning()) {
            timer.stop();
        }
        opacity = 1f;
        timer = new Timer(1000 / 24, e -> {
            opacity -= 0.1f;
            if (opacity <= 0.1f) {
                opacity = 0.1f;
                dialog.setVisible(false);
                timer.stop();
            }
            contentPanel.repaint();
        });
        timer.start();
    }
}
