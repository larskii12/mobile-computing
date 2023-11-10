# mobile-computing
Repository for Mobile Computing Group 2 Tutorial 12

Project description
-------------------

UniNooks is a mobile application designed to enhance the academic experience of students at the University of Melbourne. The applicatiom integrates exploration of study spaces within the University of Melbourne and a dedicated focus mode to optimize study sessions.

Real-time information is utilized to inform users of opening hours, capacity (how busy a place is), and distance to the study spaces from their current location. Users also have the choice to run an automatic Pomodoro sequence to efficiently manage their study time and boost productivity.

Built with
-------------------
Java (Gradle)

Installation
-------------------
### Running on emulator

For optimal performance, the emulator should use Android Version 13, API Version 33 (Tiramisu).

To install, enter this into the terminal of Android Studio:

            git clone https://github.com/larskii12/mobile-computing.git

Before running the project, set the emulators’ default location in Running Devices.
Steps:
1. Navigate to Running Devices tab
2. Go to Extended Controls - Location
3. Search for Melbourne Connect
4. Save Point
5. Set Location

After these steps, build and run the application, by pressing the build then run icons.

### Running on physical phone
1. From the zip file, retrieve *uninooks.apk*
2. Click on the apk file to install the application locally on your android device


Regardless of where the applications are ran, all permissions would have to be granted, to experience the full functionality of the application.

## Code description

### Java activity class

AccountActivity
- This class is used to display the user's account information. It is also used to update the user's account information, login and delete account.

FilterAdjustmentActivity
- For the serach page, this class is used to display the filter adjustment activity. It is also used to update the filter settings.

FocusModeMainActivity
- This class is used to display the focus mode main activity. It is also used to start the focus mode timer activity.

FocusModeSettingsActivity
- This class is used to display the focus mode settings activity. It is also used to update the focus mode settings.

FocusModeTimerActivity
- This class is used to display the focus mode timer activity.

HomeActivity
- This class is used to display the home page. It shows the nearby study spaces and the user's favourite study spaces.

IntroActivity
- This class is used to play onboarding slides for new users.

IntroViewPagerActivity
- This class is a helper class for IntroActivity.

LocationActivity
- This class is used to display a location activity. It contains information such as how far it is from the current location and reviews.

LoginActivity
- This class is used to login the user.

MainActivity
- This class is for splash screen where the permissions are prompted.

MapsActivity
- This class is

NavigationActivity
- This class handles the navigation drawer using googleMap API.

PersonalInformationActivity
- This class is for users to change their personal information.

ReportIssue
- This class is for users to report issues.

ResetPasswordActivity
- This class is for users to reset their password.

ScreenItem
- This is a helper class for IntroActivity.

SearchResults
- This class shows a map and a list of study spaces based on the user's search criteria.

SignUpActivity
- This class is for users to sign up for an account for the first time.

### Layout XML Files


activity_account.xml
- This file is for the UI of the account activity.

activity_account_personal_information.xml
- This file is for the UI of the personal information activity.

activity_filter_adjustment.xml
- This file is for the UI of the filter adjustment activity.

activity_focus_mode_main.xml
- This file is for the UI of the focus mode main activity, telling users what this mode is about.

activity_focus_mode_notification.xml
- This file is for the UI of the focus mode notification activity, telling users to stay focused.

activity_focus_mode_settings.xml
- This file is for the UI of the focus mode settings activity.

activity_focus_splash.xml
- This is a splash screen for the focus mode.

activity_home.xml
- This file is for the UI of the home activity.

activity_intro.xml
- This file is for oen of the UI elements of the onboarding page it has button and progress bar.

activity_location.xml
- This file is for the UI of the location activity, showing reviews, distance from the current location and a map.

activity_login.xml
- This file is for the UI of the login activity.

activity_main.xml
- This is a splashback screen for when the app is launched.

activity_maps.xml
- This is a map activity UI showing a search bar and filter at the top.

activity_navigation.xml
- This is a UI for the navigation when taking the user to the destination.

activity_report_issue.xml
- This is a simple UI for the report issue page.

activity_reset_password.xml
- This is a layout for the reset password page.

activity_search_results.xml
- This is a layout for the search results page to show the results.

activity_sign_up.xml
- This is a layout for the sign up page.

add_review_dialog.xml
- This is a custom pop up dialog for users to add a review.

amenity_layout.xml
- This is a layout to show what amenities are available at each study space.

delete_account_confirmation_dialog.xml
- This is a custom pop up dialog for users to confirm if they want to delete their account.

information_dialog.xml
- This is a custom pop up dialog for users to learn what each icon means.

large_location_card.xml
- This is a layout for the location card on the home page.

layout_screen.xml
- This is a layout for the onboarding page.

list_card_layout.xml
- This is a layout for the list of study spaces.

list_group.xml
- This is a layout for the list of amenities.

logout_confirmation_dialog.xml
- This is a custom pop up dialog for users to confirm if they want to log out.

review_layout.xml
- This is a layout to show reviews for each study space.

shake_dialog.xml
- This is a custom pop up dialog to encourage users to use a shake feature to get a randomised recommendation for a study space.

small_card_layout.xml

top_layout_card.xml
- These two are small cards layout for the top card on the home page.


Database
-------------------
The database utilizes a self-managed relational database with PostgreSQL, deployed on a Linux server.

To access the database:
1. Download PGAdmin (https://www.pgadmin.org/download/)
2. Enter the following details:

           Host name/address: 103.119.109.139
           Port: 5432
           Maintenance database: mobile
           Kerberos authentication: off
           Password: iHgm5npedxNHkpWwFb99SWW28Z5dGgKvYhncehaWGvmbzWVXr3
           Save password: off

All passwords are hashed with salt within the application then transmitted to the database.

Usage and Assumptions
-------------------
1. The running system and device is always running in light mode.
2. The data on how busy a place is, is retrieved from and provided by the University of Melbourne.
3. All permissions are granted. If precise location permissions are not turned
4. Animation scale settings on the device is set to 1 as this will affect the timer animation in Focus Mode.
5. Location of the emulator is always at Melbourne Connect.
6. Turning off notifications and precise locations permissions in the application will restart it.