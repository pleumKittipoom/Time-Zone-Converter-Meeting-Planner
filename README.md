# Time Zone Converter + Meeting Planner (Swing, Java)

แอปเดสก์ท็อป (Swing) สำหรับ

แปลงเวลา ระหว่างเมือง/โซนเวลา และ

วางแผนเวลาประชุมข้ามประเทศ โดยค้นหา “ช่วงที่ทุกเมืองยังอยู่ในเวลางาน”

ใช้ java.time จัดการโซนเวลา/DST อัตโนมัติ, ข้อมูลเมืองแก้ได้ใน cities.csv, และมีโหมด Admin เพิ่มเมืองผ่าน UI

# ✨ ฟีเจอร์หลัก

Convert Time: กรอก YYYY-MM-DD HH:mm + เลือกเมืองต้นทาง/ปลายทาง → ได้เวลาท้องถิ่นปลายทาง

Plan Meeting: เลือกหลายเมือง → ระบบสไลด์หน้าต่างเวลา (ตาม duration และ step) บนเส้นเวลา UTC เพื่อหา slot ที่ทุกเมือง อยู่ในเวลางาน (รองรับกะข้ามคืน)

Admin → Add City…: เพิ่ม/แก้เมืองใหม่ลง cities.csv จากในแอป (รหัสตัวอย่าง: admin123)

รีโหลดข้อมูลเมืองจากไฟล์ได้ทันที (ไม่ต้องคอมไพล์ใหม่)

UI รองรับภาษาไทย (ตั้งฟอนต์อัตโนมัติ)

# 📂 โครงสร้างโปรเจกต์
tz-planner-swing/
├─ cities.csv
└─ src/app/
   ├─ Main.java                # entry point
   ├─ MainFrame.java           # หน้าต่างหลัก + เมนู Admin
   ├─ ConvertPanel.java        # แท็บแปลงเวลา
   ├─ PlanPanel.java           # แท็บวางแผนประชุม (ตารางผลลัพธ์)
   ├─ SlotTableModel.java      # TableModel สำหรับผลลัพธ์
   ├─ AddCityDialog.java       # ไดอะล็อกเพิ่มเมือง (Admin)
   ├─ City.java, WorkHours.java
   ├─ CityIndex.java           # โหลด/บันทึก cities.csv (+ upsert, saveToCsv)
   ├─ TimeZoneService.java     # แปลงโซนเวลา
   ├─ MeetingPlanner.java      # อัลกอริทึมค้นหา slot (ตรวจวัน+เวลา)
   └─ Ui.java                  # ตั้งค่า LAF/ฟอนต์ไทย

# 🧰 ขึ้นก่อนใช้งาน (Prerequisites)

JDK 21 ขึ้นไป
Windows (CMD/PowerShell):

winget install --id EclipseAdoptium.Temurin.21.JDK -e


ตรวจสอบ:

java -version
javac -version


ถ้า javac ไม่เจอ: ตั้ง JAVA_HOME ไปที่ C:\Program Files\Eclipse Adoptium\jdk-21... และเพิ่ม %JAVA_HOME%\bin ใน Path (ปิด/เปิด VS Code/Terminal ใหม่)

# 🚀 การคอมไพล์และรัน
## Windows
javac -encoding UTF-8 -d out src\app\*.java
java  -Dfile.encoding=UTF-8 -cp out app.Main

## macOS / Linux
javac -encoding UTF-8 -d out src/app/*.java
java  -Dfile.encoding=UTF-8 -cp out app.Main


VS Code ใช้ได้เช่นกัน: ติดตั้ง Extension Pack for Java → เปิด Main.java แล้วกด ▶️ Run

# ⚙️ ไฟล์ cities.csv

รูปแบบคอลัมน์:
code,name,zoneId,workStart,workEnd

ตัวอย่าง:

BKK,Bangkok,Asia/Bangkok,09:00,17:00
NYC,New York,America/New_York,09:00,17:00
TYO,Tokyo,Asia/Tokyo,09:00,17:00


zoneId ต้องเป็นค่าในฐานข้อมูลของ Java (เช่น Europe/London, Asia/Bangkok)

workStart/End ใช้รูปแบบ HH:mm

หลีกเลี่ยงเครื่องหมายจุลภาค (,) ใน name เพื่อไม่ให้ CSV เพี้ยน

เพิ่มเมืองผ่าน UI (Admin)

เมนูบาร์ → Admin → Add City…
กรอกรหัสผู้ดูแล: admin123 (ตัวอย่าง) → กรอกข้อมูล → บันทึก
ระบบจะเขียนกลับ cities.csv และรีโหลดรายการเมืองอัตโนมัติ

# 🧭 วิธีใช้งาน
## Convert Time

ใส่วัน-เวลาท้องถิ่นของเมืองต้นทาง (รูปแบบ YYYY-MM-DD HH:mm)

เลือกเมือง From/To

กด แปลงเวลา

## Plan Meeting

เลือกหลายเมือง (Ctrl/Shift เลือกหลายรายการ)

ตั้งค่า:

วันเริ่มค้นหา (เช่น 2025-10-22)

ความยาว (นาที) = duration

ก้าวเวลา step (นาที) = ความละเอียดการค้นหา (เช่น 15/30/60)

ค้นหากี่วันถัดไป = days

จำนวนผลลัพธ์สูงสุด

กด ค้นหาเวลาประชุม → ตารางจะแสดง UTC และเวลาท้องถิ่นของแต่ละเมือง

ทิป:

step เล็ก = ค้นละเอียด (ช้าลงเล็กน้อย), step ใหญ่ = ไวกว่าแต่อาจพลาดช่วงเริ่ม 09:15/09:30

หากใช้กะข้ามคืน เช่น 22:00–06:00 ให้ตั้ง workStart=22:00, workEnd=06:00 (ระบบรองรับข้ามวัน)

# 🧠 หลักการทำงาน (คร่าว ๆ)

สร้างเส้นเวลา UTC จากเที่ยงคืนของ startDate → ไถไปทีละ step จนถึง days

สำหรับ slot [cursorUtc, cursorUtc + duration) แปลงเป็นเวลา ท้องถิ่น ของทุกเมือง

ตรวจว่า ทั้งช่วง อยู่ภายใน workStart–workEnd ของ “วันนั้น” ในโซนของเมืองนั้น
(รองรับกรณี workEnd < workStart = กะข้ามคืน)

ถ้าทุกเมืองผ่าน → เก็บเป็นผลลัพธ์
