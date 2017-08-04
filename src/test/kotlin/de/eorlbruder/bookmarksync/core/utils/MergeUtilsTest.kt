package de.eorlbruder.bookmarksync.core.utils

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
        assertTrue(MergeUtils.haveTagsChanged(sourceTags, targetTags))
        targetTags.add("Blafoo")
        assertTrue(MergeUtils.haveTagsChanged(sourceTags, targetTags))
        sourceTags.add("NewTag")
        assertFalse(MergeUtils.haveTagsChanged(sourceTags, targetTags))
    }

    @Test
    fun testChangedTitle() {
        assertTrue(true)
    }

    @Test
    fun testChangedDescription() {
        assertTrue(true)
    }

    @Test
    fun testEqualEntries() {
        assertTrue(true)
    }
}