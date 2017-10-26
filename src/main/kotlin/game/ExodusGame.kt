package game

class ExodusGame(resourceLoader: ResourceLoader) {
    val galaxy: Galaxy = Galaxy(400, 10000, resourceLoader.getStarNames())
    val fleet: Fleet = Fleet()

    var day = 0
        private set
}
