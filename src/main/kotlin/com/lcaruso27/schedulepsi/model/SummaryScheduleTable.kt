package com.lcaruso27.schedulepsi.model
import com.lcaruso27.schedulepsi.controller.ExportViewController
import tornadofx.*

/**
 * String of the class name. Useful for debugging only.
 */
private val TAG = SummaryScheduleTable::class.simpleName

/**
 * Class that describes a row of the table from the SummaryScheduleView
 * where a summary of all scheduled dates are displayed.
 *
 * @param check Boolean that indicates whether this member has a interview scheduled or not.
 * @param name String of the member name.
 * @param date String of the meeting date scheduled.
 * @param indexDate Int that describes the index of the date schedules among all schedules (-1 if it is not scheduled).
 * @param numAvail Int that describes the number of availabilities for this member.
 * @param nameInterviewer String of the interviewer name involved.
 */
class SummaryScheduleTable (check: Boolean, val name: String, date: String, indexDate : Int, val numAvail: Int,
                            nameInterviewer : String){

    /**
     * SimpleBooleanProperty binded to the check value. Used to access and update check value.
     */
    private var check by property(check)

    /**
     * Method used to access check property and to update it.
     */
    fun checkProperty() = getProperty(SummaryScheduleTable::check)

    /**
     * SimpleStringProperty binded to the date value. Used to access and update date value.
     */
    private var date by property(date)

    /**
     * Method used to access date property and to update it.
     */
    fun dateProperty() = getProperty(SummaryScheduleTable::date)

    /**
     * SimpleIntegerProperty binded to the indexDate value. Used to access and update indexDate value.
     */
    private var indexDate by property(indexDate)

    /**
     * Method used to access indexDate property and to update it.
     */
    fun indexDateProperty() = getProperty(SummaryScheduleTable::indexDate)

    /**
     * SimpleStringProperty binded to the nameInterviewer value. Used to access and update nameInterviewer value.
     */
    private var nameInterviewer by property(nameInterviewer)

    /**
     * Method used to access nameInterviewer property and to update it.
     */
    fun nameInterviewerProperty() = getProperty(SummaryScheduleTable::nameInterviewer)

}