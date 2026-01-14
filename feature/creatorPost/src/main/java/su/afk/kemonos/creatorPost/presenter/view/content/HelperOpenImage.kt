package su.afk.kemonos.creatorPost.presenter.view.content

internal const val IMAGE_CLICK_HOOK_JS = """
<script>
(function() {
  function absUrl(src) {
    try { return new URL(src, document.baseURI).href; } catch (e) { return src; }
  }

  document.addEventListener('click', function(e) {
    var el = e.target;
    if (!el) return;

    // Ловим клик по IMG
    if (el.tagName === 'IMG') {
      e.preventDefault();
      e.stopPropagation();

      var src = el.currentSrc || el.src || el.getAttribute('src');
      if (!src) return;

      src = absUrl(src);
      window.location.href = 'kemonos://open_image?url=' + encodeURIComponent(src);
    }
  }, true); // capture=true, чтобы перехватывать даже если IMG внутри <a>
})();
</script>
"""