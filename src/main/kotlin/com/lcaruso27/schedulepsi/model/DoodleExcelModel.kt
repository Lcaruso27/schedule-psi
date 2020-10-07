package com.lcaruso27.schedulepsi.model

import com.lcaruso27.schedulepsi.view.jsonFileName
import org.apache.poi.hssf.util.HSSFColor
import org.apache.poi.ss.usermodel.*
import org.apache.poi.ss.util.CellRangeAddress
import org.apache.poi.xssf.usermodel.*
import tornadofx.*
import java.awt.Color
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import javax.json.JsonArray
import javax.json.JsonObject

/**
 * String of the class name. Useful for debugging only.
 */
private val TAG = DoodleExcelModel::class.simpleName

/**
 * String of the default comment to write in new planning.
 */
private const val COMMENT = "Ecrire un commentaire"

/**
 * Class to parse Excel file data from Doodle into JSON data. It handles data import and export.
 */
class DoodleExcelModel : JsonModel{

    //Begin of table(always the same)
    /**
     * Int of the first row location of data within the Excel file. Always the same location.
     */
    private val beginRow = 3

    /**
     * Int of the first column location of data within the Excel file. Always the same location.
     */
    private val beginDateCol = 1

    //Data for ScheduleModel
    /**
     * List of Strings of day names within the Excel file. Only the french version is handled.
     */
    private val headerDays = listOf("lun.", "mar.", "mer.", "jeu.", "ven.", "sam.", "dim.")

    /**
     * ArrayList of the day names. The conversion from list to ArrayList is done here.
     */
    private val listHeadersDays = ArrayList(headerDays)

    //Data to build people class
    //People name with their available and non-available dates
    /**
     * ArrayList of String which stores all people names within the Excel file.
     */
    private val people = ArrayList<String>()

    //All time slots from Doodle
    /**
     * ArrayList of String which stores all month names within the Excel file.
     */
    private val months = ArrayList<String>()

    /**
     * ArrayList of Int which stores the first indices of meeting times corresponding to months. The i-th element
     * is the first meeting index corresponding to the i-th month. The last number is the number of meetings.
     */
    private val monthsIndex =  ArrayList<Int>()

    /**
     * ArrayList of String which stores all day dates (name + number) within the Excel file.
     */
    private val days = ArrayList<String>()

    /**
     * ArrayList of Int which stores first indices of meeting times corresponding to days. The i-th element
     * is the first meeting index corresponding to the i-th day. The last number is the number of meetings.
     *
     */
    private val daysIndex =  ArrayList<Int>()

    /**
     * ArrayList of String which stores the meeting time within the Excel file.
     */
    private val meetingTimes =  ArrayList<String>() //Optional, depends on Doodle file

    //Final data for Views
    //Final time slots
    /**
     * ArrayList of String which stores all dates within the Excel file
     * in the following format : day(name + date)/months/meetingTime
     */
    val schedules =  ArrayList<String>() //List of all time slots as dates (lun. 3/décembre 2018/13:00)

    /**
     * ArrayList of People which describes all member data from the Excel file.
     */
    val peopleList = ArrayList<People>()

    /**
     * String that describes the comment associated to the current planning. The comment is set to "Ecrire un commentaire"
     * at first and updated by the user.
     */
    var comment = COMMENT

    /**
     * Boolean that indicates whether the Excel file contains meeting times or not.
     */
    var flagMeeting = false

    //Storing data
    /**
     * String of the directory names where historic JSON files are stored.
     */
    val dirName = "MyPlannings"


