package com.lcaruso27.schedulepsi.view

import com.lcaruso27.schedulepsi.controller.HistoricalController
import com.lcaruso27.schedulepsi.controller.InterviewersViewController
import com.lcaruso27.schedulepsi.model.DoodleExcelModel
import com.lcaruso27.schedulepsi.model.HistoricalTable
import javafx.geometry.HPos
import javafx.geometry.Pos
import javafx.scene.control.*
import javafx.scene.text.Font
import javafx.scene.text.FontWeight
import javafx.stage.FileChooser
import javafx.stage.Stage
import tornadofx.*
import java.io.File

//v1.1.0
//TODO : Use DoodleExcelModel as global variable only for View classes.
//TODO : Add delete button for historic files


//Global variables

/**
 * String of the class name. Useful for debugging only.
 */
private val TAG = MainView::class.simpleName

/**
 * Global variable string with the path to json file corresponding to the current planning.
 */
var jsonFileName = ""

/**
 * Global variable [DoodleExcelModel] that handles the current planning. Makes easier to access through all files.
 * Just a work around for the first version.
 */
var doodle = DoodleExcelModel()

/**
 * Class that creates the first view of the application which aims to choose which planning to schedule :
 * either a new planning from an Excel file or a previous planning from an historical file (JSON file).
 *
 * It inherits from the View class.
 *
 * @see InterviewersView
 */

//"Home" is the title that appears on the top of the view
class MainView : View("Home") {
    /**
     * Controller that looks for previous plannings scheduled embedded in JSON files.
     * @see HistoricalController
     */
    private val controller: HistoricalController by inject()

    /**
     * Controller that updates member names for selecting interviewers (next view).
     *
     * @see InterviewersViewController
     */
    private val interviewersController : InterviewersViewController by inject()

    /**
     * Tableview from [HistoricalTable] with all previous plannings available and their data.
     * @see HistoricalController
     * @see HistoricalTable
     */
    private val historic = controller.historic.observable()

    /**
     * List of extension filters. It aims to select only Excel file type when looking for a file.
     */
    private val ef = arrayOf(FileChooser.ExtensionFilter("Tableur (*.xls, *.xlsx, *.xlsm)", "*.xls", "*.xlsx", "*.xlsm"))

    /**
     * Textfield that contains the path of the file selected when choosing to import an Excel file.
     */
    private lateinit var txtField: TextField

    /**
     * Int used to check whether a file is selected or not. At each file selection, the variable is incremented,
     * and at each cancellation, it is decremented.
     */
    private var flagFileSelected = 0

    /**
     * Int to check whether an Excel (new planning) or a JSON file (previous planning) is chosen :
     * the value is 1 for Excel file, 2 for JSON file.
     */
    private var excelOrJsonFile = 0

    /**
     * Radio button corresponding to the presence of members availabilities in the Excel file data imported.
     */
    private lateinit var radioAvail: RadioButton

    /**
     * Radio button corresponding to the presence of members non availabilities in the Excel file data imported.
     */
    private lateinit var radioUnavail: RadioButton

    /**
     * ToggleGroup of buttons that ensures to be able to select only one radio button among [radioAvail] and [radioUnavail].
     */
    private val toggleGroup = ToggleGroup()

    /**
     * String of the current filename corresponding to the planning : Excel filename or JSON filename.
     */
    private var dataFileName = ""

    /**
     * Int that acts like a boolean to check which data is embedded in the Excel file :
     * members availabilities or members non availabilities.
     */
    private var availOpts = 0 //0 : Availabilities checked / 1 : Unavailability

    //Error messages
    /**
     * String of the error message header : when it is impossible to go further in the planning process.
     */
    private val headError = "Impossible d'ouvrir le planning"

    /**
     * String of the description error message when several files are selected.
     */
    private val msgSeveralFiles = "Plusieurs fichiers ont été selectionnés. Veuillez n'en choisir qu'un seul."

    /**
     * String of the description error message when no file is selected.
     */
    private val msgNoneFile = """   Aucun fichier n'a été sélectionné. Veuillez importer un Doodle ou
                                    sélectionner un ancien planning."""

