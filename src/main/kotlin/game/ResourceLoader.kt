package game

interface ResourceLoader {
    fun loadStarNames(): List<String>
    fun loadShipNames(): List<String>
}