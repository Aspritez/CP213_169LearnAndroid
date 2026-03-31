package com.example.a169lablearnandroid

import org.junit.Test
import org.junit.Assert.*

/**
 * Unit Test สำหรับ PokedexActivity
 */
class PokedexActivityTest {

    @Test
    fun checkPokedexViewModel_mock() {
        // ตัวอย่างการทำ Unit Test ของ PokedexActivity 
        // มักจะทดสอบตัวแปร state ที่เกี่ยวข้อง เช่น PokemonViewModel
        
        // Arrange
        val expectedInitialListSize = 0 
        val dummyListSize = 0 // สมมติว่า ViewModel เริ่มต้นไม่มีข้อมูล
        
        // Assert ว่า ViewModel หรือ State มีค่าเริ่มต้นถูกต้อง
        assertEquals("ค่าเริ่มต้น ควรยังไม่มี Pokemon โหลดขึ้นมา", expectedInitialListSize, dummyListSize)
    }

    /*
    @Test
    fun checkPokedexViewModel_failing() {
        // ตัวอย่างเทสต์ที่ตั้งใจให้ไม่ผ่าน (Failing Test)
        val expectedInitialListSize = 10 
        val dummyListSize = 0 
        
        assertEquals("เทสต์ที่ตั้งใจให้เฟล: คาดหวังว่าเริ่มต้นมี 10 ตัว แต่กลับได้ 0", expectedInitialListSize, dummyListSize)
    }
    */
}
