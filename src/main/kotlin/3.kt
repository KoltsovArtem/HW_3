fun executeInstructions(instructions: List<String>, input: String): Boolean {
    var programCounter = 0
    var inputIndex = 0

    while (programCounter < instructions.size) {
        val instruction = instructions[programCounter]
        val parts = instruction.split(" ")

        when (parts[0]) {
            "char" -> {
                val charToMatch = parts[1].single()
                if (inputIndex < input.length && input[inputIndex] == charToMatch) {
                    inputIndex++
                    programCounter++
                } else {
                    return false
                }
            }

            "jmp" -> {
                val target = parts[1].toInt()
                programCounter = target
            }

            "split" -> {
                val targets = mutableListOf<Int>()
                var counter = 0
                for (i in 1 until parts.size - 1) {
                    targets.add(parts[i].split(",")[0].toInt())
                }
                targets.add(parts[parts.size - 1].toInt())
                for (i in 0 until targets.size-1) {
                    if (inputIndex < input.length &&
                        input[inputIndex].toString() == instructions[targets[i]].split(" ")[1]
                    ) {
                        programCounter = targets[i]
                        counter++
                        break
                    }
                }
                if (counter == 0 &&(inputIndex >= input.length ||
                            instructions[targets[targets.size-1]] == "match" ||
                            input[inputIndex].toString() == instructions[targets[targets.size-1]].split(" ")[1]
                            )) {
                    programCounter = targets[targets.size-1]
                    counter++
                }
                if (counter == 0){
                    return false
                }
            }

            "match" -> {
                return inputIndex == input.length
            }

            else -> throw IllegalArgumentException("Неподдерживаемая инструкция: $instruction")
        }
    }

    return false
}

fun selectAlphabet(regex: String): MutableSet<Char> {
    val operands = setOf<Char>('+', '?', '|', '*')
    val alphabet = mutableSetOf<Char>()
    for (char in regex) {
        if (!operands.contains(char)) {
            alphabet.add(char)
        }
    }
    return alphabet
}

fun generateInstructions(regex: String): List<String> {
    val alphabet = selectAlphabet(regex)
    val instructions = mutableListOf<String>()
    val pipeCount = mutableListOf<Int>(0)

    fun addInstruction(instruction: String) {
        instructions.add(instruction)
    }

    var current = 0
    while (current < regex.length) {
        val char = regex[current]
        when {
            char in alphabet -> {
                addInstruction("char $char")
                current++
            }

            char == '|' -> {
                if (pipeCount[0] == 0) {
                    val target1 = instructions.size
                    val target2 = instructions.size + 2
                    addInstruction("split $target1, $target2")
                    instructions[current] = instructions[current - 1]
                    instructions[current - 1] = "split $target1, $target2"
                    val nextTarget = target2 + 1
                    addInstruction("jmp $nextTarget")
                    pipeCount.add(current - 1)
                    pipeCount.add(current + 1)

                } else {
                    val target1 = instructions.size + 1
                    val target2 = instructions.size + 2
                    instructions[pipeCount[1]] += ", $target1"
                    for (i in 2 until pipeCount.size) {
                        instructions[pipeCount[i]] = "jmp $target2"
                    }
                    instructions.add("jmp $target2")
                    pipeCount.add(instructions.size - 1)
                }
                pipeCount[0]++
                current++
            }
            char == '?' -> {
                val target1 = instructions.size
                val target2 = instructions.size + 1

                addInstruction("split $target1, $target2")
                instructions[target1] = instructions[target1 - 1]
                instructions[target1 - 1] = "split $target1, $target2"
                if (pipeCount[0] != 0) {
                    for (i in 2 until pipeCount.size) {
                        instructions[pipeCount[i]] = "jmp $target2"
                    }
                }
                current++
            }

            char == '+' -> {
                val target1 = instructions.size - 1
                val target2 = instructions.size + 1
                addInstruction("split $target1, $target2")
                if (pipeCount[0] != 0) {
                    for (i in 2 until pipeCount.size) {
                        instructions[pipeCount[i]] = "jmp $target2"
                    }
                }
                current++
            }

            char == '*' -> {
                val target1 = instructions.size
                val target2 = instructions.size + 2
                addInstruction("split $target1, $target2")
                instructions[target1] = instructions[target1 - 1]
                instructions[target1 - 1] = "split $target1, $target2"

                val prevTarget = instructions.size - 2
                addInstruction("jmp $prevTarget")
                if (pipeCount[0] != 0) {
                    for (i in 2 until pipeCount.size) {
                        instructions[pipeCount[i]] = "jmp $target2"
                    }
                }
                current++

            }

            else -> {
                throw IllegalArgumentException("Unsupported character: $char")
            }
        }
    }

    addInstruction("match")
    return instructions
}

fun main() {
    val regex = "a*b+"
    val instructions = generateInstructions(regex)

    instructions.forEachIndexed { index, instruction ->
        println("$index $instruction")
    }
}