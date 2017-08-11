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
        assertFalse(MergeUtils.haveTagsChanged(sourceTags, targetTags))
        targetTags.add("Blafoo")
        assertFalse(MergeUtils.haveTagsChanged(sourceTags, targetTags))
        sourceTags.add("NewTag")
        assertTrue(MergeUtils.haveTagsChanged(sourceTags, targetTags))
    }

    @Test
    fun testChangedTitle() {
        assertTrue(false)
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