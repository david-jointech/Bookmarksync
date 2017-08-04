package de.eorlbruder.wallabag_shaarli_connector.core.utils

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class MergeUtilsTest {
    @Test
    fun testContainsAllTags() {
        val sourceTags = HashSet<String>()
        sourceTags.add("Art")
        val targetTags = HashSet<String>()
        targetTags.add("Art")
        targetTags.add("Wallabag")
        assertTrue(MergeUtils.containsAllTags(sourceTags, targetTags))
        targetTags.add("Blafoo")
        assertTrue(MergeUtils.containsAllTags(sourceTags, targetTags))
        sourceTags.add("NewTag")
        assertFalse(MergeUtils.containsAllTags(sourceTags, targetTags))
    }

    @Test
    fun testEqualEntries() {

    }
}