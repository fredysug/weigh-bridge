package android.template.core.ui

sealed class Screens(val route: String) {
    object Home : Screens("home")
    object AddTicket : Screens("add_ticket")
    object EditTicket : Screens("edit_ticket/{id}")
}
