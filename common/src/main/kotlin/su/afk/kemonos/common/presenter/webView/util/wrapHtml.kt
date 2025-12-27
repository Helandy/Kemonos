package su.afk.kemonos.common.presenter.webView.util

fun wrapHtml(
    body: String,
    textColor: Int,
    linkColor: Int,
    backgroundColor: Int,
    fontSizeSp: Float
): String {
    fun Int.toCssHex(): String = String.format("#%06X", 0xFFFFFF and this)

    val text = textColor.toCssHex()
    val link = linkColor.toCssHex()
    val bg = backgroundColor.toCssHex()

    return """
        <!doctype html>
        <html>
        <head>
          <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0">
          <style>
            :root { color-scheme: light dark; }

            html, body {
              margin: 0;
              padding: 0;
              background: $bg;
              color: $text;
              font-size: ${fontSizeSp}px; /* важно: px, не sp */
              line-height: 1.45;
              word-wrap: break-word;
              overflow-wrap: anywhere;
            }

            /* Reset для типичных тегов */
            p, h1, h2, h3, h4, h5, h6, ul, ol, li, blockquote, pre, figure {
              margin: 0;
              padding: 0;
            }

            /* Контролируемые отступы */
            p { margin: 0 0 10px 0; }
            h1, h2, h3, h4, h5, h6 { margin: 12px 0 8px 0; font-weight: 700; }

            a { color: $link; text-decoration: underline; }
            img {
              max-width: 100%;
              height: auto;
              display: block;
              margin: 8px 0;
            
              /* ключевое: рендер только когда близко к вьюпорту */
              content-visibility: auto;
              contain-intrinsic-size: 600px 400px;
            }

            /* Убираем «пустые» элементы, которые создают пустоты */
            p:empty { display: none; }
            a:empty { display: none; }

            /* Часто приходит <p><br></p> */
            p > br:only-child { display: none; }
          </style>
        </head>
        <body>
          $body
        </body>
        </html>
    """.trimIndent()
}
