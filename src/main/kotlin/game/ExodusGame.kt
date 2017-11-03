package game

class ExodusGame(resourceLoader: ResourceLoader) {
    val galaxy: Galaxy = Galaxy(400, 10000, resourceLoader.getStarNames())
    val fleet: Fleet

    init {
        ShipClass.initClasses(resourceLoader.getShipClasses())

        fleet = Fleet(20, resourceLoader.getShipNames())
    }

    var day = 0
        private set

    fun nextDay(){
        day += 1

        fleet.abandonUncrewed()

    }
}
