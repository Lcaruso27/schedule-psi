package com.lcaruso27.schedulepsi.model

import com.lcaruso27.schedulepsi.controller.ExportViewController
import java.util.*
import kotlin.collections.ArrayList

//headersDays refers to how the days are encoded in the Doodle ("lun.", "mar.", ... in French). It is sorted.

/**
 * String of the class name. Useful for debugging only.
 */
private val TAG = ScheduleModel::class.simpleName


/**
 * Class that handles data to properly display dates as a calendar with days sorted by week.
 */
class ScheduleModel {

    /**
     * ArrayList that describes all weeks of the planning. Each element is an ArrayList of DayScheduleModel
     * of one week.
     */
    var mWeek = ArrayList<ArrayList<DayScheduleModel>>()

    /**
     * ArrayList of String that describes all month names.
     */
    val months = ArrayList<String>()

    /**
     * ArrayList of Int that describes the indices of months.
     */
    val monthsIndex = ArrayList<Int>()

    /**
     * ArrayList of Int that describes the indices of weeks.
     */
    val weekIndex = ArrayList<Int>()

    /**
     * Int of the number of meeting times within one week. Use to merge cells of months in the Excel export step.
     */
    var monthSpacing = 0

    /**
     * Empty class Constructor. Useful to initialise the class without values and update it later.
     */
    constructor(){}

    /**
     * Method that merges all schedules information to create a DayScheduleModel.
     *
     * @param dMonth ArrayList of String that describes all month names.
     * @param dMonthIndex ArrayList of Int that describes the first indices of meetings matching months.
     *                          The i-th element is the first index corresponding to the i-th month.
     *                          The last number is the number of meetings.
     * @param dDay ArrayList of String that describes all days (name + number).
     * @param dDayIndex ArrayList of Int that describes the first indices of meetings matching days.
     *                          The i-th element is the first index corresponding to the i-th day.
     *                          The last number is the number of meetings.
     * @param dMeeting ArrayList of String that describes all meeting times (13:00 - 14:00).
     *
     * @return ArrayList of DayScheduleModel that describes all meeting times of the Excel file.
     *
     * @see DayScheduleModel
     */
    private fun parseDays(dMonth : ArrayList<String>, dMonthIndex : ArrayList<Int>,
                          dDay :  ArrayList<String>, dDayIndex : ArrayList<Int>,
                          dMeeting : ArrayList<String>) : ArrayList<DayScheduleModel>{

        val dayTab = ArrayList<DayScheduleModel>()
        val tmpMeeting = ArrayList<String>()
        val boolMeeting = dMeeting.isNotEmpty()

        //Init months
        monthsIndex.add(0)
        var tmpIndex = 1
        var monthYear = dMonth[tmpIndex-1].split(" ")
        var nextMonthIndex = dMonthIndex[tmpIndex]

        for(i in dDay.indices){
            for(j in dDayIndex[i] until dDayIndex[i+1]){
                if(boolMeeting){
                    tmpMeeting.add(dMeeting[j])
                }

                //Get the matching month
                if(nextMonthIndex <= j){
                    monthsIndex.add(i)
                    tmpIndex++
                    //Updates
                    nextMonthIndex = dMonthIndex[tmpIndex]
                    monthYear = dMonth[tmpIndex-1].split(" ")
                }
            }

            dayTab.add(DayScheduleModel(dDay[i], monthYear[0], monthYear[1], ArrayList(tmpMeeting), i))
            tmpMeeting.clear()
        }

        monthsIndex.add(dDay.size)
        return dayTab
    }

    //Add missing days and sort the current week
    /**
     * Method that sorts days within a week and add missing days.
     *
     * @param week ArrayList of DayScheduleModel that describes one week within the Excel file.
     * @param headers ArrayList of String that describes day names within the week.
     *
     * @return
     */
    private fun sortWeek(week : ArrayList<DayScheduleModel>, headers : ArrayList<String>)
            : ArrayList<DayScheduleModel>{
        val numWeekDay = 7 //number of days within a week
        val sortedWeek = ArrayList<DayScheduleModel>()
        val mask = ArrayList<Int>()
        var tmpDay : DayScheduleModel
        var mHeader : String
        var indexDay : Int

        val nullDay = DayScheduleModel("","","", ArrayList(), -1)

        for (i in 0 until numWeekDay){
            sortedWeek.add(nullDay)
            mask.add(0)
        }

        for(i in 0 until week.size) { //put days in the correct order
            tmpDay = week[i]
            mHeader = tmpDay.dDay.split(" ")[0]
            indexDay = headers.indexOf(mHeader)
            sortedWeek[indexDay] = tmpDay
            mask[indexDay] = 1
        }

        return sortedWeek
    }

