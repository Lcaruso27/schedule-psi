package com.lcaruso27.schedulepsi.model

import com.lcaruso27.schedulepsi.controller.ExportViewController
import java.util.*
import kotlin.collections.ArrayList

/**
 * String of the class name. Useful for debugging only.
 */
private val TAG = DayScheduleModel::class.simpleName


/**
 * Class that describes a day meeting.
 *
 * @param dDay String of the day date (day name and number such as "lun. 16").
 * @param dMonth String of the month name.
 * @param dYear String of the year date.
 * @param dMeeting ArrayList of meeting hours ([13:00 14:00, 14:00 15:00]).
 * @param index Int of the index of this date among all days from the Excel file.
 */
class DayScheduleModel(val dDay : String, val dMonth : String, val dYear : String, val dMeeting : ArrayList<String>,
                       val index : Int) {

    /**
     * Method that converts the year to an Integer.
     *
     * @return Integer that represents the year corresponding to the current day.
     */
    fun getYear() : Int{
        return dYear.toInt()
    }

    /**
     * Method that returns the day number of the corresponding day.
     *
     * @return Integer that returns the day number.
     */
    fun getDate() : Int{
        return dDay.split(" ")[1].toInt()
    }

    /**
     * Method that returns the number of the corresponding month.
     *
     * @return Integer that corresponds to the month index within the year.
     */
    fun getMonth(): Int{
        var iMonth = 0
        when(dMonth.toLowerCase()){
            "janvier" -> iMonth     =   Calendar.JANUARY
            "février" -> iMonth     =   Calendar.FEBRUARY
            "mars" -> iMonth        =   Calendar.MARCH
            "avril" -> iMonth       =   Calendar.APRIL
            "mai" -> iMonth         =   Calendar.MAY
            "juin" -> iMonth        =   Calendar.JUNE
            "juillet" -> iMonth     =   Calendar.JULY
            "août" -> iMonth        =   Calendar.AUGUST
            "septembre" -> iMonth   =   Calendar.SEPTEMBER
            "octobre" -> iMonth     =   Calendar.OCTOBER
            "novembre" -> iMonth    =   Calendar.NOVEMBER
            "décembre" -> iMonth    =   Calendar.DECEMBER
        }

        return iMonth
    }
}