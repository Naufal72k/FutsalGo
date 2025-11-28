import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ModernMenuButton extends JButton {
        private Tema ini = new Tema();
        private boolean isActive = false;
        private Color hoverColor = new Color(255, 255, 255, 20);

        public ModernMenuButton(String text, boolean isActive) {
            super(text);
            this.isActive = isActive;
            setFont(new Font(ini.font, Font.PLAIN, 16));
            setForeground(Color.WHITE);
            setBackground(Color.decode(ini.warna_utama));
            setBorder(new EmptyBorder(10, 30, 10, 10));
            setHorizontalAlignment(SwingConstants.LEFT);
            setFocusPainted(false);
            setContentAreaFilled(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setMaximumSize(new Dimension(260, 50));

            addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) {
                    setForeground(Color.decode(ini.warna_isi));
                }

                public void mouseExited(MouseEvent e) {
                    setForeground(isActive ? Color.decode(ini.warna_isi) : Color.WHITE);
                }
            });
        }

        public void setActive(boolean active) {
            this.isActive = active;
            setForeground(active ? Color.decode(ini.warna_isi) : Color.WHITE);
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            if (isActive) {
                g2.setColor(Color.decode(ini.warna_isi));
                g2.fillRect(0, 5, 5, getHeight() - 10);

                g2.setColor(hoverColor);
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
            super.paintComponent(g);
        }
    }