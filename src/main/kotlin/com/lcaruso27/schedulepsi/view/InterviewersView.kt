package com.lcaruso27.schedulepsi.view

import com.lcaruso27.schedulepsi.controller.ExportViewController
import com.lcaruso27.schedulepsi.controller.InterviewersViewController
import com.lcaruso27.schedulepsi.controller.SummaryScheduleViewController
import com.lcaruso27.schedulepsi.model.InterviewerChoiceTable
import com.lcaruso27.schedulepsi.model.People
import javafx.geometry.Pos
import tornadofx.*

/**
 * String of the class name. Useful for debugging only.
 */
private val TAG = InterviewersView::class.simpleName

/**
 * Class that creates the view where the user can select who are the interviewers
 * among the names within the [DoodleExcelModel].
 *
 * @see SummaryScheduleView
 */

//"Interviewers" is the title that appears on top of the view
class InterviewersView : View("Interviewers") {

    /**
     * Controller that looks for all names with the [DoodleExcelModel] and sets to true the interviewer boolean
     * when a user choose a member as interviewer when clicking on "Next" to go to next view.
     *
     * @see InterviewersViewController
     */
    private val controller : InterviewersViewController by inject()

    /**
     * Controller that summarises the meetings scheduled.
     *
     * @see SummaryScheduleViewController
     */
    private val summaryScheduleController : SummaryScheduleViewController by inject()

    /**
     * GridPane with all view elements. GridPane constraints handles where elements are in the view (which row, which column).
     */
    override val root = gridpane {
        padding = insets(20.0)
        vgap = 10.0
        hgap = 20.0
        alignment = Pos.CENTER

        text("Choisir les interviewers").gridpaneConstraints {
            columnRowIndex(0, 0)
        }

        tableview(controller.interviewers){
            isEditable = true
            //The interviewer property (boolean) is editable by using a checkbox.
            column("Choix", People::interviewerProperty).useCheckbox()
            column("Nom", People::nameProperty)
            columnResizePolicy = SmartResize.POLICY

            gridpaneConstraints {
                columnSpan = 4
                columnRowIndex(0, 1)
            }

            smartResize()
        }

        imageview("logo_PSI_120pix.png") {
            gridpaneConstraints {
                columnRowIndex(0, 2)
            }
        }

        button("Précédent") {
            useMaxWidth = true
            action {
                replaceWith(MainView::class)
            }

            gridpaneConstraints {
                columnRowIndex(2, 2)
            }
        }

        button("Suivant") {
            useMaxWidth = true
            action {
                controller.updateInterviewers() //Set interviewer property to true for selected members
                summaryScheduleController.initSchedules()
                replaceWith(SummaryScheduleView::class)
            }

            gridpaneConstraints {
                columnRowIndex(3, 2)
            }
        }
    }
}