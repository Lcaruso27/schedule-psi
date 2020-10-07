package com.lcaruso27.schedulepsi.view

import com.lcaruso27.schedulepsi.controller.ExportViewController
import com.lcaruso27.schedulepsi.controller.ScheduleController
import com.lcaruso27.schedulepsi.controller.SummaryScheduleViewController
import com.lcaruso27.schedulepsi.model.SummaryScheduleTable
import javafx.geometry.Pos
import tornadofx.*

//V1.1.0
//TODO : Color the rows concerned when there are meeting conflicts. See ToggleClass.

/**
 * String of the class name. Useful for debugging only.
 */
private val TAG = SummaryScheduleView::class.simpleName


/**
 * Class that creates the view which summarises the planning, for each member it shows the meeting date and the interviewer name.
 * From this view, the user can edit the planning by going to ScheduleView.
 * When the user has finished to schedule interviews, it is possible to export it as an Excel file in the next view.
 * It inherits from the View class.
 *
 * @see ScheduleView
 * @see ExportView
 */

//"Résumé" is the title that appears on top of the view
class SummaryScheduleView : View("Résumé") {

    /**
     * Controller that retrieves all information about meetings in order to display it,
     * updates data and checks if there are date conflicts.
     *
     * @see SummaryScheduleViewController
     */
    private val controller : SummaryScheduleViewController by inject()

    private val subView : SubScheduleView by inject()

    /**
     * Controller with information of possible meeting for the interviews. Useful here in order to handle the back button,
     * i.e it enables the user to modify the interviewers by updating them and the possible meetings.
     *
     * @see ScheduleController
     */
    private val scheduleController : ScheduleController by inject()

    /**
     * GridPane with all view elements. GridPane constraints handles where elements are in the view (which row, which column).
     */
    override val root = gridpane {
        padding = insets(20.0)
        vgap = 10.0
        hgap = 20.0
        alignment = Pos.CENTER


        text("Choisir les dates d'entretiens").gridpaneConstraints {
            columnRowIndex(0,0)
        }

        tableview(controller.scheduleSummary) {
            isEditable = true
            //The check property is an visual indicator for the user to remind if the meeting is planned
            //for the corresponding member.
            column("Fait", SummaryScheduleTable::checkProperty).makeEditable()
            readonlyColumn("Nom", SummaryScheduleTable::name)
            column("Créneau", SummaryScheduleTable::dateProperty)
            readonlyColumn("Disponibilités", SummaryScheduleTable::numAvail)
            column("Interviewer", SummaryScheduleTable::nameInterviewerProperty).makeEditable()

            sortOrder.add(columns[3]) //Presort table with numAvail value

            gridpaneConstraints {
                columnRowIndex(0,1)
                columnSpan = 7
            }

            smartResize()
        }

        button("Voir planning") {
            useMaxWidth = true
            action {
                scheduleController.initMeetings()
                subView.buildPlanningPane()
                replaceWith(ScheduleView::class)
            }
            gridpaneConstraints {
                columnRowIndex(4, 2)
                columnSpan = 2
            }
        }

        button("Vérifier planning"){
            useMaxWidth = true
            action {
                //Check if several interviews are scheduled at the same time.
                //Warning message if there are !
               controller.checkConflict()
            }
            gridpaneConstraints {
                columnRowIndex(2, 2)
                columnSpan = 2
            }
        }

        imageview("logo_PSI_120pix.png").gridpaneConstraints {
            columnRowIndex(0,3)
        }

        button("Précédent") {
            action{
                replaceWith(InterviewersView::class)
            }
            gridpaneConstraints {
                columnRowIndex(2, 3)
                columnSpan = 2
            }
        }

        button("Suivant"){
            useMaxWidth =true

            action{
                //Planning are schedules and the DoodleExcelModel is updated with the current data.
                controller.updateDoodle()
                replaceWith(ExportView::class)
            }

            gridpaneConstraints {
                columnRowIndex(4,3)
                columnSpan = 2
            }
        }

        autosize()
    }
}