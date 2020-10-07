package com.lcaruso27.schedulepsi.controller

import com.lcaruso27.schedulepsi.model.DoodleExcelModel
import com.lcaruso27.schedulepsi.view.MainView
import com.lcaruso27.schedulepsi.view.doodle
import com.lcaruso27.schedulepsi.view.jsonFileName
import tornadofx.*

/**
 * String of the class name. Useful for debugging only.
 */
private val TAG = ExportViewController::class.simpleName

/**
 * Class that handles the export of the scheduled planning into an Excel file.
 */
class ExportViewController : Controller() {

    /**
     * String of the current planning comment.
     */
    var comment = doodle.comment

    /**
     * Method that updates the comment describing the current planning.
     *
     * @param cm String that describes the new comment of the current planning.
     */
    fun updateComment(cm : String){
        doodle.comment = cm
        comment = cm
    }

    /**
     * Method that exports the scheduled planning to an Excel file.
     *
     * @param doodle DoodleExcelModel of the current planning.
     */
    fun exportDoodle(doodle : DoodleExcelModel){
        doodle.writeToFile(jsonFileName)
    }

}