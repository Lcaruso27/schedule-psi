package com.lcaruso27.schedulepsi.controller

import com.lcaruso27.schedulepsi.model.People
import com.lcaruso27.schedulepsi.model.SummaryScheduleTable
import com.lcaruso27.schedulepsi.view.doodle
import javafx.collections.transformation.SortedList
import javafx.scene.control.Alert
import tornadofx.*

//v1.1.0
// TODO : Colourise the corresponding row within the TableView when a meeting is scheduled for a member
//  instead of using a checkbox + use People properties in TableView

/**
 * String of the class name. Useful for debugging only.
 */
private val TAG = SummaryScheduleViewController::class.simpleName

/**
 * Class that manages the summary table of the interviews scheduled.
 */

class SummaryScheduleViewController : Controller() {

    /**
     * Observable ArrayList of [SummaryScheduleTable] which stores all meetings scheduled. The list is sorted
     * according to the number of availabilities of all members.
     */
    val scheduleSummary = ArrayList<SummaryScheduleTable>().asObservable()

    /**
     * ArrayList of String which stores all possible meetings from the doodle.
     */
    private val mSchedules = ArrayList<String>()

    /**
     * ArrayList of String which stores all member names.
     */
    private val mPeopleNames = ArrayList<String>()

    /**
     * Method that retrieves member names and the selected meetings to populate the TableView.
     */
    fun initSchedules(){
        var tmpPeople : People
        var name : String
        var doodleDate : String
        var tmpIndex : Int
        var tmpCheck : Boolean

        scheduleSummary.clear()
        mPeopleNames.clear()
        mSchedules.clear()

        for(x in doodle.schedules){
            mSchedules.add(x)
        }

        for(i in doodle.peopleList.indices){
            tmpPeople = doodle.peopleList[i]
            name = tmpPeople.nameProperty.value

            doodleDate = tmpPeople.dateProperty.value
            tmpIndex = mSchedules.indexOf(doodleDate)

            tmpCheck = doodleDate.split("/").size > 1

            scheduleSummary.add(SummaryScheduleTable(tmpCheck, name, doodleDate,
                    tmpIndex, tmpPeople.numAvailProperty.value, tmpPeople.interviewByProperty.value))

            mPeopleNames.add(name)
        }
    }

    /**
     * Method that updates the DoodleExcelModel with the interviews scheduled.
     */
    fun updateDoodle() {
        var memberIndex : Int
        var dateIndex : Int
        var meetingDate : String
        var interviewerName : String?
        for(i in 0 until scheduleSummary.size){
            memberIndex = mPeopleNames.indexOf(scheduleSummary[i].name)
            dateIndex = scheduleSummary[i].indexDateProperty().value

            if(dateIndex!=-1) {
                meetingDate = mSchedules[dateIndex]
                interviewerName = scheduleSummary[i].nameInterviewerProperty().value

                doodle.peopleList[memberIndex].dateProperty.value = meetingDate
                doodle.peopleList[memberIndex].interviewByProperty.value = interviewerName

            }
        }
    }

    /**
     * Method that finds possible meeting conflicts : one meeting is taken by several members.
     */
    fun checkConflict(){
        //Check if one meeting is taken several times
        val sizeSchedules = mSchedules.size
        val mask = ArrayList<Int>()
        val conflictIndices = ArrayList<Int>()

        for(i in 0 until sizeSchedules){
            mask.add(0)
        }

        var index : Int
        scheduleSummary.forEach {
            index = it.indexDateProperty().value

            if(index!=-1) {
                mask[index] = mask[index] + 1
            }
        }

        for(i in 0 until sizeSchedules){
            if(mask[i] > 1){
                conflictIndices.add(i)
            }
        }

        val flag = (conflictIndices.size > 0)
        if(flag) alert(Alert.AlertType.WARNING, "Un même créneau a été choisi plusieurs fois, vérifiez les dates !")
    }
}