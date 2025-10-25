// ConvertPanel.java
package app;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ConvertPanel extends JPanel {
    private final CityIndex index;
    private final TimeZoneService tz;

    // เปลี่ยนเป็น JComboBox<City>
    private final JComboBox<City> fromBox;
    private final JComboBox<City> toBox;

    private final JTextField dtField; // "yyyy-MM-dd HH:mm"
    private final JTextArea resultArea;

    public ConvertPanel(CityIndex index, TimeZoneService tz) {
        this.index = index;
        this.tz = tz;
        setLayout(new BorderLayout(10, 10));

        fromBox = new JComboBox<>(index.all().toArray(new City[0]));
        toBox = new JComboBox<>(index.all().toArray(new City[0]));

        // เรนเดอร์เป็น "CODE (Name)"
        ListCellRenderer<? super City> renderer = new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
                    boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof City c)
                    setText(c.code() + " (" + c.name() + ")");
                return this;
            }
        };
        fromBox.setRenderer(renderer);
        toBox.setRenderer(renderer);

        dtField = new JTextField(16);
        dtField.setText(java.time.LocalDateTime.now().withSecond(0).withNano(0).toString().replace('T', ' '));
        resultArea = new JTextArea(6, 40);
        resultArea.setEditable(false);

        JButton convertBtn = new JButton("แปลงเวลา");
        convertBtn.addActionListener(e -> doConvert());

        JPanel top = new JPanel(new GridBagLayout());
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(5, 5, 5, 5);
        gc.anchor = GridBagConstraints.WEST;

        int r = 0;
        gc.gridx = 0;
        gc.gridy = r;
        top.add(new JLabel("เวลา (YYYY-MM-DD HH:mm):"), gc);
        gc.gridx = 1;
        top.add(dtField, gc);
        r++;

        gc.gridx = 0;
        gc.gridy = r;
        top.add(new JLabel("จากเมือง:"), gc);
        gc.gridx = 1;
        top.add(fromBox, gc);
        r++;

        gc.gridx = 0;
        gc.gridy = r;
        top.add(new JLabel("เป็นเมือง:"), gc);
        gc.gridx = 1;
        top.add(toBox, gc);
        r++;

        gc.gridx = 1;
        gc.gridy = r;
        top.add(convertBtn, gc);

        add(top, BorderLayout.NORTH);
        add(new JScrollPane(resultArea), BorderLayout.CENTER);
    }

    private void doConvert() {
        try {
            String dt = dtField.getText().trim();
            LocalDateTime ldt = LocalDateTime.parse(dt, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
            City from = (City) fromBox.getSelectedItem();
            City to = (City) toBox.getSelectedItem();
            var out = tz.convert(ldt, from, to);
            String msg = String.format(
                    "อินพุต: %s (%s / %s)\nผลลัพธ์: %s %s (%s)\n",
                    dt, from.code(), from.zone().getId(),
                    out.toLocalDate(), out.toLocalTime().withNano(0), to.zone().getId());
            resultArea.setText(msg);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "รูปแบบเวลาไม่ถูกต้อง หรือเมืองไม่พบ\n" + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void refreshCities() {
        ListCellRenderer<? super City> renderer = fromBox.getRenderer();
        fromBox.setModel(new DefaultComboBoxModel<>(index.all().toArray(new City[0])));
        toBox.setModel(new DefaultComboBoxModel<>(index.all().toArray(new City[0])));
        fromBox.setRenderer(renderer);
        toBox.setRenderer(renderer);
    }

}
