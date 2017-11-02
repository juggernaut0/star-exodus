package game

interface ResourceLoader {
    fun getStarNames(): List<String>
    fun getShipNames(): List<String>
    fun getShipClasses(): List<ShipClass>
}
