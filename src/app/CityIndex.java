package app;

import java.io.BufferedReader;
import java.nio.file.*;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.*;

public final class CityIndex {
    private final Path csvPath;
    private final Map<String, City> byCode = new HashMap<>();

    public CityIndex(Path csvPath) {
        this.csvPath = csvPath;
        load();
    }

    public void reload() { load(); }

    private void load() {
        byCode.clear();
        if (!Files.exists(csvPath)) {
            System.out.println("⚠️  ไม่พบไฟล์ cities.csv จะใช้ชุดค่าเริ่มต้น");
            loadDefaults();
            return;
        }
        try (BufferedReader br = Files.newBufferedReader(csvPath)) {
            String header = br.readLine(); // skip header
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;
                String[] t = line.split(",", -1);
                if (t.length < 5) continue;
                String code = t[0].trim().toUpperCase();
                String name = t[1].trim();
                ZoneId zone = ZoneId.of(t[2].trim());
                java.time.LocalTime ws = java.time.LocalTime.parse(t[3].trim());
                java.time.LocalTime we = java.time.LocalTime.parse(t[4].trim());
                City c = new City(code, name, zone, new WorkHours(ws, we));
                byCode.put(code, c);
            }
            if (byCode.isEmpty()) loadDefaults();
        } catch (Exception e) {
            System.out.println("อ่านไฟล์ไม่สำเร็จ: " + e.getMessage());
            loadDefaults();
        }
    }

    private void loadDefaults() {
        put("BKK","Bangkok","Asia/Bangkok","09:00","17:00");
        put("SFO","San Francisco","America/Los_Angeles","09:00","17:00");
        put("NYC","New York","America/New_York","09:00","17:00");
        put("LON","London","Europe/London","09:00","17:00");
        put("PAR","Paris","Europe/Paris","09:00","17:00");
        put("BER","Berlin","Europe/Berlin","09:00","17:00");
        put("TYO","Tokyo","Asia/Tokyo","09:00","17:00");
        put("SGP","Singapore","Asia/Singapore","09:00","17:00");
        put("SYD","Sydney","Australia/Sydney","09:00","17:00");
        put("DEL","Delhi","Asia/Kolkata","09:00","17:00");
        put("SHA","Shanghai","Asia/Shanghai","09:00","17:00");
    }

    private void put(String code, String name, String zone, String ws, String we) {
        City c = new City(code, name, ZoneId.of(zone),
                new WorkHours(java.time.LocalTime.parse(ws), java.time.LocalTime.parse(we)));
        byCode.put(code, c);
    }

    public Optional<City> find(String code) {
        return Optional.ofNullable(byCode.get(code.toUpperCase()));
    }

    public List<City> all() {
        return byCode.values().stream().sorted(Comparator.comparing(City::code)).toList();
    }

        // มีรหัสนี้อยู่แล้วหรือไม่
    public boolean exists(String code) {
        return byCode.containsKey(code.toUpperCase());
    }

    // เพิ่ม/แก้เมืองในหน่วยความจำ
    public synchronized void upsert(City c) {
        byCode.put(c.code().toUpperCase(), c);
    }

    // เขียน cities.csv ทั้งไฟล์ใหม่ (ปลอดภัยกว่า append)
    public synchronized void saveToCsv() {
        try {
            if (csvPath.getParent() != null) Files.createDirectories(csvPath.getParent());
            try (java.io.BufferedWriter w = Files.newBufferedWriter(csvPath)) {
                w.write("code,name,zoneId,workStart,workEnd\n");
                for (City c : this.all()) {
                    // หมายเหตุ: หลีกเลี่ยงจุลภาคใน name (ถ้ามีจะตัดเป็นช่อง) — ควรไม่ใช้ , ในชื่อ
                    w.write(String.format("%s,%s,%s,%s,%s%n",
                            c.code(), c.name(), c.zone().getId(),
                            c.work().start(), c.work().end()));
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("บันทึก cities.csv ไม่สำเร็จ: " + e.getMessage(), e);
        }
    }

}