    /**
     * GridPane with all view elements. GridPane constraints handles where elements are in the view (which row, which column).
     */
    override val root = gridpane {
        padding = insets(20.0)
        vgap = 10.0
        hgap = 40.0
        alignment = Pos.CENTER
        prefHeight = 680.0

        imageview("logo_PSI_120pix.png") {
            gridpaneConstraints {
                columnRowIndex(0, 0)
                columnSpan = 8
                hAlignment = HPos.CENTER
            }
        }

        text("Bienvenue sur SchedulePSI !") {
            font = Font(18.0)
            gridpaneConstraints {
                columnRowIndex(0, 1)
                columnSpan = 8
                hAlignment = HPos.CENTER
            }
        }

        text("Gérer un nouveau planning") {
            style { fontWeight = FontWeight.BOLD }
            gridpaneConstraints {
                columnRowIndex(0, 4)
            }
        }


        button("Importer Doodle") {
            action {
                val file: List<File> = chooseFile("Fichier Doodle", ef, FileChooserMode.Single)
                if (file.isNotEmpty()) {
                    txtField.text = file.first().canonicalPath
                    dataFileName = file.first().absolutePath
                }

                if (txtField.text.isNotBlank()) { //Possible to cancel import
                    flagFileSelected++
                    excelOrJsonFile = 1
                }
            }

            gridpaneConstraints {
                columnRowIndex(0, 5)
            }
        }

        button("Annuler Doodle") {
            action {
                txtField.text = ""
                if (flagFileSelected > 0) flagFileSelected = 0
            }

            gridpaneConstraints {
                columnRowIndex(0, 6)
            }
        }


        txtField = textfield("") {
            gridpaneConstraints {
                columnRowIndex(1, 5)
                columnSpan = 7
            }
        }

        text("Infos renseignés dans le Doodle").gridpaneConstraints {
            columnRowIndex(0, 8)
        }

        radioAvail = radiobutton("Disponibilités", toggleGroup) {
            isSelected = true
            gridpaneConstraints { columnRowIndex(0, 9) }
        }

        radioUnavail = radiobutton("Indisponiblités", toggleGroup).gridpaneConstraints {
            columnRowIndex(0, 10)
        }

        text("Modifier un ancien planning") {
            style { fontWeight = FontWeight.BOLD }
            gridpaneConstraints {
                columnRowIndex(0, 13)
            }
        }

        tableview(historic) {
            isEditable = true
            column("Choix", HistoricalTable::checkProperty).makeEditable()
            readonlyColumn("Nom", HistoricalTable::name)
            readonlyColumn("Date", HistoricalTable::date)
            readonlyColumn("Commentaire", HistoricalTable::comments)
            columnResizePolicy = TableView.CONSTRAINED_RESIZE_POLICY

            gridpaneConstraints {
                columnRowIndex(0, 14)
                columnSpan = 8
            }

            onEditCommit {
                if (it.checkProperty().value) {
                    flagFileSelected++
                    excelOrJsonFile = 2
                    dataFileName = it.path
                } else {
                    flagFileSelected--
                }
            }

            smartResize()
        }

        var tmpMsg: String

        button("Suivant") {
            action {
                // Be sure doodle model is empty
                doodle.clear()

                //Check radio buttons for availOpts
                availOpts = if (radioAvail.isSelected) 0 else 1

                if (flagFileSelected == 1) {
                    if (dataFileName.isNotBlank()) {

                        //New thread not to block the UI with progress
                        // to display a progression until the task is done
                        val thread = runAsyncWithProgress {
                            when (excelOrJsonFile) {
                                1 -> {
                                    doodle.writeDataToJson(dataFileName, availOpts)
                                }
                                2 -> doodle.readDataFromJsonFile(dataFileName)
                            }
                        }

                        thread.setOnSucceeded {
                            interviewersController.getInterviewers()
                            replaceWith(InterviewersView::class)
                        }

                    } else {
                        tmpMsg = if (flagFileSelected < 1) msgNoneFile else msgSeveralFiles
                        alert(Alert.AlertType.ERROR, headError, tmpMsg)
                    }
                }
            }

            gridpaneConstraints {
                columnRowIndex(7, 16)
                hAlignment = HPos.RIGHT
            }
        }
        autosize()
    }
}