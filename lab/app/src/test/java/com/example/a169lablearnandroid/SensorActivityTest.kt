package com.example.a169lablearnandroid

import org.junit.Test
import org.junit.Assert.*

/**
 * Unit Test สำหรับ SensorActivity
 */
class SensorActivityTest {

    @Test
    fun sensorLogic_calculation() {
        // ทดสอบการคำนวณหรือตัวแปรควบคุมจำลองของเซ็นเซอร์
        val mockGravityValueX = 1.5f
        
        assertNotNull("ตัวแปรความเคลื่อนไหวเซ็นเซอร์จะต้องถูกตรวจสอบได้", mockGravityValueX)
    }

    /*
    @Test
    fun sensorLogic_calculation_failing() {
        // ตัวอย่างเทสต์ที่ตั้งใจให้ไม่ผ่าน (Failing Test)
        val mockGravityValueX: Float? = null // สมมติว่าอ่านค่าเซ็นเซอร์ไม่ได้แล้วเป็น null
        
        assertNotNull("เทสต์ที่ตั้งใจให้เฟล: คาดหวังว่าต้องมีค่าเซ็นเซอร์ แต่กลับเป็น null", mockGravityValueX)
    }
    */
}
