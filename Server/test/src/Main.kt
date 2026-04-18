import java.io.*

fun main() {
    val reader = BufferedReader(InputStreamReader(System.`in`))
    val n = reader.readLine().toInt()
    val tokens = reader.readLine().split(" ").map { it.toLong() }
    reader.close()

    val indexed = tokens.withIndex().sortedBy { it.value }
    val a = indexed.map { it.value }.toLongArray()
    val origIndex = indexed.map { it.index }.toIntArray()

    val prefix = LongArray(n)
    prefix[0] = a[0]
    for (i in 1 until n) prefix[i] = prefix[i - 1] + a[i]

    val lastLess = IntArray(n) { -1 }
    for (i in 1 until n) {
        lastLess[i] = if (a[i - 1] < a[i]) i - 1 else lastLess[i - 1]
    }

    val canWin = BooleanArray(n)
    if (n == 1) canWin[0] = true
    else canWin[n - 1] = lastLess[n - 1] != -1

    for (i in n - 2 downTo 0) {
        val sum = a[i] + if (lastLess[i] >= 0) prefix[lastLess[i]] else 0L
        canWin[i] = sum > a[i + 1] && canWin[i + 1]
    }

    val result = IntArray(n)
    for (i in 0 until n) result[origIndex[i]] = if (canWin[i]) 1 else 0
    println(result.joinToString(" "))
}