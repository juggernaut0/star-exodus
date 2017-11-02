package game.command

import game.Game

interface Command {
    fun execute(state: Game)
}
