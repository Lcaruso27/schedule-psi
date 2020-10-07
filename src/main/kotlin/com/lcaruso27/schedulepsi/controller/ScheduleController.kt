package com.lcaruso27.schedulepsi.controller

import com.lcaruso27.schedulepsi.model.People
import com.lcaruso27.schedulepsi.model.ScheduleModel
import com.lcaruso27.schedulepsi.view.doodle
import tornadofx.*
/**
 * String of the class name. Useful for debugging only.
 */
private val TAG = ScheduleController::class.simpleName

//V1.1.0
//TODO : Replace parameter as return element when possible (#getConvenientMeetings)

/**
 * Class that manages the [SubScheduleView]. It assigns meetings and interviewers.
 */

class ScheduleController : Controller() {

    /**
     * [SummaryScheduleViewController] used to exchange information between [SummaryScheduleView]
     * and [SubScheduleView].
     */
    private val summaryController : SummaryScheduleViewController by inject()

    /**
     * ArrayList of [People] with member information from the [DoodleExcelModel].
     */
    var mPeople = ArrayList<People>()

    /**
     * ArrayList of String which stores the possible meeting times
     */
    private var mSchedules = ArrayList<String>()

    /**
     * ArrayList of Integer which stores the member index of the interviewers.
     */
    private val interviewersIndex = ArrayList<Int>()

    /**
     * ArrayList of [SummaryScheduleTable] which stores information shown in SummaryScheduleView.
     */
    val summarySchedule = summaryController.scheduleSummary

    /**
     * ArrayList which stores a list of possible meetings for each member.
     */
    val peopleAvailMat = ArrayList<ArrayList<String>>()

    /**
     * [ScheduleModel] which stores meeting dates.
     */
    var schedule = ScheduleModel()

    /**
     * Boolean that indicates whether the doodle presents meeting hours or not.
     */
    var flagMeeting = false

    /**
     * Method that initialises all class parameters from the [DoodleExcelModel].
     */
    private fun initParams(){
        mPeople = doodle.peopleList
        mSchedules = doodle.schedules
        flagMeeting = doodle.flagMeeting
        schedule = doodle.getSchedule(mSchedules)
    }

    //Init tab necessary for putting interviewers' names with empty string
    /**
     * Method that instantiates an empty ArrayList of String.
     *
     * @return ArrayList of String with empty values. There are as many elements as members.
     */
    private fun initMeetingTime() : ArrayList<String>
    {
        val tab = ArrayList<String>()
        val size = mPeople[0].availTabProperty.value.size
        for(i in 0 until size){
            tab.add("")
        }

        return tab
    }

    /**
     * Method that finds the matching meetings between one member and one interviewer.
     * If the interviewer is not available for a matching meeting, there is an empty string. If the member is not
     * available, there is a "M" string.
     *
     * @param interviewer [People] which represents the interviewer
     * @param member [People] which represents the member to interview.
     * @param interviewerIndex Integer which represents the index of the interviewer among the list of members.
     * @param memberIndex Integer which represents the index of the member among the list of members.
     * @param tab ArrayList of String where convenient meetings are written.
     */
    private fun getConvenientMeetings(interviewer : People, member : People,
                                      interviewerIndex : Int, memberIndex : Int, tab : ArrayList<String>)
    {
        val memberAvailTab = member.availTabProperty.value
        val interviewerAvailTab = interviewer.availTabProperty.value
        var memberAvail : Boolean
        var interviewerAvail : Boolean
        var flagAvail : Boolean

        for(i in 0 until memberAvailTab.size){
            memberAvail = memberAvailTab[i]==1
            interviewerAvail = interviewerAvailTab[i] ==1
            flagAvail = (memberAvail) && (interviewerAvail) && (memberIndex != interviewerIndex)
            if(flagAvail){
                tab[i] = "${tab[i]}${interviewer.nameProperty.value}/" //Concatenate interviewer name with "/" parsing element
            } else if(!memberAvail){
                tab[i] = "M"
            }

            //If interviewer is not available : tab[i] = "" : useful to concatenate available interviewers names
        }
    }

    /**
     * Method that updates the table which summarises the meetings scheduled.
     *
     * @param memberIndex Integer which represents the index of the member among the list of members.
     * @param scheduleIndex Integer which represents the index of the meeting scheduled among the list of meetings.
     * @param interviewerName String which is the concatenated names of the interviewers available.
     */
    fun updateTable(memberIndex: Int, scheduleIndex: Int, interviewerName: String){
        val meetingDate = mSchedules[scheduleIndex]
        val tableIndex = summarySchedule.indexOfFirst {
            it.name == mPeople[memberIndex].nameProperty.value
        }

        summarySchedule[tableIndex].dateProperty().value = meetingDate
        summarySchedule[tableIndex].indexDateProperty().value = scheduleIndex
        summarySchedule[tableIndex].nameInterviewerProperty().value = interviewerName
        summarySchedule[tableIndex].checkProperty().value = true
    }

    /**
     * Method that retrieves the convenient meetings for all members.
     */
    fun initMeetings(){
        interviewersIndex.clear()
        peopleAvailMat.clear()

        //Init parameters
        initParams()

        //Get indices of interviewers within mPeople
        var interviewBool: Boolean
        for (i in 0 until mPeople.size) {
            interviewBool = mPeople[i].interviewerProperty.value
            if (interviewBool) {
                interviewersIndex.add(i)
            }
        }

        //Check availabilities of people
        var tmpMutualMeeting = initMeetingTime()
        var tmpMember : People
        for (i in 0 until mPeople.size)
        {
            tmpMember = mPeople[i]
            interviewersIndex.forEach {
                getConvenientMeetings(mPeople[it], tmpMember, it, i, tmpMutualMeeting)
            }
            peopleAvailMat.add(tmpMutualMeeting)
            tmpMutualMeeting = initMeetingTime()
        }
    }
}