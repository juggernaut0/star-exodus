package game

class ExodusGame(resourceLoader: ResourceLoader) {
    val galaxy: Galaxy = Galaxy(400, 10000, resourceLoader.getStarNames())
    val fleet: Fleet = Fleet(20, resourceLoader.getShipClasses().associateBy { it.name }, resourceLoader.getShipNames())

    var day = 0
        private set

    fun nextDay(){
        day += 1

        fleet.abandonUncrewed()

    }
}
