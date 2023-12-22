import java.io.File

data class Automaton(
    val numStates: Int,
    val alphabetSize: Int,
    val startStates: Set<Int>,
    val acceptStates: Set<Int>,
    val transitions: Map<Pair<Int, Int>, Set<Int>>
)

fun readAutomaton(file: File): Automaton {
    val lines = file.readLines()
    val numStates = lines[0].toInt()
    val alphabetSize = lines[1].toInt()
    val startStates = lines[2].split(" ").map { it.toInt() }.toSet()
    val acceptStates = lines[3].split(" ").map { it.toInt() }.toSet()
    val transitions = mutableMapOf<Pair<Int, Int>, MutableSet<Int>>()

    for (i in 4 until lines.size) {
        val parts = lines[i].split(" ")
        val fromState = parts[0].toInt()
        val symbol = parts[1].toInt()
        val toStates = parts.subList(2, parts.size).map { it.toInt() }.toSet()
        if (transitions.containsKey(Pair(fromState, symbol))) {
            transitions[Pair(fromState, symbol)]?.addAll(toStates)
        } else {
            transitions[Pair(fromState, symbol)] = toStates.toMutableSet()
        }
    }

    return Automaton(numStates, alphabetSize, startStates, acceptStates, transitions)
}

fun simulateAutomaton(automaton: Automaton, input: String): Boolean {
    fun checkPath(currentStates: Set<Int>, remainingInput: String): Boolean {
        if (remainingInput.isEmpty()) {
            return currentStates.intersect(automaton.acceptStates).isNotEmpty()
        }
        val symbol = remainingInput[0]
        val newStates = mutableSetOf<Int>()
        for (state in currentStates) {
            val transition = Pair(state, symbol.toString().toInt())
            val nextStates = automaton.transitions[transition] ?: emptySet()
            newStates.addAll(nextStates)
        }
        if (newStates.isEmpty()) {
            return false
        }
        for (state in newStates) {
            if (checkPath(setOf(state), remainingInput.substring(1))) {
                return true
            }
        }
        return false
    }

    return checkPath(automaton.startStates, input)
}



fun main(args: Array<String>) {
    val automatonFile = File(args[0])
    val input = args[1]

    val automaton = readAutomaton(automatonFile)

    val isAccepted = simulateAutomaton(automaton, input)

    println(isAccepted)
}
