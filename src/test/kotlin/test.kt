import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals

class AutomatonTest {
    @Test
    fun test1() {
        val automatonFile = File("src/test/kotlin/test1.txt")
        val input = "10000"

        val automaton = readAutomaton(automatonFile)

        val isAccepted = simulateAutomaton(automaton, input)

        assertEquals(true, isAccepted)
    }

    @Test
    fun test2() {
        val automatonFile = File("src/test/kotlin/test1.txt")
        val input = "1001"

        val automaton = readAutomaton(automatonFile)

        val isAccepted = simulateAutomaton(automaton, input)

        assertEquals(false, isAccepted)
    }

    @Test
    fun test3() {
        val automatonFile = File("src/test/kotlin/test1.txt")
        val input = "101"

        val automaton = readAutomaton(automatonFile)

        val isAccepted = simulateAutomaton(automaton, input)

        assertEquals(false, isAccepted)
    }

    @Test
    fun test4() {
        val automatonFile = File("src/test/kotlin/test1.txt")
        val input = "111"

        val automaton = readAutomaton(automatonFile)

        val isAccepted = simulateAutomaton(automaton, input)

        assertEquals(false, isAccepted)
    }

    @Test
    fun test5() {
        val automatonFile = File("src/test/kotlin/test1.txt")
        val input = ""

        val automaton = readAutomaton(automatonFile)

        val isAccepted = simulateAutomaton(automaton, input)

        assertEquals(false, isAccepted)
    }

    @Test
    fun test2_1() {
        val automatonFile = File("src/test/kotlin/test1.txt")
        val dfaFile = File("src/test/kotlin/test2.txt")

        val nfa = readAutomaton2(automatonFile)

        val dfa = convertNfaToDfa2(nfa)

        writeAutomaton2(dfaFile, dfa)

        val dfa_test = readAutomaton2(dfaFile)

        assertEquals(simulateAutomaton2(nfa,"100"), simulateAutomaton2(dfa_test, "100"))
    }

    @Test
    fun test2_2() {
        val automatonFile = File("src/test/kotlin/test1.txt")
        val dfaFile = File("src/test/kotlin/test2.txt")

        val nfa = readAutomaton2(automatonFile)

        val dfa = convertNfaToDfa2(nfa)

        writeAutomaton2(dfaFile, dfa)

        val dfa_test = readAutomaton2(dfaFile)

        assertEquals(simulateAutomaton2(nfa,"1001"), simulateAutomaton2(dfa_test, "1001"))
    }

    @Test
    fun test2_3() {
        val automatonFile = File("src/test/kotlin/test1.txt")
        val dfaFile = File("src/test/kotlin/test2.txt")

        val nfa = readAutomaton2(automatonFile)

        val dfa = convertNfaToDfa2(nfa)

        writeAutomaton2(dfaFile, dfa)

        val dfa_test = readAutomaton2(dfaFile)

        assertEquals(simulateAutomaton2(nfa,""), simulateAutomaton2(dfa_test, ""))
    }

    @Test
    fun test2_4() {
        val automatonFile = File("src/test/kotlin/test1.txt")
        val dfaFile = File("src/test/kotlin/test2.txt")

        val nfa = readAutomaton2(automatonFile)

        val dfa = convertNfaToDfa2(nfa)

        writeAutomaton2(dfaFile, dfa)

        val dfa_test = readAutomaton2(dfaFile)

        assertEquals(simulateAutomaton2(nfa,"10000"), simulateAutomaton2(dfa_test, "10000"))
    }

    @Test
    fun test2_5() {
        val automatonFile = File("src/test/kotlin/test1.txt")
        val dfaFile = File("src/test/kotlin/test2.txt")

        val nfa = readAutomaton2(automatonFile)

        val dfa = convertNfaToDfa2(nfa)

        writeAutomaton2(dfaFile, dfa)

        val dfa_test = readAutomaton2(dfaFile)

        assertEquals(simulateAutomaton2(nfa,"1111"), simulateAutomaton2(dfa_test, "1111"))
    }

