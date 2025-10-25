# Time Zone Converter & Meeting Planner (Java Swing)

> Desktop app สำหรับ **แปลงเวลา** และ **หาช่วงประชุมข้ามโซนเวลา** ด้วย `java.time` (รองรับ DST) + จัดการเมืองผ่าน **Admin UI** (บันทึกลง `cities.csv`)

![JDK](https://img.shields.io/badge/JDK-21%2B-blue)
![GUI](https://img.shields.io/badge/GUI-Swing-informational)
![License](https://img.shields.io/badge/License-MIT-green)

---

## 📸 Screenshots
> (วางภาพไว้ใน `docs/screenshots/` แล้วแก้พาธได้ตามสะดวก)
- Convert Time  
  ![Convert](docs/screenshots/convert.png)
- Plan Meeting  
  ![Plan](docs/screenshots/plan.png)

---

## 🧭 Table of Contents
- [Features](#features)
- [Quick Start](#quick-start)
- [Project Structure](#project-structure)
- [Configuration — `cities.csv`](#configuration--citiescsv)
- [Admin (Add/Update City)](#admin-addupdate-city)
- [How Planning Works](#how-planning-works)
- [UML Diagrams](#uml-diagrams)
- [Troubleshooting](#troubleshooting)
- [Roadmap](#roadmap)
- [License](#license)

---

## ✨ Features
- ✅ **Convert Time** — กรอก `YYYY-MM-DD HH:mm` + เลือก From/To → แสดงเวลาปลายทางทันที  
- ✅ **Plan Meeting** — เลือกหลายเมือง → กวาดเวลาบนแกน **UTC** ทีละ `step` ตรวจว่าทุกเมืองอยู่ใน `work hours`  
- ✅ **Overnight Work Hours** — รองรับกะข้ามคืน (เช่น 22:00–06:00)  
- ✅ **Admin UI** — เพิ่ม/แก้เมืองจากเมนู (เขียนกลับ `cities.csv`)  
- ✅ **Thai-friendly UI** — ตั้งค่า LAF/Font ให้แสดงผลภาษาไทยได้ดี

---

## ⚡ Quick Start

### 1) Prerequisites
- **JDK 21+**  
  Windows:
  ```bat
  winget install --id EclipseAdoptium.Temurin.21.JDK -e
  java -version
  javac -version

---

### 2) Build & Run

- Windows

javac -encoding UTF-8 -d out src\app\*.java
java  -Dfile.encoding=UTF-8 -cp out app.Main


- macOS / Linux

javac -encoding UTF-8 -d out src/app/*.java
java  -Dfile.encoding=UTF-8 -cp out app.Main


ใช้ VS Code ก็ได้: ติดตั้ง “Extension Pack for Java” → เปิด Main.java → ▶ Run
