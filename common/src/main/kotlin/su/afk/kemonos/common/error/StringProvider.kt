package su.afk.kemonos.common.error

interface StringProvider {
    fun get(resId: Int): String
    fun get(resId: Int, vararg args: Any): String
}