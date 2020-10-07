package com.lcaruso27.schedulepsi.model
import com.lcaruso27.schedulepsi.controller.ExportViewController
import tornadofx.*

/**
 * String of the class name. Useful for debugging only.
 */
private val TAG = InterviewerChoiceTable::class.simpleName


/**
 * Class that describes one row of the table from the InterviewersView.
 *
 * @param check Boolean that indicates whether the member is an interviewer or not.
 * @param name String property corresponding to the member name.
 */
class InterviewerChoiceTable (check: Boolean, val name: String){

    /**
     * SimpleBooleanProperty binded to the check value. Used to access and update check value.
     */
    private var check by property(check)

    /**
     * Method used to access check property and to update it.
     */
    fun checkProperty() = getProperty(InterviewerChoiceTable::check)
}