package game

class ExodusGame(resourceLoader: ResourceLoader) {
    private val galaxy = Galaxy(400, 10000, resourceLoader.loadStarNames())
    private val fleet = Fleet()

    private var day = 0
}