    /**
     * Methods that retrieves the maximum number of scheduled days within a week. Useful for Excel layout,
     * it enables to correct number of cells in the exported Excel file.
     *
     * @param months ArrayList of String that describes all month names.
     * @param monthsIndex ArrayList of Int that describes the indices of months.
     * @param weekIndex  ArrayList of Int that describes the indices of weeks.
     * @param weeks ArrayList that describes all weeks of the planning. Each element is an ArrayList of DayScheduleModel
     * of one week.
     *
     * @return Maximum number of cells to merge for month's Excel layout.
     */
    private fun getSpacing(months : ArrayList<String>, monthsIndex : ArrayList<Int>,
                           weekIndex : ArrayList<Int>, weeks : ArrayList<ArrayList<DayScheduleModel>>): Int{
        var tmpSpacing : Int
        var maxSpacing = 0
        var indexCurrMonth : Int
        var indexNextMonth : Int
        var meetingSize : Int

        for(i in months.indices){
            indexCurrMonth = weekIndex[monthsIndex[i]]
            indexNextMonth = weekIndex[monthsIndex[i+1]]

            for(j in indexCurrMonth until indexNextMonth){
                tmpSpacing = 0
                for(k in weeks[j]){
                    if(k.index != -1){
                        meetingSize = k.dMeeting.size

                        if(meetingSize>0) {
                            k.dMeeting.forEach { tmpSpacing++ }
                        } else{
                            tmpSpacing++
                        }
                    }

                }

                if(tmpSpacing > maxSpacing) maxSpacing = tmpSpacing
            }
        }

        //maxSpacing : week with maximum meetings
        return (3*maxSpacing - 2)
    }

    /**
     * Class Constructor from calendar elements.
     *
     * @param monthTitles ArrayList of String that describes all month names.
     * @param monthTitlesIndex ArrayList of Int that describes the first indices of meetings matching months.
     *                          The i-th element is the first index corresponding to the i-th month.
     *                          The last number is the number of meetings.
     * @param days ArrayList of String that describes all days (name + number).
     * @param daysIndex ArrayList of Int that describes the first indices of meetings matching days.
     *                          The i-th element is the first index corresponding to the i-th day.
     *                          The last number is the number of meetings.
     * @param meetings ArrayList of String that describes all meeting times (13:00 - 14:00).
     * @param headers ArrayList of String that describes the day names of a whole week: from monday to sunday in French.
     */
    constructor(monthTitles : ArrayList<String>, monthTitlesIndex : ArrayList<Int>,
                days : ArrayList<String>, daysIndex : ArrayList<Int>,
                meetings : ArrayList<String>, headers : ArrayList<String>){

        monthTitles.forEach { months.add(it) }

        val allDays = parseDays(monthTitles, monthTitlesIndex, days, daysIndex, meetings)
        val mCal = GregorianCalendar()

        var currWeekNum = -1
        var currMonth = -1
        var tmpWeekNum : Int
        var tmpMonth : Int
        var boolNewWeek : Boolean
        var mDay : DayScheduleModel

        val tmpWeek = ArrayList<DayScheduleModel>()
        var tmpIndex = 0
        var sortedWeek : ArrayList<DayScheduleModel>

        for(x in allDays.indices){
            mDay = allDays[x]
            tmpMonth = mDay.getMonth()
            mCal.set(mDay.getYear(), mDay.getMonth(), mDay.getDate())
            tmpWeekNum = mCal.get(Calendar.WEEK_OF_YEAR)
            boolNewWeek = (tmpMonth != currMonth) || (tmpWeekNum != currWeekNum)

            if(boolNewWeek) { //New week : 7 days or new month
                currWeekNum = tmpWeekNum
                currMonth = tmpMonth

                if(tmpWeek.isNotEmpty()) {
                    sortedWeek = sortWeek(tmpWeek, headers)
                    mWeek.add(sortedWeek)
                    tmpWeek.clear()
                    tmpIndex++
                }
            }

            tmpWeek.add(mDay)
            weekIndex.add(tmpIndex)
        }

        sortedWeek = sortWeek(tmpWeek, headers)
        mWeek.add(sortedWeek)
        weekIndex.add(tmpIndex+1)
        monthSpacing = getSpacing(months, monthsIndex, weekIndex, mWeek)
    }
}