    @Test
    fun test3_1() {
        val nfaFile = File("src/test/kotlin/test2.txt")
        val minimDFA = File("src/test/kotlin/test3.txt")

        val nfa = readAutomaton3(nfaFile)

        val dfa = convertNfaToDfa3(nfa)

        val mdfa = minimizeDfa3(dfa)

        writeAutomaton3(minimDFA, mdfa)

        assertEquals(simulateAutomaton3(nfa,""), simulateAutomaton3(mdfa, ""))
    }

    @Test
    fun test3_2() {
        val nfaFile = File("src/test/kotlin/test2.txt")
        val minimDFA = File("src/test/kotlin/test3.txt")

        val nfa = readAutomaton3(nfaFile)

        val dfa = convertNfaToDfa3(nfa)

        val mdfa = minimizeDfa3(dfa)

        writeAutomaton3(minimDFA, mdfa)

        assertEquals(simulateAutomaton3(nfa,"10000"), simulateAutomaton3(mdfa, "10000"))
    }

    @Test
    fun test3_3() {
        val nfaFile = File("src/test/kotlin/test2.txt")
        val minimDFA = File("src/test/kotlin/test3.txt")

        val nfa = readAutomaton3(nfaFile)

        val dfa = convertNfaToDfa3(nfa)

        val mdfa = minimizeDfa3(dfa)

        writeAutomaton3(minimDFA, mdfa)

        assertEquals(simulateAutomaton3(nfa,"1001"), simulateAutomaton3(mdfa, "1001"))
    }

    @Test
    fun test3_4() {
        val nfaFile = File("src/test/kotlin/test2.txt")
        val minimDFA = File("src/test/kotlin/test3.txt")

        val nfa = readAutomaton3(nfaFile)

        val dfa = convertNfaToDfa3(nfa)

        val mdfa = minimizeDfa3(dfa)

        writeAutomaton3(minimDFA, mdfa)

        assertEquals(simulateAutomaton3(nfa,"111"), simulateAutomaton3(mdfa, "111"))
    }

    @Test
    fun test3_5() {
        val nfaFile = File("src/test/kotlin/test2.txt")
        val minimDFA = File("src/test/kotlin/test3.txt")

        val nfa = readAutomaton3(nfaFile)

        val dfa = convertNfaToDfa3(nfa)

        val mdfa = minimizeDfa3(dfa)

        writeAutomaton3(minimDFA, mdfa)

        assertEquals(simulateAutomaton3(nfa,"101"), simulateAutomaton3(mdfa, "101"))
    }

    @Test
    fun test4_1() {
        val regex = "a"
        val instructions = generateInstructions(regex)
        val input = "a"
        val isMatch = executeInstructions(instructions, input)

        assertEquals(true, isMatch)
    }

    @Test
    fun test4_2() {
        val regex = "ab+"
        val instructions = generateInstructions(regex)
        val input = "abb"
        val isMatch = executeInstructions(instructions, input)

        assertEquals(true, isMatch)
    }

    @Test
    fun test4_3() {
        val regex = "a*b"
        val instructions = generateInstructions(regex)
        val input = "aaaab"
        val isMatch = executeInstructions(instructions, input)

        assertEquals(true, isMatch)
    }

    @Test
    fun test4_4() {
        val regex = "a?b"
        val instructions = generateInstructions(regex)
        val input = "b"
        val isMatch = executeInstructions(instructions, input)

        assertEquals(true, isMatch)
    }

    @Test
    fun test4_5() {
        val regex = "a|b"
        val instructions = generateInstructions(regex)
        val input = "a"
        val isMatch = executeInstructions(instructions, input)

        assertEquals(true, isMatch)
    }

    @Test
    fun test4_6() {
        val regex = "a*b"
        val instructions = generateInstructions(regex)
        val input = "aac"
        val isMatch = executeInstructions(instructions, input)

        assertEquals(false, isMatch)
    }

    @Test
    fun test4_7() {
        val regex = "a*"
        val instructions = generateInstructions(regex)
        val input = ""
        val isMatch = executeInstructions(instructions, input)

        assertEquals(true, isMatch)
    }
}
