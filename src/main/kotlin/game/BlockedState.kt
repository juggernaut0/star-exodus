package game

sealed class BlockedState {
    class Combat : BlockedState()
    class Hailed : BlockedState()
}
