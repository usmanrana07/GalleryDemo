package com.gallerydemo

import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun media_isVideoCorrect() {
        val mimeType = "video/mp4"
        assertEquals(true, mimeType.startsWith("video", true))
    }

    @Test
    fun media_isVideoInCorrect() {
        val mimeType = "image/jpeg"
        assertEquals(false, mimeType.startsWith("video", true))
    }
}