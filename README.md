# Clinic Scheduler with Graphics Project
### Abstract
This is our preliminary project using JavaFx. As the title implies, the project allows a user to schedule, cancel, and reschedule appointments. Additionally, users can sort through lists of appointments and generate financial statements using different sorting criteria. Users must upload a list of providers upon startup for the project to function.

### Uploading provider info
In order for the project to function, the first thing a user should do is click on the "Load providers" box and upload a text file with provider information. Below is the format for the text file.

- The file should be a .txt file, and each line in the txt file should have the following information in the exact order presented:
	- The first character should be a 'D' or 'T'
		'D' means doctor (PCP)
		'T' means technician (imaging/radiologist)
	- The name of the provider, first and last name
	- The birthdate of the provider
	- The location of the provider's office
	- The specialty of the provider if the provider is classified with 'D'
	- The doctor's national provider identification number (NPI), which a unique number used to identify the doctor.

For a good example, check out the providers.txt file. This was the file that was used to test the program during development.

Once the file has been loaded successfully, a confirmation message will be printed to the output, showing the doctors and technicians have been accepted into the system.

### Schedule Appointments
To schedule an appointment, the user has to provide the following information
- Appointment Date,
- Patient first name,
- Patient last name,
- Patient's DOB
- Type of visit (is the appointment with PCP or imaging/radiologist)
	- if Office, the doctor's NPI number must be provided
	- if Imaging, the type of imaging service must be specified
- Time of appointment that patient requests
- Provider for appointment

Once the schedule button is clicked, the output will print a confirmation message if the appointment was scheduled. If the appointment was not scheduled, an error message will be printed specifying the reason why.

### Cancel Appointments
Same as scheduling appointment, but instead of clicking on the schedule button, you would click the cancel button. The output will print a confirmation message if the appointment was cancelled. If the appointment was not cancelled, an error message will be printed specifying why.

### Reschedule Appointments
Provided the following information, the user can reschedule the time of an appointment to be a different time on the same day. 
- First name of patient
- Last name of patient
- DOB of patient
- Date of appointment of the patient
- Current time of appointment
- New desired time of appointment

If the appointment was able to be rescheduled, a confirmation message will be printed. If not, an error message will be printed. 

If the patient needs to reschedule the appointment to a different day, it is best to cancel the appointment altogether and then schedule the appointment again on the new day.

### Clinic Locations
This menu shows the available locations in the system that a user can book appointments at. This is used for the user's reference only.

### Demo Bar
Using this bar, all above functions are possible, as well as listing the appointments booked in the system. Financial statements can also be generated from this menu.

Appointments can be listed by date/time/provider, by location, or by patient name, or by type of provider.

Users can generate statement showing how much money is owed by each patient or how much money is owed to each provider.

### Output
Lists, statements, confirmation, and error messages will be printed in the output section of the JavaFx scene

### Quitting the Program
Once you quit the program, all information that was entered (i.e. appointments made, providers list) will not be stored. The user will have to reinput all the information again if they need to restart the program.

### Future Updates
In the future, we may change the JavaFx panel to be more visually appealing and user-friendly. There are currently no established plans to change the functionality of the project.
