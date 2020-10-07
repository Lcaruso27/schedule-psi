package com.lcaruso27.schedulepsi.model

import com.lcaruso27.schedulepsi.controller.ExportViewController
import tornadofx.*

/**
 * String of the class name. Useful for debugging only.
 */
private val TAG = HistoricalTable::class.simpleName


/**
 * Class that describes a row of the HistoricalTable from the MainView. It displays data about previous
 * planning planned.
 *
 * @param check Boolean which describes whether the row is selected or not
 * @param name String of the file name
 * @param path String of the file path
 * @param date String of the date where the file has been saved (JJ/MM/AA format)
 * @param comments String of the comment corresponding to the planning.
 */
class HistoricalTable(check: Boolean, val name: String, val path: String, val date: String, val comments : String){

    /**
     * SimpleBooleanProperty binded to the check value. Used to access and update check value.
     */
    private var check by property(check)

    /**
     * Method used to access check property and to update it.
     */
    fun checkProperty() = getProperty(HistoricalTable::check)

}
