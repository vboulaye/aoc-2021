package day16

import utils.asBinary


const val BITS_IN_CHAR = 4

enum class OperatorType {
    sum, multiply, min, max, value, greater, less, equals//, jump, jumpIfTrue, jumpIfFalse,
}

data class Packet(
    val version: Int,
    val operator: OperatorType,
    val subPackets: List<Packet> = emptyList(),
    val value: Long = 0L
) {

    fun sumVersions(): Long = version + subPackets.sumOf { it.sumVersions() }

    fun calculate(): Long {
        return when (operator) {
            OperatorType.value -> {
                value
            }
            OperatorType.sum -> {
                subPackets.sumOf { it.calculate() }
            }
            OperatorType.multiply -> {
                subPackets.fold(1L) { acc, packet -> acc * packet.calculate() }
            }
            OperatorType.min -> {
                subPackets.map { it.calculate() }.minOrNull()!!
            }
            OperatorType.max -> {
                subPackets.map { it.calculate() }.maxOrNull()!!
            }
            OperatorType.greater -> {
                check(subPackets.size == 2)
                if (subPackets[0].calculate() > subPackets[1].calculate()) 1 else 0
            }
            OperatorType.less -> {
                check(subPackets.size == 2)
                if (subPackets[0].calculate() < subPackets[1].calculate()) 1 else 0
            }
            OperatorType.equals -> {
                check(subPackets.size == 2)
                if (subPackets[0].calculate() == subPackets[1].calculate()) 1 else 0
            }
        }
    }
}


data class Parser(val input: List<String>) {
    val packet: Packet = parsePacket()

    private var index = 0

    private fun parsePacket(): Packet {
        val version = getNextBits(3).asBinary()
        val operatorTypeOrdinal = getNextBits(3).asBinary()
        val operatorType = OperatorType.values()[operatorTypeOrdinal]
        return when (operatorType) {
            OperatorType.value -> Packet(version, operatorType, value = parseNextValue())
            else -> Packet(version, operatorType, subPackets = parseOperatorValues())
        }
    }


    private fun parseNextValue(): Long {
        val stringBuilder = StringBuilder()
        var nextValue = getNextBits(5)
        val value = nextValue.substring(1)
        while (nextValue[0] == '1') {
            stringBuilder.append(value)
            nextValue = getNextBits(5)
        }
        stringBuilder.append(value)
        val longValue = stringBuilder.toString().toLong(2)
        return longValue
    }

    private fun parseOperatorValues(): List<Packet> {
        val subPacks = emptyList<Packet>().toMutableList()
        val lengthType = getNextBits(1)
        when (lengthType) {
            "0" -> {
                val totalLength = getNextBits(15).asBinary()
                val endPacket = index + totalLength
                while (index < endPacket - 1) {
                    subPacks.add(parsePacket())
                }
            }
            "1" -> {
                val totalBlocks = getNextBits(11).asBinary()
                (0 until totalBlocks).forEach { _ ->
                    subPacks.add(parsePacket())
                }
            }
        }
        return subPacks
    }

    private fun getNextBits(size: Int): String {
        val fullChars = size / BITS_IN_CHAR
        val remain = size % BITS_IN_CHAR
        val stringBuilder = StringBuilder()
        (0 until fullChars).forEach {
            val typeSting = getBitsFromChar(BITS_IN_CHAR)
            stringBuilder.append(typeSting)
        }
        stringBuilder.append(getBitsFromChar(remain))
        return stringBuilder.toString()
    }


    private fun getBitsFromChar(size: Int): String {
        val car = index / BITS_IN_CHAR
        val starPos = index % BITS_IN_CHAR
        val typeSting = if (starPos + size <= BITS_IN_CHAR) {
            input[car].substring(starPos, starPos + size)
        } else {
            input[car].substring(starPos) + input[car + 1].substring(0, size - (4 - starPos))
        }
        index += size
        return typeSting
    }

}
