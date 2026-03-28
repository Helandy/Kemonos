package su.afk.kemonos.auth.news

import java.security.KeyStore

class CtyptoManager {
    private val keyStore = KeyStore.getInstance(KeyStore.getDefaultType()).apply {
        load(null)
    }


}