package com.lcaruso27.schedulepsi.controller

import com.lcaruso27.schedulepsi.model.HistoricalTable
import com.lcaruso27.schedulepsi.view.doodle
import tornadofx.*
import java.io.File
import java.text.SimpleDateFormat
import javax.json.JsonObject
import kotlin.collections.ArrayList

/**
 * String of the class name. Useful for debugging only.
 */
private val TAG = HistoricalController::class.simpleName


/**
 * Class that retrieves all former planning files and stores them into an ArrayList.
 */
class HistoricalController : Controller() {

    /**
     * ArrayList of HistoricalTable describing all former plannings.
     */
    val historic = ArrayList<HistoricalTable>()

    init {
        val directory = File("${System.getProperty("user.dir")}/${doodle.dirName}")
        val filter = "json"
        val dateFormat = SimpleDateFormat("dd/MM/yyyy")

        var ef : String
        var lastModified : String
        var comment : String
        var jsonObj : JsonObject

        directory.walk().forEach {
            //Get file extension
            ef = it.extension

            //Check if it is a JSON file
            if(ef==filter){
                //Get last modification date
                lastModified = dateFormat.format(it.lastModified())

                //Get comment
                jsonObj = loadJsonObject(it.inputStream())
                comment = jsonObj.string("Comment").orEmpty()

                //Add to historic tableview
                historic.add(HistoricalTable(false, it.name, it.canonicalPath, lastModified, comment))
            }
        }
    }
}