import java.io.File

data class Automaton2(
    val numStates: Int,
    val alphabetSize: Int,
    val startStates: Set<Int>,
    val acceptStates: Set<Int>,
    val transitions: Map<Pair<Int, Int>, Set<Int>>
)

fun readAutomaton2(file: File): Automaton2 {
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

    return Automaton2(numStates, alphabetSize, startStates, acceptStates, transitions)
}

fun simulateAutomaton2(automaton2: Automaton2, input: String): Boolean {
    fun checkPath(currentStates: Set<Int>, remainingInput: String): Boolean {
        if (remainingInput.isEmpty()) {
            return currentStates.intersect(automaton2.acceptStates).isNotEmpty()
        }
        val symbol = remainingInput[0]
        val newStates = mutableSetOf<Int>()
        for (state in currentStates) {
            val transition = Pair(state, symbol.toString().toInt())
            val nextStates = automaton2.transitions[transition] ?: emptySet()
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

    return checkPath(automaton2.startStates, input)
}

fun convertNfaToDfa2(nfa: Automaton2): Automaton2 {
    val dfaTransitions = mutableMapOf<Pair<Int, Int>, Set<Int>>()
    val dfaStartStates = setOf(0)
    val dfaStateSets = mutableMapOf(0 to nfa.startStates)
    val dfaAcceptStates = mutableSetOf<Int>()
    val dfaQueue = mutableListOf(0)

    var nextDfaState = 1

    while (dfaQueue.isNotEmpty()) {
        val currentState = dfaQueue.removeFirst()
        val correspondingNfaStates = dfaStateSets[currentState] ?: error("No corresponding NFA states")

        if (correspondingNfaStates.intersect(nfa.acceptStates).isNotEmpty()) {
            dfaAcceptStates.add(currentState)
        }

        for (symbol in 0 until nfa.alphabetSize) {
            val nextNfaStates = correspondingNfaStates.flatMap { nfa.transitions[Pair(it, symbol)] ?: emptyList() }.toSet()
            if (nextNfaStates.isNotEmpty()) {
                val existingState = dfaStateSets.entries.find { it.value == nextNfaStates }?.key
                if (existingState == null) {
                    dfaQueue.add(nextDfaState)
                    dfaStateSets[nextDfaState] = nextNfaStates
                    dfaTransitions[Pair(currentState, symbol)] = setOf(nextDfaState)
                    nextDfaState++
                } else {
                    dfaTransitions[Pair(currentState, symbol)] = setOf(existingState)
                }
            }
        }
    }

    return Automaton2(
        dfaStateSets.size,
        nfa.alphabetSize,
        dfaStartStates,
        dfaAcceptStates,
        dfaTransitions
    )
}

fun writeAutomaton2(file: File, automaton2: Automaton2) {
    file.writeText("")
    file.appendText("${automaton2.numStates}\n")
    file.appendText("${automaton2.alphabetSize}\n")
    file.appendText("${automaton2.startStates.joinToString(" ")}\n")
    file.appendText("${automaton2.acceptStates.joinToString(" ")}\n")
    for ((transition, toStates) in automaton2.transitions) {
        val fromState = transition.first
        val symbol = transition.second
        file.appendText("$fromState $symbol ${toStates.joinToString(" ")}\n")
    }
}


fun main(args: Array<String>) {
    val nfaFile = File(args[0])
    val dfaFile = File(args[1])

    val nfa = readAutomaton2(nfaFile)

    val dfa = convertNfaToDfa2(nfa)

    writeAutomaton2(dfaFile, dfa)
}