    /**
     * Method that parse the Excel file and store data about the members.
     *
     * @param xlWs Sheets that describes the Excel sheet where all data are written.
     */
    private fun getRawDataFromDoodle(xlWs: Sheet){

        val dataFormatter = DataFormatter()

        var firstCell : String
        var mString : String
        var colIdx = 0

        for(x in beginRow..xlWs.lastRowNum) {
            firstCell = dataFormatter.formatCellValue(xlWs.getRow(x).getCell(0))

            if (firstCell != "Nombre") { //End of table by the row "Nombre"

                for (cell in xlWs.getRow(x)) {
                    mString = dataFormatter.formatCellValue(cell)

                    if (firstCell != "") {
                        people.add(mString)
                    } else {
                        if (mString != "") {

                            //Get first column index to 0 in monthsIndex & daysIndex & meetingTimes
                            colIdx = cell.columnIndex - beginDateCol

                            when (x) {

                                3 -> {
                                    months.add(mString)
                                    monthsIndex.add(colIdx)
                                }
                                4 -> {
                                    days.add(mString)
                                    daysIndex.add(colIdx)
                                }
                                5 -> meetingTimes.add(mString)
                            }

                        }
                    }
                }
            }
        }

        //Little trick here : We put the very end index in index arrays
        //Useful for getTimeSlots() : I use the current index and the next one
        //to see if it is the correct month/day to match
        //By artificially put the very end of the table, I can match all months/days
        //For example : I have two months : monthsIndex = [0,6,12]
        //I will match :
        // - the first month with all cells with indices between 0 (inclusive) and 6 (exclusive)
        // - the second month with all cells with indices between 6 and 12

        monthsIndex.add(colIdx+1)
        daysIndex.add(colIdx+1)
    }

    /**
     * Method to get availabilities value for each meeting in the Excel file.
     * The Excel file contains "OK" or empty cells according to the member answers to the Doodle.
     * According the availabilities options : "OK" means either available or not available.
     * This data is stored as 1 if the members are available and 0 if they are not.
     *
     * @param availableOpt Int that indicates whether people indicate whether "OK" cells means they are available
     * or not. This parameter has two values :
     *  * 0 if "OK" means people are available;
     *  * 1 if "OK" means people are not available.
     */
    private fun getAvailableFromPeople(availableOpt : Int) {
        var mString: String

        for (i in 0 until people.size) {
            mString = people[i]

            //Problem here : I use only one accumulator but I increment two
            //I could verify at each iteration but it is not efficient neither
            when (mString) {
                "OK" -> people[i] = (1 - availableOpt).toString()

                "" -> people[i] = availableOpt.toString()
            }
        }

    }

    /**
     * Method that builds all dates present in the Excel file with the following format : day/month/meeting.
     * If there is no meeting times specified in the Excel file, the format is only day/month.
     */
    private fun getTimeSlots(){

        var flagMonths : Boolean
        var flagDays : Boolean

        //I have to check if there are hours proposed in Doodle or just days
        //Hours : I have to match month & day with each hour
        //Days : I have to match month with each day

        if(meetingTimes.size != 0) {
            flagMeeting = true
            for (i in 0 until monthsIndex.size-1) {
                for (j in 0 until daysIndex.size-1) {
                    for (k in meetingTimes.indices) {

                        flagMonths = (j >= monthsIndex[i]) && (j < monthsIndex[i + 1])
                        flagDays = (k >= daysIndex[j]) && (k < daysIndex[j+1])

                        if (flagMonths && flagDays) schedules.add(days[j] + "/" + months[i] + "/" + meetingTimes[k])
                    }
                }
            }
        }

        else {
            for(i in 0 until monthsIndex.size-1){
                for(j in 0 until days.size){
                    flagMonths = (j >= monthsIndex[i]) && (j < monthsIndex[i + 1])

                    if(flagMonths) schedules.add(days[j] + "/" + months[i])
                }
            }
        }
    }

    /**
     * Method that returns the ArrayList of dates where interviews have been scheduled by the user.
     *
     * @param listPeople ArrayList of People which describes all people data stored.
     * @param schedules ArrayList of String which describes all proposed dates within the Excel file.
     *
     * @return ArrayList of String which describes all dates when an interview is scheduled.
     */
    private fun getMeetingsList(listPeople : ArrayList<People>, schedules : ArrayList<String>) : ArrayList<String>{
        val meetingsList = ArrayList<String>()
        val peopleMeetings = ArrayList<String>()
        var peopleIndex : Int

        for(x in listPeople){
            peopleMeetings.add(x.dateProperty.value)
        }

        for(x in schedules){
            peopleIndex = peopleMeetings.indexOf(x)
            if(peopleIndex!=-1) meetingsList.add(x)
        }

        return meetingsList
    }

    //Public methods

//    /**
//     * Useless method.
//     *
//     * @param json JsonObject where People fields are present.
//     */
//    override fun updateModel(json: JsonObject) {
//        peopleList.forEach { it.updateModel(json)}
//    }

