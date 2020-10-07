package com.lcaruso27.schedulepsi.view

import com.lcaruso27.schedulepsi.controller.ExportViewController
import com.lcaruso27.schedulepsi.controller.ScheduleController
import com.lcaruso27.schedulepsi.model.People
import com.lcaruso27.schedulepsi.model.SummaryScheduleTable
import tornadofx.*

/**
 * String of the class name. Useful for debugging only.
 */
private val TAG = ScheduleView::class.simpleName


/**
 * Class that creates the view where the user can define the interview meetings for each members according to both members
 * and interviewers availabilities. When the user has finished, the SummaryScheduleView must be reached to finish the process.
 *
 * The view is separated in two parts : a table with people information on the left
 * and the planning possibilities (display as a calender per week) on the right. This last part is embedded in SubScheduleView
 * to lighten the code.
 *
 * It inherits from the View class.
 *
 * @see SummaryScheduleView
 * @see SubScheduleView
 */

//"Planning" is the title that appears on top of the view
class ScheduleView : View("Planning") {

    /**
     * View of the planning with the possible meeting dates.
     *
     * @see SubScheduleView
     */
    private val subView: SubScheduleView by inject()

    /**
     * Controller that handles all information about people, e.g. their availabilities, their interviewers, their names, etc.
     *
     * @see ScheduleController
     */
    private val controller: ScheduleController by inject()

    /**
     * Int that describes which member is selected. The corresponding possible meetings are then displayed.
     * This variable is updated when the user clicks on a row of the TableView on the left of the window.
     */
    private var memberIndex = 0

    /**
     * BorderPane with all view elements.
     */
    override val root = borderpane()

    //Init method that creates the view : here as a BorderPane with all elements.
    init {
        root.padding = insets(15.0)

        //Table with people names
        root.left = tableview(controller.summarySchedule) {
            readonlyColumn("Nom", SummaryScheduleTable::name)
            column("Créneau", SummaryScheduleTable::dateProperty)
            column("Interviewer", SummaryScheduleTable::nameInterviewerProperty)
            smartResize()
            onDoubleClick {
                memberIndex = controller.mPeople.indexOfFirst {
                    it.nameProperty.value == selectedItem?.name
                }
                //Update colours according to current availabilities (members and interviewers)
                subView.updateAvail(memberIndex, controller.flagMeeting)
            }
        }

        //Schedule view to pick a proper date
        root.center = subView.root

        root.bottom = borderpane {

            right = borderpane {
                left = button("Valider le créneau") {
                    action {
                        subView.validateMeeting(memberIndex)
                    }
                }

                right = button("Voir résumé") {
                    action {
                        replaceWith(SummaryScheduleView::class)
                    }
                }
            }
            left = imageview("logo_PSI_120pix.png")
        }
    }
}