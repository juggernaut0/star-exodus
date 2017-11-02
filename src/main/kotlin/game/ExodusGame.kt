package game

import game.command.Command

class ExodusGame(resourceLoader: ResourceLoader) {
    private val state: Game

    init {
        val galaxy = Galaxy(10000, mutableSetOf()) // TODO
        val fleet = Fleet(mutableSetOf()) // TODO
        state = Game(galaxy, fleet, 0)
    }

    fun command(command: Command) = command.execute(state)

    //fun <T> query(query: Query<T>): T = query.execute(state)
}
