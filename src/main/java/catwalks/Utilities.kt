package catwalks

/**
 * Created by TheCodeWarrior
 */

fun YELL_AT_DEV() {
    error("HORRIBLE THINGS ARE HAPPENING!") // BREAKPOINT HERE OR YOU ARE A HORRIBLE PERSON!!!!!
}

@Throws(DEV_SCREAMING_EXCEPTION::class)
fun SCREAM_AT_DEV() {
    YELL_AT_DEV()
    throw DEV_SCREAMING_EXCEPTION()
}

class DEV_SCREAMING_EXCEPTION : RuntimeException("I'M SCREAMING AT YOU CAUSE YOU DIDN'T DO GOOD!!!!")

