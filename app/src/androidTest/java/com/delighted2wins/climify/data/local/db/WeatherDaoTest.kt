package com.delighted2wins.climify.data.local.db

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.matcher.ViewMatchers.assertThat
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.delighted2wins.climify.domainmodel.Alarm
import com.delighted2wins.climify.utils.Constants
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.hamcrest.Matchers.`is`
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@SmallTest
class WeatherDaoTest {
    private lateinit var database: WeatherDatabase
    private lateinit var dao: WeatherDao

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(), //context
            WeatherDatabase::class.java
        ).build()
        dao = database.getWeatherDao()
    }

    @After
    fun tearDown() = database.close()

    @Test
    fun insertAlarm_returnsValidId() = runTest {
        // given
        val alarm = Alarm(
            tag = "1",
            startDuration = 2000,
            endDuration = 3000,
            type = Constants.TYPE_NOTIFICATION
        )
        // when
        val isInserted = dao.insertAlarm(alarm) > 0
        val  insertedAlarm = dao.getAllAlarms().first()[0]
        // then
        assertTrue(isInserted)
        assertThat(insertedAlarm.tag, `is`(alarm.tag))
        assertThat(insertedAlarm.type, `is`(alarm.type))
        assertThat(insertedAlarm.endDuration, `is`(alarm.endDuration))
        assertThat(insertedAlarm.startDuration, `is`(alarm.startDuration))
    }

    @Test
    fun deleteAlarm_returnsCorrectRowCount() = runTest {
        // given
        val alarm = Alarm(
            tag = "1",
            startDuration = 2000,
            endDuration = 3000,
            type = Constants.TYPE_NOTIFICATION
        )
        // when
        val isInserted = dao.insertAlarm(alarm) > 0
        val deletedId = dao.deleteAlarm(alarm)
        val alarms = dao.getAllAlarms().first().isEmpty()

        // then
        assertTrue(isInserted)
        assertTrue(alarms)
        assertThat(deletedId, `is`(1))
    }
}