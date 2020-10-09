package com.lcaruso27.schedulepsi.app

import com.lcaruso27.schedulepsi.view.MainView
import javafx.scene.image.Image
import tornadofx.*

/**
 * Main application for SchedulePSI. Starts the MainView.
 * @see MainView
 * @see Styles
 *
 * @author Lorenzo Caruso
 * @version 1.0.0
 */

//v1.1.0
//TODO: Update styles and put same widths to buttons

//MainView refers to the first view of the application
//Styles refers to the css styles to apply to all application views
class ScheduleApp: App(MainView::class, Styles::class){
    init {
        setStageIcon(Image("icon_PSI_120pix.png"))
    }
}