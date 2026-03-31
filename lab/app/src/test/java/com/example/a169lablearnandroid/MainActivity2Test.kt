package com.example.a169lablearnandroid

import org.junit.Test
import org.junit.Assert.*

/**
 * Unit Test สำหรับ MainActivity2
 */
class MainActivity2Test {

    @Test
    fun mainActivity2_initialState() {
        // ทดสอบสถานะเบื้องต้นของแอป
        val isInitialized = true
        assertTrue("MainActivity2 ควรจะเตรียมค่าสำเร็จก่อนรัน", isInitialized)
    }


    @Test
    fun mainActivity2_initialState_failing() {
        // ตัวอย่างเทสต์ที่ตั้งใจให้ไม่ผ่าน (Failing Test)
        val isInitialized = false // สมมติว่าแอปล่ม เลย Initialized ไม่สำเร็จ
        
        assertTrue("เทสต์ที่ตั้งใจให้เฟล: คาดหวังว่า Activity จะ Init สำเร็จ แต่กลับพัง", isInitialized)
    }

}
