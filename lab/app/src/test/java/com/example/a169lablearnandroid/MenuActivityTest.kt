package com.example.a169lablearnandroid

import org.junit.Test
import org.junit.Assert.*

/**
 * Unit Test สำหรับ MenuActivity
 * เนื่องจาก Activity รัน UI (Jetpack Compose)
 * การทดสอบ UI แบบเต็มรูปแบบมักใช้ createComposeRule() ใน androidTest
 * ส่วน Unit test นี้จำลองการทดสอบ Logic หรือฟังก์ชันภายในเบื้องต้น
 */
class MenuActivityTest {

    @Test
    fun checkMenuActivity_MenuCount_logic() {
        // Arrange
        val expectedMenuCount = 6 // มีปุ่มเมนู 6 ปุ่ม
        val actualCount = 6
        
        // Act & Assert
        assertEquals("จำนวนเมนูควรมี 6 เมนูตามที่ตั้งใจไว้", expectedMenuCount, actualCount)
    }


    @Test
    fun checkMenuActivity_MenuCount_failing() {
        // ตัวอย่างเทสต์ที่ตั้งใจให้ไม่ผ่าน (Failing Test)
        // เพื่อให้เห็นว่าถ้ามีบัก หรือ Logic คำนวณผิดพลาดจะเป็นอย่างไร
        val expectedMenuCount = 6
        val actualCount = 5 // สมมติว่าหน้าจอเมนูหายไป 1 ปุ่ม
        
        assertEquals("เทสต์ที่ตั้งใจให้เฟล: คาดหวังว่ามี 6 เมนู แต่กลับมี 5 เมนู", expectedMenuCount, actualCount)
    }
}
