package com.jk.apisdemo

import com.jk.apisdemo.impl.LogDbHelper
import com.jk.apisdemo.impl.LogDbServer
import org.junit.Assert.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment


@RunWith(RobolectricTestRunner::class)
class LogDbTest {
    private var dbServer: LogDbServer? = null

    @Before
    fun setUp() {
        val context = RuntimeEnvironment.application
        dbServer = LogDbServer(context)
    }

    @After
    fun finish() {
        dbServer?.onDestroy()
    }

    @Test
    fun testInsert() {
        val content = "test response"
        val ret = dbServer?.insert(LogDbHelper.STATUS_OK, content)
        assertNotEquals(ret, -1)
        val logs = dbServer?.query(true)
        assertEquals(logs?.get(0)?.status, LogDbHelper.STATUS_OK)
        assertEquals(logs?.get(0)?.response, content)
    }

    @Test
    fun testQuery() {
        val content1 = "test response1"
        val content0 = "test response0"
        dbServer?.insert(LogDbHelper.STATUS_OK, content1)
        dbServer?.insert(LogDbHelper.STATUS_NOK, content0)
        val logs = dbServer?.query(false)
        assertEquals(logs?.get(0)?.response, content0)
        assertEquals(logs?.get(0)?.status, LogDbHelper.STATUS_NOK)
        assertEquals(logs?.get(1)?.response, content1)
        assertEquals(logs?.get(1)?.status, LogDbHelper.STATUS_OK)
    }

}