    /**
     * Method that creates the JSON file with People fields.
     *
     * @param json Empty JsonBuilder where the structure and the People class content is written.
     */
    override fun toJSON(json: JsonBuilder) {
        with(json){
            add("People", peopleList.toJSON())
            add("Schedules", schedules)
            add("Comment", comment)
        }
    }

    /**
     * Method that writes the DoodleExcelModel data into a JSON file.
     *
     * @param filename String of the name of the JSON file where to write data.
     */
    fun writeToFile(filename : String){
        val json = JsonBuilder()
        toJSON(json)
        File(filename).writeText(json.build().toPrettyString().removeRange(0,1))
        //The JSON file always start with an empty line, so I remove it with removeRange method.
    }

    /**
     * Method that returns a ScheduleModel with data to display as a calendar all dates.
     *
     * @param meetingsList ArrayList of String which describes dates of interest
     *
     * @return ScheduleModel that enables to display properly dates as a calendar with dates
     * sorted by week.
     */
    fun getSchedule(meetingsList : ArrayList<String>) : ScheduleModel{
        val mMonths = ArrayList<String>()
        val mMonthsIndex = ArrayList<Int>()
        val mDays = ArrayList<String>()
        val mDaysIndex = ArrayList<Int>()
        val mMeetings = ArrayList<String>()

        var tmp : List<String>
        var currMonth= ""
        var currDay= ""
        var tmpMonth : String
        var tmpDay : String


        for(i in meetingsList.indices){
            tmp = meetingsList[i].split("/")
            tmpDay = tmp[0]
            tmpMonth = tmp[1]

            if(tmpMonth!=currMonth){
                currMonth = tmpMonth
                mMonths.add(tmpMonth)
                mMonthsIndex.add(i)
            }

            if(tmpDay!=currDay){
                currDay = tmpDay
                mDays.add(tmpDay)
                mDaysIndex.add(i)
            }

            if(flagMeeting) mMeetings.add(tmp[2])
        }

        mMonthsIndex.add(meetingsList.size)
        mDaysIndex.add(meetingsList.size)

        return ScheduleModel(mMonths, mMonthsIndex, mDays, mDaysIndex, mMeetings, listHeadersDays)
    }

    /**
     * Method that writes data to a JSON file considered as an historical file.
     *
     * @param filepath String of the file path of the Excel file
     * @param availableOpt Int that describes if members indicate when they are available or when they are not.
     */
    fun writeDataToJson(filepath: String, availableOpt: Int){
        //Open Excel file
        val inputStream = FileInputStream(filepath)
        val xlWb = WorkbookFactory.create(inputStream)
        val xlWs = xlWb.getSheetAt(0)

        //Retrieve data from Excel and close it
        getRawDataFromDoodle(xlWs)
        inputStream.close()

        //Get data ordered : People / Schedules
        getAvailableFromPeople(availableOpt)
        getTimeSlots()

        //Create all People elements
        val nbCols = schedules.size + 1
        var index : Int

        val tmpAvailTab = ArrayList<Int>()
        var tmpAvail : Int
        var numAvail = 0
        var tmpData : String

        for(i in people.size-1 downTo 0){
            tmpData = people[i]

            index = i%nbCols
            //0 or 1
            if(index!=0){
                tmpAvail = tmpData.toInt()
                numAvail+= tmpAvail
                tmpAvailTab.add(tmpAvail)
            } else {
                //People name : End of line we get all data to create People item
                peopleList.add(People(tmpData, ArrayList(tmpAvailTab), numAvail))
                //Init tmp values
                tmpAvailTab.clear()
                numAvail = 0
            }
        }

        //Write it in a JSON

        //Give a structure
        val mJson = JsonBuilder()
        toJSON(mJson)

        //Split "/"
        var nameJsonFile = StringBuilder(filepath).split("/").last()
        //Split "\"
        nameJsonFile = StringBuilder(nameJsonFile).split("\\").last()
        //Split "."
        nameJsonFile = StringBuilder(nameJsonFile).split(".")[0]

        //Append timestamp and .json
        nameJsonFile+= "_${System.currentTimeMillis()}.json"

        //Everything's ok, let's update static json name
        jsonFileName = "$dirName/$nameJsonFile"

        //Create directory
        val mDir = File(dirName)
        mDir.mkdirs()

        //Write JSON file
        writeToFile(jsonFileName)
    }

