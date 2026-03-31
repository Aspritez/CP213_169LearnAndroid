package com.example.a169lablearnandroid

import org.junit.Test
import org.junit.Assert.*

/**
 * Unit Test สำหรับ GalleryActivity
 */
class GalleryActivityTest {

    @Test
    fun galleryImage_count_test() {
        // ทดสอบเงื่อนไขหรือการคำนวณที่อาจจะเกี่ยวข้องกับ Gallery Model
        // Arrange
        val imageGalleryCount = 5
        
        // Act & Assert
        assertTrue("ควรจะมีรูปภาพแสดงอยู่อย่างน้อย 1 รูป", imageGalleryCount > 0)
    }


    @Test
    fun galleryImage_count_failing() {
        // ตัวอย่างเทสต์ที่ตั้งใจให้ไม่ผ่าน (Failing Test)
        val imageGalleryCount = 0

        assertTrue("เทสต์ที่ตั้งใจให้เฟล: คาดหวังว่าต้องมีรูป (>0) แต่กลับไม่มีรูปเลย (0)", imageGalleryCount > 0)
    }

}
