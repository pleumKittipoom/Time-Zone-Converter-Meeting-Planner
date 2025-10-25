package app;

import javax.swing.*;
import java.awt.*;
import java.util.Enumeration;

public final class Ui {
    private Ui() {
    }

    public static void initLookAndFeel() {
        try {
            // ใช้ LAF ของระบบปฏิบัติการ (Windows จะได้ Segoe UI/Leelawadee UI ที่รองรับไทย)
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }

        // บังคับฟอนต์ให้เป็นฟอนต์ที่รองรับไทย
        setGlobalFont(pickThaiFont(14));
    }

    // เลือกฟอนต์ที่มีในเครื่องโดยเรียงลำดับความน่าจะมี
    private static Font pickThaiFont(int size) {
        String[] candidates = {
                "Leelawadee UI", "Segoe UI", "Tahoma", "Noto Sans Thai", "Sarabun", "Microsoft Sans Serif"
        };
        java.util.Set<String> avail = new java.util.HashSet<>(
                java.util.Arrays.asList(java.awt.GraphicsEnvironment
                        .getLocalGraphicsEnvironment().getAvailableFontFamilyNames()));
        for (String name : candidates) {
            if (avail.contains(name))
                return new Font(name, Font.PLAIN, size);
        }
        // ถ้าไม่เจอเลย ใช้ SansSerif (ส่วนใหญ่ก็รองรับ)
        return new Font("SansSerif", Font.PLAIN, size);
    }

    public static void setGlobalFont(Font f) {
        javax.swing.plaf.FontUIResource r = new javax.swing.plaf.FontUIResource(f);
        java.util.Enumeration<Object> keys = UIManager.getDefaults().keys();
        while (keys.hasMoreElements()) {
            Object key = keys.nextElement();
            Object val = UIManager.get(key);
            if (val instanceof Font)
                UIManager.put(key, r);
        }
    }

    public static JPanel row(Component... comps) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));
        for (Component c : comps)
            p.add(c);
        return p;
    }

    public static JPanel col(Component... comps) {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        for (Component c : comps) {
            c.setMaximumSize(new Dimension(Integer.MAX_VALUE, c.getPreferredSize().height));
            p.add(c);
        }
        return p;
    }
}
