package com.lcaruso27.schedulepsi.model

import com.lcaruso27.schedulepsi.controller.ExportViewController
import javafx.beans.property.*
import tornadofx.*

/**
 * String of the class name. Useful for debugging only.
 */
private val TAG = People::class.simpleName

/**
 * Class that describes a member with all fields present in the Excel file and updated during the process.
 * It inherits from the [JsonModel] class to be able to read and write such data in Json file.
 *
 * @param name String of the member name.
 * @param availTab ArrayList of Int where each element specifies whether the member is available (1) or not (0).
 * @param numAvail Int of the number of availabilities of the member.
 * @param interviewer Boolean that indicates whether the member is an interview. Set to false at first.
 * @param date String of the scheduled interview date. Set to "A planifier" at first.
 * @param interviewBy String of the interviewer name. Set to "A definir" at first.
 */
class People(name: String, availTab: ArrayList<Int>, numAvail : Int,
             interviewer : Boolean = false, date: String ="A planifier", interviewBy: String ="A definir") : JsonModel{

    /**
     * SimpleStringProperty binded to the name property of the member. Used to access only [name] value.
     */
    val nameProperty = SimpleStringProperty(this,"",name)

    /**
     * String of the member name. Linked to nameProperty.
     */
    private val name by nameProperty

    /**
     * SimpleObjectProperty binded to the [availTab] property of the member. Used to access only [availTab] value.
     */
    val availTabProperty = SimpleObjectProperty(this,"",availTab)

    /**
     * ArrayList of Int that describes the availabilities of the member. Each value correspond to one date (month + date + hour).
     * Only two possible values :
     *  * 0 : member busy for this meeting time
     *  * 1 : member available for this meeting time
     */
    private val availTab by availTabProperty

    /**
     *  SimpleIntegerProperty binded to the [numAvail] property of the member. Used to access only [numAvail] value.
     */
    val numAvailProperty = SimpleIntegerProperty(this,"",numAvail)

    /**
     * Int that describes the number of availabilities of a member. Used to sort the member by this value (increasing order).
     */
    private val numAvail by numAvailProperty

    /**
     *  SimpleBooleanProperty binded to the interviewer property of the member. Used to access and update interviewer value.
     */
    val interviewerProperty = SimpleBooleanProperty(this,"",interviewer)

    /**
     * Boolean that describes whether the member is an interviewer or not. This value is set to false for all members
     * at first. The user has to select the interviewers among the members during the process, the value is then updated.
     */
    private var interviewer by interviewerProperty

    /**
     * SimpleStringProperty binded to the [date] property of the member. Used to access and update [date] value.
     */
    val dateProperty = SimpleStringProperty(this,"",date)

    /**
     * String of the meeting date date/month year/hour like "lun. 16/Avril 2020/13:00 14:00". This value is set to "A planifier"
     * for all members at first and is updated by the user during the process.
     */
    private var date by dateProperty

    /**
     * SimpleStringProperty binded to the [interviewBy] property of the member. Used to access and update [interviewBy] value.
     */
    val interviewByProperty = SimpleStringProperty(this, "", interviewBy)

    /**
     * String of the member name among the interviewers who will interview the current member. It is possible to have
     * several interviewers like John Doe/Jane Doe/Droopy. This value is set to "A definir" for all members at first
     * and it is updated by the user during the process.
     */
    private var interviewBy by interviewByProperty

//    /**
//     * Method useless.
//     *
//     * @param json JsonObject where People fields are present.
//     */
//    override fun updateModel(json: JsonObject) {
//        val jsonPeople = json.getJsonArray("People")
//        jsonPeople.forEach{
//            interviewer = it.asJsonObject().bool("Interviewer")!!
//            date = it.asJsonObject().string("Date")
//            interviewBy = it.asJsonObject().string("InterviewBy")
//        }
//    }

    /**
     * Method that creates the JSON file with People fields.
     *
     * @param json Empty JsonBuilder where the structure and the [People] class content is written.
     */
    override fun toJSON(json: JsonBuilder) {
        with(json){
            add("Name", name)
            add("AvailTab", availTab)
            add("NumAvail", numAvail)
            add("Interviewer", interviewer)
            add("Date", date)
            add("InterviewBy", interviewBy)
        }
    }
}

/**
 * Class that enables to get People class as an [ItemViewModel]. Not used. This class is useful when the user needs to
 * create a [People] from the UI.
 */
class PeopleModel: ItemViewModel<People>() {

    /**
     * String of the member name. Linked to [nameProperty].
     */
    val name = bind(People::nameProperty)

    /**
     * ArrayList of Int that describes the availabilities of the member. Each value correspond to one date (month + date + hour).
     * Only two possible values :
     *  * 0 : member busy for this meeting time
     *  * 1 : member available for this meeting time
     */
    val availTab = bind(People::availTabProperty)

    /**
     * Int that describes the number of availabilities of a member. Used to sort the member by this value (increasing order).
     */
    val numAvail = bind(People::numAvailProperty)

    /**
     * Boolean that describes whether the member is an interviewer or not. This value is set to false for all members
     * at first. The user has to select the interviewers among the members during the process, the value is then updated.
     */
    val interviewer = bind(People::interviewerProperty)

    /**
     * String of the meeting date date/month year/hour like "lun. 16/Avril 2020/13:00 14:00". This value is set to "A planifier"
     * for all members at first and is updated by the user during the process.
     */
    val date = bind(People::dateProperty)

    /**
     * String of the member name among the interviewers who will interview the current member. It is possible to have
     * several interviewers like John Doe/Jane Doe/Droopy. This value is set to "A definir" for all members at first
     * and it is updated by the user during the process.
     */
    val interviewBy = bind(People::interviewByProperty)
}