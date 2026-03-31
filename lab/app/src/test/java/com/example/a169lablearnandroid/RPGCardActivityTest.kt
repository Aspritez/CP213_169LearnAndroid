package com.example.a169lablearnandroid

import org.junit.Test
import org.junit.Assert.*

/**
 * Unit Test สำหรับ RPGCardActivity
 */
class RPGCardActivityTest {

    @Test
    fun checkCardStats_validation() {
        // ทดสอบและตรวจสอบความถูกต้องของสเตตัสการ์ด RPG 
        // (ส่วนใหญ่มักจะดึงคลาส Model หรือ ViewModel ออกมาเทสต์แยก)
        val cardHp = 100
        val cardAttack = 50
        
        assertTrue("HP หรือเลือดต้องมีค่ามากกว่า 0", cardHp > 0)
        assertTrue("พลังโจมตีไม่ควรติดลบ", cardAttack >= 0)
    }

    /*
    @Test
    fun checkCardStats_validation_failing() {
        // ตัวอย่างเทสต์ที่ตั้งใจให้ไม่ผ่าน (Failing Test)
        val cardHp = -10 // สมมติว่าบั๊กทำให้ HP เริ่มต้นติดลบ
        val cardAttack = -5
        
        assertTrue("เทสต์ที่ตั้งใจให้เฟล: เลือดการ์ดไม่ควรติดลบ", cardHp > 0)
        assertTrue("เทสต์ที่ตั้งใจให้เฟล: พลังโจมตีไม่ควรติดลบ", cardAttack >= 0)
    }
    */
}