    /**
     * Method that retrieve data from a previous planning written in a JSON file.
     *
     * @param path String if the file path of the JSON file where a previous planning is stored.
     */
    fun readDataFromJsonFile(path : String) {
        val inputStream = File(path).inputStream()
        val json = loadJsonObject(inputStream)
        inputStream.close()

        var tmp : JsonObject
        val availTab = ArrayList<Int>()

        //People
        var jsonArray = json.getJsonArray("People")
        var availArray : JsonArray
        jsonArray.forEach{
            availTab.clear()
            tmp = it.asJsonObject()
            availArray = tmp.getJsonArray("AvailTab")

            for(i in 0 until availArray.size){
                availTab.add(availArray.getInt(i))
            }

            peopleList.add(People(tmp.getString("Name"), ArrayList(availTab),
                    tmp.getInt("NumAvail"), tmp.getBoolean("Interviewer"),
                    tmp.getString("Date"), tmp.getString("InterviewBy")))
        }

        //Schedules
        jsonArray = json.getJsonArray("Schedules")
        for(i in 0 until jsonArray.size){
            schedules.add(jsonArray.getString(i))
        }
        comment = json.string("Comment").orEmpty()
        flagMeeting = (schedules[0].split("/").size > 2)
        jsonFileName = path
    }

    /**
     * Method that export the current planning result into an Excel file.
     *
     * @param filepath String of the file path where the Excel file is written
     *
     * @return Boolean that indicates whether the file has been successfully written or not.
     */
    fun writeToExcel(filepath : String): Boolean {
        //Get Schedule of Interviews and Names of members and their interviewers
        val meetingsList = getMeetingsList(peopleList, schedules)
        val duoNames = Array(peopleList.size){""}
        var tmpIndex : Int
        peopleList.forEach {
            tmpIndex = meetingsList.indexOf(it.dateProperty.value)
            if(tmpIndex!=-1){
                duoNames[tmpIndex] = "${it.nameProperty.value};${it.interviewByProperty.value}"
            }
        }
        val mSchedule = getSchedule(meetingsList)

        //Create Excel sheet
        val xlWb = XSSFWorkbook()
        val xlWs = xlWb.createSheet()

        //Write styles and font
        val dayStyle = xlWb.createCellStyle()
        dayStyle.setFillForegroundColor(XSSFColor(Color.ORANGE))
        dayStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND)
        dayStyle.setAlignment(HorizontalAlignment.CENTER)

        val mStyle = xlWb.createCellStyle()
        mStyle.setAlignment(HorizontalAlignment.CENTER)

        val headerFont = xlWb.createFont()
        headerFont.bold = true
        headerFont.fontHeightInPoints = 24

        val normalFont = xlWb.createFont()

        val titleFont = xlWb.createFont()
        titleFont.bold = true

        val memberFont = xlWb.createFont()
        memberFont.color = HSSFColor.HSSFColorPredefined.BLUE.index

        val interviewerFont = xlWb.createFont()
        interviewerFont.color = HSSFColor.HSSFColorPredefined.GREEN.index

        //Write 1st and 2nd line
        val textTab = listOf("Planning Entretiens", "Membre", "Interviewer")
        val fontTab = listOf<XSSFFont>(headerFont, memberFont, interviewerFont, normalFont, titleFont)
        val headColTab = listOf(0,10,11)

        //Temp variables
        var mRow : XSSFRow
        var cell : XSSFCell
        var txt : XSSFRichTextString

        mRow = xlWs.createRow(0)
        for(j in headColTab.indices){
            cell = mRow.createCell(headColTab[j])
            txt = XSSFRichTextString(textTab[j])
            txt.applyFont(fontTab[j])
            cell.setCellValue(txt)
        }

        mRow = xlWs.createRow(1)
        cell = mRow.createCell(0)
        txt = XSSFRichTextString(this.comment)
        txt.applyFont(normalFont)
        cell.setCellValue(txt)

        //Write planning
        val rowOffset = 4
        val colOffset = 0
        var tmpRow = 0
        var tmpCol : Int

        var indexCurrMonth : Int
        var indexNextMonth : Int
        var myWeek : ArrayList<DayScheduleModel>
        var tmpDuo : List<String>
        val indexMeetingToMember = ArrayList<Int>()
        var indexMeetingOffset = 0
        var tmpMeetingSize : Int
        var spacing : Int //Spacing to merge month and day cells


