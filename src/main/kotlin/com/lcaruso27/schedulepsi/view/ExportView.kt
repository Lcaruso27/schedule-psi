package com.lcaruso27.schedulepsi.view

import com.lcaruso27.schedulepsi.controller.ExportViewController
import javafx.geometry.Pos
import javafx.scene.control.Alert
import javafx.scene.text.FontWeight
import javafx.stage.FileChooser
import tornadofx.*

//V1.1.0
//TODO : Select the comment (make easier to replace it)

/**
 * String of the class name. Useful for debugging only.
 */
private val TAG = ExportView::class.simpleName

/**
 * Class that creates the final view where the user can add a comment to the planning, export it as an Excel file
 * and close the window.
 * It inherits from the View class.
 */

//"Export" is the title that appears on top of the view
class ExportView : View("Export") {

    /**
     * Controller used to get the current comment in the doodle and update it. It also exports the planning as an Excel file.
     *
     * @see ExportViewController
     */
    private val controller : ExportViewController by inject()

    /**
     * List of extension filters. It aims to select only Excel file type when looking for a file.
     */
    private val ef = arrayOf(FileChooser.ExtensionFilter("Tableur (*.xlsx, *.xls, *.xlsm)","*.xlsx","*.xls", "*.xlsm"))

    /**
     * Long string of the error message when there is a problem while creating the Excel file.
     */
    private val msgExportError = """Le planning n'a pas pu être exporté dans le fichier Excel. Veuillez vérifier que le fichier cible n'est pas ouvert ou endommagé. Si le problème persiste, renommer le nom du fichier Excel cible."""

    /**
     * GridPane with all view elements. GridPane constraints handles where elements are in the view (which row, which column).
     */
    override val root = gridpane {
        padding = insets(20.0)
        vgap = 10.0
        hgap = 20.0
        alignment = Pos.CENTER
        autosize()

        text("Ajouter un commentaire"){
            style { fontWeight = FontWeight.BOLD }
            gridpaneConstraints {
                columnRowIndex(0,0)
            }
        }


        val mText = textarea(controller.comment) {
            gridpaneConstraints {
                columnRowIndex(0, 1)
            }
        }

        button("Valider commentaire"){
            action{
                controller.updateComment(mText.text)
            }
            gridpaneConstraints {
                columnRowIndex(0,2)
            }
        }

        text("Exporter le planning vers un fichier Excel"){
            style { fontWeight = FontWeight.BOLD }
            gridpaneConstraints {
                columnRowIndex(0,4)
            }
        }

        button("Exporter"){
            action{
                val file = chooseFile("Fichier Excel",ef, FileChooserMode.Save)
                val filepath = file.first().absolutePath
                var boolRead = false
                val thread = runAsyncWithProgress{
                    //Export to Excel file the planning data
                    boolRead = doodle.writeToExcel(filepath)
                }

                thread.setOnSucceeded {
                    if(!boolRead) {
                        alert(Alert.AlertType.ERROR, "Erreur lors de l'export", msgExportError)
                    }
                }
            }

            gridpaneConstraints {
                columnRowIndex(0,5)
                columnSpan = 5
            }
        }

        imageview("logo_PSI_120pix.png").gridpaneConstraints {
            columnRowIndex(0,7)
        }

        button("Précédent"){
            action {
                replaceWith(SummaryScheduleView::class)
            }

            gridpaneConstraints {
                columnRowIndex(1,7)
            }
        }

        button("Terminer"){
            action{
                //Update JSON file
                controller.exportDoodle(doodle)
                close()
            }
            gridpaneConstraints {
                columnRowIndex(2,7)
            }
        }
    }
}