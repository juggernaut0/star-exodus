package ui.components

import kui.*
import ui.GameService

class StarExodusApp(private val svc: GameService) : Component() {
    override fun render() {
        markup().div(classes("container-fluid")) {
            component(GlobalDisplayPanel(svc))
            ul(classes("nav", "nav-tabs")) {
                tab("Log", href = "#main", active = true)
                tab("Fleet", href = "#fleet")
                tab("Star System", href = "#star")
                tab("Combat Sim", href = "#combat")
            }

            div(classes("tab-content")) {
                div(Props(id = "main", classes = listOf("tab-pane", "active"))) { component(LogTabComponent(svc)) }
                div(Props(id = "fleet", classes = listOf("tab-pane"))) { component(FleetTabComponent(svc)) }
                div(Props(id = "star", classes = listOf("tab-pane"))) { component(StarTabComponent(svc)) }
                div(Props(id = "combat", classes = listOf("tab-pane"))) { component(CombatSimTab(svc)) }
            }
        }
    }

    private fun MarkupBuilder.tab(text: String, href: String, active: Boolean = false) {
        li(classes("nav-item")) {
            a(Props(classes = listOfNotNull("nav-link", "active".takeIf { active }), attrs = mapOf("data-toggle" to "tab")), href = href) {
                +text
            }
        }
    }
}
