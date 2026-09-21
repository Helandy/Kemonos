package su.afk.kemonos.profile.data.remote

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

internal class PawchiveSessionTest {

    @Test
    fun `reads the session cookie out of Set-Cookie headers`() {
        val headers = listOf(
            "other=1; Path=/",
            "$REAL_COOKIE; Expires=Sat, 23 Aug 2036 13:20:52 GMT; HttpOnly; Path=/; SameSite=Lax",
        )

        assertEquals(REAL_COOKIE.removePrefix("session="), headers.pawchiveSessionCookie())
    }

    @Test
    fun `no session cookie when the site did not set one`() {
        assertNull(listOf("other=1; Path=/").pawchiveSessionCookie())
        assertNull(emptyList<String>().pawchiveSessionCookie())
        assertNull(listOf("session=; Path=/").pawchiveSessionCookie())
    }

    @Test
    fun `reads the flash message a rejected login puts in the cookie`() {
        val session = REAL_COOKIE.removePrefix("session=")

        assertEquals("Username or password is incorrect", session.pawchiveFlashMessage())
    }

    @Test
    fun `no flash message when the cookie carries none`() {
        /** {"_permanent":true} */
        val noFlashes = "eyJfcGVybWFuZW50Ijp0cnVlfQ.ao7oNA.signature"

        assertNull(noFlashes.pawchiveFlashMessage())
    }

    @Test
    fun `garbage cookies do not blow up`() {
        assertNull("".pawchiveFlashMessage())
        assertNull("not-base64!!.x.y".pawchiveFlashMessage())
        assertNull(".compressed-payload.x.y".pawchiveFlashMessage())
    }

    private companion object {
        /**
         * Ровно то, что pawchive прислал на отклонённый вход:
         * {"_permanent":true,"_flashes":[{" t":["message","Username or password is incorrect"]}]}
         */
        const val REAL_COOKIE =
            "session=eyJfcGVybWFuZW50Ijp0cnVlLCJfZmxhc2hlcyI6W3siIHQiOlsibWVzc2FnZSIsIlVzZXJuYW1lIG9y" +
                "IHBhc3N3b3JkIGlzIGluY29ycmVjdCJdfV19.ao7oNA.1p9NdlzQLvM2NOFkkyB472vnnSM"
    }
}
