package game

sealed class BlockedState {
    class Combat(val battle: Battle) : BlockedState()
    class Hailed : BlockedState()
}
