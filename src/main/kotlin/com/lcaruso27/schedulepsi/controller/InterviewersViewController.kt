package com.lcaruso27.schedulepsi.controller
import com.lcaruso27.schedulepsi.model.InterviewerChoiceTable
import com.lcaruso27.schedulepsi.model.People
import com.lcaruso27.schedulepsi.view.doodle
import tornadofx.*

/**
 * String of the class name. Useful for debugging only.
 */
private val TAG = InterviewersViewController::class.simpleName

/**
 * Class that manages the choice of interviewers.
 */

class InterviewersViewController : Controller(){

    /**
     * ArrayList of People which populates the TableView from InterviewersView.
     */
    val interviewers = ArrayList<People>().observable()

    /**
     * Method that retrieves all members within the DoodleExcelModel as an observable list.
     */
    fun getInterviewers(){
        interviewers.clear()
        interviewers.addAll(doodle.peopleList)
    }

    /**
     * Method that updates the interviewer status within the DoodleExcelModel.
     *
     * @see InterviewerChoiceTable
     */
    fun updateInterviewers(){
        for(i in 0 until interviewers.size)
        {
            doodle.peopleList[i].interviewerProperty.value = interviewers[i].interviewerProperty.value
        }
    }
}