# FOCUS SHOT

**FOCUS SHOT** คือแอปพลิเคชันเกมฝึกทักษะการตอบสนองบนระบบปฏิบัติการ Android ที่ออกแบบมาเพื่อฝึกฝนและทดสอบการตอบสนองของผู้เล่น ผ่านเกมเพลย์ที่ท้าทายและสามารถปรับแต่งได้

## บทนำและฟีเจอร์หลัก

**จุดประสงค์ของโปรเจกต์:**
เนื่องจากตัวผมเองเป็นคนเล่นเกม FPS shooting game ทั่วไปเช่น Valorant จากนั้นจึงไปเจอโปรแกรมฝึกซ้อมคือ Aimlab และพบว่าทำให้ผมรู้สึกมีสมาธิและรู้สึกร่างกายมีความ active มากขึ้น จึงทำให้เกิดโปรเจกต์นี้ขึ้นมา โดยได้รับแรงบันดาลใจจากโหมด Gridshot ใน Aimlab ตัวเกมนี้เราจะเปลี่ยนเป้าหมายจากการฝึกความแม่นยำ มาเป็นการฝึกสมาธิและการจดจ่อผ่านการตอบสนองของผู้เล่นแทน

**Features (ฟีเจอร์หลัก):**
- **Multiple Game Modes:** รองรับโหมดการเล่นที่หลากหลาย ทั้งโหมด Gridshot (แตะเป้าหมายปกติ) และ Gyroscope Training (ใช้เซ็นเซอร์ Gyroscope ในการเล็ง)
- **Dynamic Targets:** เป้าหมาย (Circles) จะปรากฏขึ้นบนหน้าจอแบบสุ่มตำแหน่งในขอบเขตที่กำหนด
- **Instant Reaction:** ผู้ใช้ต้องแตะเป้าหมายให้หายไป เพื่อให้เป้าหมายใหม่ปรากฏขึ้นมาแทนที่ทันที
- **Time Challenge:** มีการกำหนดเวลาต่อรอบ พร้อมระบบนับถอยหลัง 3-2-1 ก่อนเริ่มเกม เพื่อทดสอบขีดจำกัดความเร็วและสมาธิ
- **Customizable Settings:** สามารถปรับแต่งสีของเป้าหมายและพื้นหลังผ่าน Hex Color, ปรับความไวของ Gyroscope และเลือกเสียงเอฟเฟกต์ (Sound Effects) ได้
- **Scoreboard & Metrics:** ระบบเก็บคะแนนที่แสดงข้อมูลแยกตามโหมดการเล่น พร้อมสถิติความแม่นยำ (Accuracy metrics) เพื่อติดตามพัฒนาการ

## โครงสร้างของโปรเจกต์ (Project Structure)
โปรเจกต์นี้พัฒนาด้วยสถาปัตยกรรมแบบ MVVM (Model-View-ViewModel) ร่วมกับ Jetpack Compose โดยมีโครงสร้างที่สำคัญดังนี้:

└── Project/
    ├── README.md
    ├── build.gradle.kts
    ├── settings.gradle.kts
    ├── gradle.properties
    ├── gradle/
    │   ├── libs.versions.toml
    │   └── wrapper/
    │       ├── gradle-wrapper.jar
    │       └── gradle-wrapper.properties
    └── app/
        ├── build.gradle.kts
        ├── proguard-rules.pro
        └── src/
            ├── androidTest (โฟลเดอร์สำหรับเขียนเทสต์บนอุปกรณ์จริง)/
            │   └── java/
            │       └── com...
            ├── test (โฟลเดอร์สำหรับเขียนเทสต์ลอจิกทั่วไป)/
            │   └── java/
            │       └── com...
            └── main/
                ├── AndroidManifest.xml
                ├── java/
                │   └── com/
                │       └── example/
                │           └── myapplication/
                │               ├── MainActivity.kt
                │               ├── audio/
                │               │   └── AudioController.kt
                │               ├── data/
                │               │   ├── local/
                │               │   │   └── DataStoreManager.kt
                │               │   └── model/
                │               │       └── Models.kt
                │               ├── ui/
                │               │   ├── screens/
                │               │   │   ├── GameScreen.kt
                │               │   │   ├── GyroscopeGameScreen.kt
                │               │   │   ├── HomeScreen.kt
                │               │   │   ├── ScoreboardScreen.kt
                │               │   │   └── SettingsScreen.kt
                │               │   └── theme/
                │               │       ├── Color.kt
                │               │       ├── Theme.kt
                │               │       └── Type.kt
                │               └── viewmodel/
                │                   ├── GameViewModel.kt
                │                   ├── GyroscopeViewModel.kt
                │                   └── MainViewModel.kt
                ├── res/
                │   ├── drawable/
                │   │   ├── cover.png
                │   │   ├── logo.png
                │   │   ├── ic_launcher_background.xml
                │   │   └── ic_launcher_foreground.xml
                │   ├── mipmap-anydpi-v26
                │   ├── mipmap-hdpi
                │   ├── mipmap-mdpi
                │   ├── mipmap-xhdpi
                │   ├── mipmap-xxhdpi
                │   ├── mipmap-xxxhdpi
                │   ├── values/
                │   │   ├── colors.xml
                │   │   ├── strings.xml
                │   │   └── themes.xml
                │   └── xml/
                │       ├── backup_rules.xml
                │       └── data_extraction_rules.xml
                └── AndroidManifest.xml

## เทคโนโลยีที่ใช้ (Tech Stack)
- **ภาษา:** Kotlin
- **UI Framework:** Jetpack Compose (Declarative UI)
- **Architecture & State Management:** ViewModel, Lifecycle ViewModel Compose
- **Navigation:** Navigation Compose
- **Local Storage:** DataStore Preferences, Gson
- **Hardware/Sensors:** Android Sensor API (Gyroscope)
- **เครื่องมือการพัฒนา:** Android Studio

## การเริ่มต้นใช้งาน (Getting Started)

**สิ่งที่ต้องติดตั้งก่อน (Prerequisites):**
- Android Studio (แนะนำเวอร์ชันล่าสุดที่มีการรองรับ Jetpack Compose)
- อุปกรณ์ Android หรือ Emulator ที่มี API Level 24 (Android 7.0) ขึ้นไป (Target API 34)

**ขั้นตอนการติดตั้ง (Installation):**
1. โคลนโปรเจกต์ลงในเครื่อง: `git clone <YOUR_REPOSITORY_URL>`
2. เปิดโฟลเดอร์โปรเจกต์ผ่าน Android Studio
3. รอจนกว่าระบบจะทำ Gradle Sync เสร็จสมบูรณ์
4. เลือก Emulator หรือเสียบอุปกรณ์ Android ของคุณ
5. กดปุ่ม Run (Shift + F10) เพื่อรันและทดสอบแอปพลิเคชัน

## ข้อมูลการติดต่อ (Contact / Author)
- **ชื่อผู้พัฒนา:** Bhakin31
- **GitHub Profile:** [Bhakin31](https://github.com/Bhakin31)
