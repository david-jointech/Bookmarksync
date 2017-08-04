package de.eorlbruder.wallabag_shaarli_connector.core.utils

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class MergeUtilsTest {
    @Test
    fun testEqualTags() {
        val tags0 = HashSet<String>()
        tags0.add("Art")
        tags0.add("Wallabag")
        val tags1 = HashSet<String>()
        tags1.add("Art")
        assertTrue(MergeUtils.equalTags(tags1, tags0))
        tags1.add("Blafoo")
        assertFalse(MergeUtils.equalTags(tags1, tags0))
    }

    @Test
    fun testEqualEntries() {

    }
}