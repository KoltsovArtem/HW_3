import java.io.File
import kotlin.collections.contentDeepEquals

data class Automaton3(
    val numStates: Int,
    val alphabetSize: Int,
    val startStates: Set<Int>,
    val acceptStates: Set<Int>,
    val transitions: Map<Pair<Int, Int>, Set<Int>>
)

fun readAutomaton3(file: File): Automaton3 {
    val lines = file.readLines()
    val numStates = lines[0].toInt()
    val alphabetSize = lines[1].toInt()
    val startStates = lines[2].split(" ").map { it.toInt() }.toSet()
    val acceptStates = lines[3].split(" ").map { it.toInt() }.toSet()
    val transitions = mutableMapOf<Pair<Int, Int>, Set<Int>>()

    for (i in 4 until lines.size) {
        val parts = lines[i].split(" ")
        val fromState = parts[0].toInt()
        val symbol = parts[1].toInt()
        val toStates = parts.subList(2, parts.size).map { it.toInt() }.toSet()
        transitions[fromState to symbol] = toStates
    }

    return Automaton3(numStates, alphabetSize, startStates, acceptStates, transitions)
}

fun simulateAutomaton3(automaton3: Automaton3, input: String): Boolean {
    fun checkPath(currentStates: Set<Int>, remainingInput: String): Boolean {
        if (remainingInput.isEmpty()) {
            return currentStates.intersect(automaton3.acceptStates).isNotEmpty()
        }
        val symbol = remainingInput[0]
        val newStates = mutableSetOf<Int>()
        for (state in currentStates) {
            val transition = Pair(state, symbol.toString().toInt())
            val nextStates = automaton3.transitions[transition] ?: emptySet()
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

    return checkPath(automaton3.startStates, input)
}

fun convertNfaToDfa3(nfa: Automaton3): Automaton3 {
    val dfaTransitions = mutableMapOf<Pair<Int, Int>, Set<Int>>()
    val dfaStateSets = mutableMapOf<Set<Int>, Int>()
    val dfaStartStates = setOf(0)
    val dfaAcceptStates = mutableSetOf<Int>()

    dfaStateSets[nfa.startStates] = 0
    if (dfaStartStates.intersect(nfa.acceptStates).isNotEmpty()) {
        dfaAcceptStates.add(0)
    }

    val exploredStates = mutableSetOf<Set<Int>>()
    val queue = mutableListOf(nfa.startStates)

    while (queue.isNotEmpty()) {
        val currentState = queue.removeAt(0)
        for (symbol in 0 until nfa.alphabetSize) {
            val nextStates = currentState.flatMap { state ->
                nfa.transitions[state to symbol] ?: emptySet()
            }.toSet()

            if (nextStates in exploredStates)
                continue

            queue.add(nextStates)
            exploredStates.add(nextStates)

            val currentStateId = dfaStateSets[currentState] ?: error("No DFA state corresponds to NFA state set")
            val nextStateId = if (nextStates in dfaStateSets) {
                dfaStateSets[nextStates]!!
            } else {
                val id = dfaStateSets.size
                dfaStateSets[nextStates] = id
                if (nextStates.intersect(nfa.acceptStates).isNotEmpty()) {
                    dfaAcceptStates.add(id)
                }
                id
            }

            dfaTransitions[currentStateId to symbol] = setOf(nextStateId)
        }
    }

    return Automaton3(
        dfaStateSets.size,
        nfa.alphabetSize,
        dfaStartStates,
        dfaAcceptStates,
        dfaTransitions
    )
}

fun minimizeDfa3(dfa: Automaton3): Automaton3 {
    val transitionsArray = Array(dfa.numStates) { i ->
        Array(dfa.alphabetSize) { j ->
            dfa.transitions[i to j]?.single() ?: -1
        }
    }

    val groups = mutableListOf(dfa.acceptStates.toMutableList(), (0 until dfa.numStates).filter { it !in dfa.acceptStates }.toMutableList())
    val groupIds = MutableList(dfa.numStates) { if (it in dfa.acceptStates) 0 else 1 }

    var changed: Boolean
    do {
        changed = false
        for (groupId in groups.indices) {
            val representative = groups[groupId].first()
            val representativeTransitions = transitionsArray[representative]

            val sameTransitions = mutableListOf<Int>()
            val differentTransitions = mutableListOf<Int>()

            for (state in groups[groupId]) {
                val stateTransitions = transitionsArray[state]
                if (stateTransitions.contentDeepEquals(representativeTransitions)) {
                    sameTransitions.add(state)
                } else {
                    differentTransitions.add(state)
                }
            }

            if (differentTransitions.isNotEmpty()) {
                groups[groupId] = sameTransitions
                groups.add(differentTransitions)
                differentTransitions.forEach { groupIds[it] = groups.size - 1 }
                changed = true
            }
        }
    } while (changed)

    val startStates = dfa.startStates.map { groupIds[it] }.toSet()
    val acceptStates = dfa.acceptStates.map { groupIds[it] }.toSet()
    val transitions = mutableMapOf<Pair<Int, Int>, Set<Int>>()

    for ((key, value) in dfa.transitions) {
        val (fromState, symbol) = key
        val toState = value.single()
        val fromGroupId = groupIds[fromState]
        val toGroupId = groupIds[toState]
        transitions[fromGroupId to symbol] = setOf(toGroupId)
    }

    return Automaton3(groups.size, dfa.alphabetSize, startStates, acceptStates, transitions)
}




fun writeAutomaton3(file: File, automaton3: Automaton3) {
    file.writeText("")
    file.appendText("${automaton3.numStates}\n")
    file.appendText("${automaton3.alphabetSize}\n")
    file.appendText("${automaton3.startStates.joinToString(" ")}\n")
    file.appendText("${automaton3.acceptStates.joinToString(" ")}\n")
    for ((key, value) in automaton3.transitions) {
        val fromState = key.first
        val symbol = key.second
        val toStates = value.joinToString(" ")
        file.appendText("$fromState $symbol $toStates\n")
    }
}

fun main(args: Array<String>) {
    val nfaFile = File(args[0])
    val minimizedDfaFile = File(args[1])

    val nfa = readAutomaton3(nfaFile)
    val dfa = convertNfaToDfa3(nfa)
    val minimizedDfa = minimizeDfa3(dfa)

    writeAutomaton3(minimizedDfaFile, minimizedDfa)
}