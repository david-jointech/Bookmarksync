package de.eorlbruder.wallabag_shaarli_connector

import org.junit.Test

import org.junit.Assert.*

class EntryMergerTest {

    @Test
    fun mergeEntries() {
//        val url0 = "http://www.faz.net/aktuell/g-20-gipfel/chaos-beim-g-20-gipfel-die-suenden-von-hamburg-15097227.html?xtor=EREC-7-[Der_Tag_am_Abend]-20170708"
//        val url1 = "http://www.faz.net/aktuell/g-20-gipfel/chaos-beim-g-20-gipfel-die-suenden-von-hamburg-15097227.html"
//        val url0 = "http://edition.cnn.com/2017/01/22/health/facebook-study-narrow-minded-trnd/index.html?utm_source=CNN+Five+Things&utm_campaign=cfc38bd561-EMAIL_CAMPAIGN_2017_01_23&utm_medium=email&utm_term=0_6da287d761-cfc38bd561-82789641"
//        val url1 = "http://edition.cnn.com/2017/01/22/health/facebook-study-narrow-minded-trnd/index.html"
        val url0 = "http://edition.cnn.com/2017/01/22/health/facebook-study-narrow-minded-trnd/index.html&amp;foo"
        val url1 = "http://edition.cnn.com/2017/01/22/health/facebook-study-narrow-minded-trnd/index.html&foo"

        assertTrue(EntryMerger(ArrayList<Entry>(), ArrayList<Entry>()).equalUrls(url0, url1))
    }

}