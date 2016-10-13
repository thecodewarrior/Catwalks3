package catwalks

/**
 * Created by TheCodeWarrior
 */

fun Int.combonation_bits(): Int {
    var value = this - 1
    var count = 0
    while (value > 0) {
        count++
        value = value shr 1
    }
    return count
}

fun Int.bits(): Int {
    var value = this
    var count = 0
    while (value > 0) {
        count++
        value = value shr 1
    }
    return count
}
