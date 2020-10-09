# SchedulePSI - Brief tutorial

The executable file has been created thanks to the JAR export feature from IntelliJ IDEA 2020 
and [Launch4J](http://launch4j.sourceforge.net/) tool to convert JAR file into EXE one.

## Quick start

Double-click on SchedulePSI.exe.

Here under some explanations about the pipeline of the planning procedure.

 * First frame - MainView:	
	* Choose the Excel file with planning information (new Excel file or previous planning in json file).
	* Specify if members indicated their availabilities or non-availabilities in the doodle! (only for new planning)

 * Second frame - InterviewersView:
	* Choose interviewers among the members.

 * Third frame 1 - SummaryScheduleView:
	* Summary of the planning, click on "Voir planning" to begin to schedule.
	* "Vérifier planning" enables to check that several meetings do not overlap.

 * Third frame 2 - ScheduleView:
	* Double-click on member name at left to see the possible meetings. Green boxes are possible meetings and red boxes are not available meeting. 
		Put the mouse on the box to have further details: which interviewers are available (green box) or why the meeting is impossible: member or interviewer not available.
	* Click on a green box and "Valider créneau" to select a meeting time with an interviewer.
	* Repeat this process for all members you want ! (Don't forget to plan the interviews of the interviewers)
	* Go back to the previous view to go further.

 * Fifth frame - ExportView:
	* Add a comment attached to the planning by filling the text area and clicking on "Valider commentaire" button.
	* You can export the planning into a nice Excel file to inform the members about the planning.
	* When you have finished, the "Terminer" button closed the window. The planning scheduled is saved as a JSON file in the MyPlannings folder 
		and would be displayed when tool would be used again.

Well done, you've scheduled quickly meetings !
