package com.example.historycalendar.domain

import kotlin.math.PI
import kotlin.math.floor
import kotlin.math.sin
import java.time.LocalDate

object LunarCalendarUtils {
    fun solarToLunar(date: LocalDate, timeZone: Double = 7.0): LunarDate {
        val dayNumber = jdFromDate(date.dayOfMonth, date.monthValue, date.year)
        val k = floor((dayNumber - 2415021.076998695) / 29.530588853)
        var monthStart = getNewMoonDay(k + 1, timeZone)
        if (monthStart > dayNumber) monthStart = getNewMoonDay(k, timeZone)
        var a11 = getLunarMonth11(date.year, timeZone)
        var b11 = a11
        var lunarYear: Int
        if (a11 >= monthStart) {
            lunarYear = date.year
            a11 = getLunarMonth11(date.year - 1, timeZone)
        } else {
            lunarYear = date.year + 1
            b11 = getLunarMonth11(date.year + 1, timeZone)
        }
        val lunarDay = dayNumber - monthStart + 1
        val diff = floor((monthStart - a11) / 29.0).toInt()
        var lunarLeap = false
        var lunarMonth = diff + 11
        if (b11 - a11 > 365) {
            val leapMonthDiff = getLeapMonthOffset(a11, timeZone)
            if (diff >= leapMonthDiff) {
                lunarMonth = diff + 10
                if (diff == leapMonthDiff) lunarLeap = true
            }
        }
        if (lunarMonth > 12) lunarMonth -= 12
        if (lunarMonth >= 11 && diff < 4) lunarYear -= 1
        return LunarDate(lunarDay, lunarMonth, lunarYear, lunarLeap)
    }

    private fun INT(d: Double): Int = floor(d).toInt()

    private fun jdFromDate(dd: Int, mm: Int, yy: Int): Int {
        val a = INT((14 - mm) / 12.0)
        val y = yy + 4800 - a
        val m = mm + 12 * a - 3
        var jd = dd + INT((153 * m + 2) / 5.0) + 365 * y + INT(y / 4.0) - INT(y / 100.0) + INT(y / 400.0) - 32045
        if (jd < 2299161) {
            jd = dd + INT((153 * m + 2) / 5.0) + 365 * y + INT(y / 4.0) - 32083
        }
        return jd
    }

    private fun getNewMoonDay(k: Double, timeZone: Double): Int {
        val T = k / 1236.85
        val T2 = T * T
        val T3 = T2 * T
        val dr = PI / 180
        var jd1 = 2415020.75933 + 29.53058868 * k + 0.0001178 * T2 - 0.000000155 * T3
        jd1 += 0.00033 * sin((166.56 + 132.87 * T - 0.009173 * T2) * dr)
        val M = 359.2242 + 29.10535608 * k - 0.0000333 * T2 - 0.00000347 * T3
        val Mpr = 306.0253 + 385.81691806 * k + 0.0107306 * T2 + 0.00001236 * T3
        val F = 21.2964 + 390.67050646 * k - 0.0016528 * T2 - 0.00000239 * T3
        var c1 = (0.1734 - 0.000393 * T) * sin(M * dr) + 0.0021 * sin(2 * dr * M)
        c1 -= 0.4068 * sin(Mpr * dr) + 0.0161 * sin(dr * 2 * Mpr)
        c1 -= 0.0004 * sin(dr * 3 * Mpr)
        c1 += 0.0104 * sin(dr * 2 * F) - 0.0051 * sin(dr * (M + Mpr))
        c1 -= 0.0074 * sin(dr * (M - Mpr)) + 0.0004 * sin(dr * (2 * F + M))
        c1 -= 0.0004 * sin(dr * (2 * F - M)) - 0.0006 * sin(dr * (2 * F + Mpr))
        c1 += 0.0010 * sin(dr * (2 * F - Mpr)) + 0.0005 * sin(dr * (2 * Mpr + M))
        val deltat = if (T < -11) {
            0.001 + 0.000839 * T + 0.0002261 * T2 - 0.00000845 * T3 - 0.000000081 * T * T3
        } else {
            -0.000278 + 0.000265 * T + 0.000262 * T2
        }
        val jdNew = jd1 + c1 - deltat
        return INT(jdNew + 0.5 + timeZone / 24)
    }

    private fun getSunLongitude(jdn: Int, timeZone: Double): Int {
        val T = (jdn - 2451545.5 - timeZone / 24) / 36525
        val T2 = T * T
        val dr = PI / 180
        val M = 357.52910 + 35999.05030 * T - 0.0001559 * T2 - 0.00000048 * T * T2
        val L0 = 280.46645 + 36000.76983 * T + 0.0003032 * T2
        var DL = (1.914600 - 0.004817 * T - 0.000014 * T2) * sin(dr * M)
        DL += (0.019993 - 0.000101 * T) * sin(dr * 2 * M) + 0.000290 * sin(dr * 3 * M)
        var L = L0 + DL
        L *= dr
        L -= PI * 2 * floor(L / (PI * 2))
        return INT(L / PI * 6)
    }

    private fun getLunarMonth11(yy: Int, timeZone: Double): Int {
        val off = jdFromDate(31, 12, yy) - 2415021
        val k = INT(off / 29.530588853)
        var nm = getNewMoonDay(k.toDouble(), timeZone)
        val sunLong = getSunLongitude(nm, timeZone)
        if (sunLong >= 9) nm = getNewMoonDay((k - 1).toDouble(), timeZone)
        return nm
    }

    private fun getLeapMonthOffset(a11: Int, timeZone: Double): Int {
        val k = INT(0.5 + (a11 - 2415021.076998695) / 29.530588853)
        var last = 0
        var i = 1
        var arc = getSunLongitude(getNewMoonDay((k + i).toDouble(), timeZone), timeZone)
        do {
            last = arc
            i++
            arc = getSunLongitude(getNewMoonDay((k + i).toDouble(), timeZone), timeZone)
        } while (arc != last && i < 14)
        return i - 1
    }
}
