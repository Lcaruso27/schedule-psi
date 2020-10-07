package com.lcaruso27.schedulepsi.app

import javafx.scene.text.FontWeight
import tornadofx.*

/**
 * Class that defines the whole application styles.
 */
class Styles : Stylesheet() {
    companion object {
        val heading by cssclass()
    }

    //Initialisation method called when class is created. Set some parameters of views.
    init {
        label and heading {
            fontSize = 20.px
            fontWeight = FontWeight.BOLD
        }
    }
}