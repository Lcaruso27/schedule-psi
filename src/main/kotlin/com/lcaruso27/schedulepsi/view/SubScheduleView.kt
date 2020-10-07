package com.lcaruso27.schedulepsi.view

import com.lcaruso27.schedulepsi.controller.ExportViewController
import com.lcaruso27.schedulepsi.controller.ScheduleController
import com.lcaruso27.schedulepsi.model.DayScheduleModel
import com.lcaruso27.schedulepsi.model.ScheduleModel
import javafx.geometry.HPos
import javafx.geometry.Pos
import javafx.scene.control.ToggleButton
import javafx.scene.control.ToggleGroup
import javafx.scene.control.Tooltip
import javafx.scene.layout.GridPane
import javafx.scene.paint.Color
import javafx.scene.text.FontWeight
import javafx.scene.text.Text
import tornadofx.*

//V1.1.0
//TODO : Display the number of members also available on a meeting time under each box.
//TODO : Block meeting time when it is chosen : gray box + member name scheduled.

/**
 * String of the class name. Useful for debugging only.
 */
private val TAG = SubScheduleView::class.simpleName

/**
 * Class that creates the planning part with all possible meetings arranged in a calendar manner per week.
 * This view is a component of the ScheduleView.
 * It inherits from the View class.
 *
 * @see ScheduleView
 */

class SubScheduleView : View("View")
{
    /**
     * Controller that handles all information about people, e.g. their availabilities, their interviewers, their names, etc.
     *
     * @see ScheduleController
     */
    private val controller : ScheduleController by inject()

    /**
     * Int that represents the first column position for UI elements.
     */
    private val colIndexSchedules = 1

    /**
     * Int that represents the first row position for UI elements.
     */
    private val rowIndexSchedules = 0

    /**
     * Int that represents the column position for the current UI element.
     */
    private var tmpIndexCol = 0

    /**
     * * Int that represents the row position for the current UI element.
     */
    private var tmpIndexRow = 0

    /**
     * ArrayList of People with all people information.
     *
     * @see ScheduleController
     */
    private var listPeople = controller.mPeople

    /**
     * ArrayList of all possible meetings for every members. Each element is an ArrayList with meetings as String for
     * one member.
     *
     * @see ScheduleController
     */
    private var peopleAvailMat = controller.peopleAvailMat

    /**
     * ScheduleModel that represents the current calendar with month, days and meeting times.
     *
     * @see ScheduleController
     */
    private var mySchedule = ScheduleModel()

    /**
     * ArrayList of Int with all weekIndex to display properly each week under the others.
     */
    private var weekIndex = ArrayList<Int>()

    /**
     * ArrayList of DayScheduleModel that represents one week.
     *
     * @see DayScheduleModel
     */
    private var myWeek = ArrayList<DayScheduleModel>()

    /**
     * ToogleGroup that gathers all ToggleButton in order to be able to select only one meeting for each member.
     */
    private var toggleGroup = ToggleGroup()

    /**
     * ArrayList of ToggleButton that represents one meeting date.
     */
    private val toggleList = ArrayList<ToggleButton>()

    /**
     * ArrayList of Tooltip that says which interviewers are available or why this meeting is not possible,
     * i.e. it tells who is not available : the interviewers or the member.
     */
    private val tooltipList = ArrayList<Tooltip>()

    /**
     * Text with the current member name to remind which member is selected.
     */
    private var memberName = Text()

    /**
     * GridPane with all view elements. GridPane constraints handles where elements are in the view (which row, which column).
     */
    override val root = gridpane()

    /**
     * Method that clears all instantiated lists.
     */
    private fun clear(){
        root.clear()
        weekIndex.clear()
        myWeek.clear()
        toggleGroup = ToggleGroup()
        toggleList.clear()
        tooltipList.clear()
    }