        for(i in mSchedule.months.indices){
            indexCurrMonth = mSchedule.weekIndex[mSchedule.monthsIndex[i]]
            indexNextMonth = mSchedule.weekIndex[mSchedule.monthsIndex[i+1]]

            tmpRow += i + rowOffset
            tmpCol = colOffset
            spacing = mSchedule.monthSpacing

            //Write month
            mRow = xlWs.createRow(tmpRow)
            cell = mRow.createCell(tmpCol)
            txt = XSSFRichTextString(mSchedule.months[i])
            txt.applyFont(titleFont)
            cell.setCellValue(txt)
            cell.cellStyle = mStyle
            xlWs.addMergedRegion(CellRangeAddress(tmpRow, tmpRow, tmpCol, tmpCol + spacing))


            for(j in indexCurrMonth until indexNextMonth){
                myWeek = mSchedule.mWeek[j]
                tmpRow+=2
                tmpCol = colOffset

                //Write days
                mRow = xlWs.createRow(tmpRow)
                for(k in myWeek){
                    if(k.index != -1){
                        tmpMeetingSize = k.dMeeting.size
                        spacing = if(tmpMeetingSize > 0) 3*tmpMeetingSize - 2 else 1
                        cell = mRow.createCell(tmpCol)
                        txt = XSSFRichTextString(k.dDay)
                        txt.applyFont(titleFont)
                        cell.setCellValue(txt)
                        cell.cellStyle = dayStyle
                        xlWs.addMergedRegion(CellRangeAddress(tmpRow, tmpRow, tmpCol, tmpCol + spacing ))

                        tmpCol+= spacing + 2
                    }
                }

                tmpRow++
                tmpCol = colOffset

                //Write meeting
                mRow = xlWs.createRow(tmpRow)
                for(k in myWeek){
                    if(k.index!=-1){
                        for(l in k.dMeeting.indices){
                            cell = mRow.createCell(tmpCol)
                            txt = XSSFRichTextString(k.dMeeting[l])
                            txt.applyFont(normalFont)
                            cell.setCellValue(txt)
                            cell.cellStyle = mStyle
                            xlWs.addMergedRegion(CellRangeAddress(tmpRow, tmpRow, tmpCol, tmpCol+1))
                            tmpCol+=3
                        }
                    }
                }

                if(flagMeeting) tmpRow++
                tmpCol = colOffset

                //Write names
                mRow = xlWs.createRow(tmpRow)

                for(k in myWeek){
                    if(k.index!=-1){
                        //Find overall meeting index to get correct names
                        if(k.dMeeting.isEmpty()){
                            indexMeetingToMember.add(k.index)
                        }
                        else{
                            k.dMeeting.indices.forEach{indexMeetingToMember.add(it + indexMeetingOffset)}
                            indexMeetingOffset+= indexMeetingToMember.size
                        }

                        for(x in indexMeetingToMember) {
                            tmpDuo = duoNames[x].split(";")
                            //member
                            cell = mRow.createCell(tmpCol)
                            txt = XSSFRichTextString(tmpDuo[0])
                            txt.applyFont(memberFont)
                            cell.setCellValue(txt)
                            cell.cellStyle = mStyle
                            tmpCol++

                            //interviewer
                            cell = mRow.createCell(tmpCol)
                            txt = XSSFRichTextString(tmpDuo[1])
                            txt.applyFont(interviewerFont)
                            cell.setCellValue(txt)
                            cell.cellStyle = mStyle
                            tmpCol += 2
                        }
                        indexMeetingToMember.clear()
                    }
                }
            }
        }

        //Save Excel file
        try{
            val outputStream = FileOutputStream(filepath)
            xlWb.write(outputStream)
            outputStream.close()
        }

        catch(e : IOException){
            return false
        }

        xlWb.close()
        return true
    }

    /**
     * Method that clears the current planning data. It clears all ArrayList that describes the current planning.
     * It is used to handle back buttons and to be able to switch from a planning to another.
     */
    fun clear(){
        comment = COMMENT
        flagMeeting = false

        //Clear all lists
        people.clear()
        months.clear()
        monthsIndex.clear()
        days.clear()
        daysIndex.clear()
        meetingTimes.clear()

        schedules.clear()
        peopleList.clear()
    }
}