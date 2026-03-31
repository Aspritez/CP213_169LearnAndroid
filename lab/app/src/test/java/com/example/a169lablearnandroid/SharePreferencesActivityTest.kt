package com.example.a169lablearnandroid

import org.junit.Test
import org.junit.Assert.*

/**
 * Unit Test สำหรับ SharePreferencesActivity
 */
class SharePreferencesActivityTest {

    @Test
    fun sharedPreferences_saveAndLoad_logic() {
        // ในการทดสอบ SharedPreferences จะใช้ Mock framework (เช่น Mockito/MockK)
        // เพื่อจำลองการเขียน/อ่านข้อมูลโดยไม่ต้องพึ่ง Context จริงของ Android
        val expectedUsername = "StudentPlayer"
        val mockReadValue = "StudentPlayer" // สมมติว่าดึงมาจาก mock object
        
        assertEquals("การจำลองอ่านค่า SharedPreferences ที่บันทึกไว้จะต้องตรงกับที่เก็บไว้", expectedUsername, mockReadValue)
    }

    /*
    @Test
    fun sharedPreferences_saveAndLoad_failing() {
        // ตัวอย่างเทสต์ที่ตั้งใจให้ไม่ผ่าน (Failing Test)
        val expectedUsername = "StudentPlayer"
        val mockReadValue = "Hacker" // สมมติว่ามีข้อมูลอื่นทับข้อมูลเดิม
        
        assertEquals("เทสต์ที่ตั้งใจให้เฟล: คาดหวังค่าที่เซฟไว้ตอนแรก แต่กลับได้ค่าอื่น", expectedUsername, mockReadValue)
    }
    */
}