    /**
     * Method that builds all components of the planning for the schedule task.
     */
    fun buildPlanningPane(){
        // Clear root and class parameters
        clear()

        // Create components in the root
        with(root){
            padding = insets(20.0)
            vgap = 10.0
            hgap = 40.0
            alignment = Pos.CENTER

            mySchedule = controller.schedule
            weekIndex = mySchedule.weekIndex

            var indexCurrMonth: Int
            var indexNextMonth: Int
            var gapMonth = 0
            var mToggle : ToggleButton
            var mTooltip : Tooltip
            var mmDay : DayScheduleModel
            var day : String
            var mMeeting : ArrayList<String>
            //Current member name
           memberName = text(listPeople[0].nameProperty.value){
                style { fontWeight = FontWeight.BOLD }
                gridpaneConstraints {
                    columnRowIndex(0, 0)
                    columnSpan = 8
                    hAlignment  = HPos.CENTER
                }
            }

            //Begin to build the planning with all UI elements !
            for(k in mySchedule.months.indices) {
                //Trick here : monthTitlesIndex contain indices among all days
                //We get here the week index in mySchedule.mWeek array matching the current month
                //Also remember monthTitlesIndex.size = monthTitles.size+1
                indexCurrMonth = weekIndex[mySchedule.monthsIndex[k]]
                indexNextMonth = weekIndex[mySchedule.monthsIndex[k+1]]
                gapMonth += 6
                //Draw month name
                text(mySchedule.months[k]) {
                    style { fontWeight = FontWeight.BOLD }
                    gridpaneConstraints {
                        columnRowIndex(0, indexCurrMonth + gapMonth)
                        columnSpan = 8
                        hAlignment = HPos.CENTER
                    }
                }

                for (i in indexCurrMonth until indexNextMonth) {
                    myWeek = mySchedule.mWeek[i]
                    tmpIndexRow = rowIndexSchedules + i + 1 + gapMonth
                    for (j in 0 until myWeek.size) {
                        mmDay = myWeek[j]
                        day = mmDay.dDay
                        mMeeting = mmDay.dMeeting
                        if(mMeeting.isEmpty()) mMeeting.add("XXXXXXXX")
                        //The text within the ToggleButton determines its size : quite large thanks to the text here.
                        tmpIndexCol = colIndexSchedules + j
                        //Draw day : day name + ToggleButton to select it !
                        if (mmDay.index != -1) {
                            vbox {
                                alignment = Pos.CENTER
                                label(day).gridpaneConstraints {
                                    columnRowIndex(tmpIndexCol, tmpIndexRow)
                                    columnSpan = mMeeting.size
                                }
                                for (l in mMeeting.indices) {
                                    mToggle = togglebutton("---${mMeeting[l]}---", toggleGroup) {
                                        style = "-fx-base: gray;"
                                        textFill = Color.GRAY
                                        mTooltip = tooltip()
                                        tooltipList.add(mTooltip)
                                    }
                                    toggleList.add(mToggle)
                                    gridpaneConstraints {
                                        columnRowIndex(tmpIndexCol, tmpIndexRow)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Method to colourise the ToggleButton that represents days. The button is green when the meeting is possible
     * and red when it is not. The view must be created before calling this function.
     * If there are hours for each meeting, they are written in white font within the button. Else, a serie of "X" letters
     * are written in the font color of the button to make them as invisible as possible.
     * The button also tells who are the interviewers available or why the meeting is not possible
     *
     * @param index : Int that represent the position of the current member among the list of members
     * within the DoodleExcelModel.
     *
     * @param flagMeeting : Boolean that indicates whether there are hours in the dates or not. The display is different
     * in both cases.
     */

    //Need init before using this function
    fun updateAvail(index : Int, flagMeeting : Boolean){
        var tmpInterviewers : String
        var mSize : Int

        if(peopleAvailMat.isEmpty()) controller.initMeetings()

        memberName.text = listPeople[index].nameProperty.value

        for(i in 0 until toggleList.size){
            tmpInterviewers = peopleAvailMat[index][i]
            mSize = tmpInterviewers.length

            if(mSize > 1){
                toggleList[i].style = "-fx-base: green;"
                toggleList[i].textFill = Color.GREEN
                tooltipList[i].text = tmpInterviewers
            } else {
                toggleList[i].style = "-fx-base: red;"
                toggleList[i].textFill = Color.RED
                if(mSize == 0){
                    tooltipList[i].text = "Interviewer Indisp."
                } else {
                    tooltipList[i].text = "Membre Indisp."
                }
            }

            if(flagMeeting) toggleList[i].textFill = Color.WHITE
        }
    }

    /**
     * Method that sends all meeting information to the controller in order to update the DoodleExcelModel,
     * e.g. the meeting date and the interviewer names.
     *
     * @param index : Int that represent the position of the current member among the list of members
     * within the DoodleExcelModel.
     */
    fun validateMeeting(index : Int){
        val selected = toggleGroup.selectedToggle
        val dateIndex = toggleGroup.toggles.indexOf(selected)
        val interviewerName = peopleAvailMat[index][dateIndex]

        controller.updateTable(index, dateIndex, interviewerName)
    }
}