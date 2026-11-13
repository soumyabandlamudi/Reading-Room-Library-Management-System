// Colour palette + dark mode, confirm dialogs and auto-submit selects.
// Kept in a file (not inline) so the Content-Security-Policy can block injected scripts.
(function () {
  var root = document.documentElement;
  var MODE_KEY = "rr-theme", PAL_KEY = "rr-palette";
  var PALETTES = ["ocean", "emerald", "violet", "rose", "mono"];

  try {
    var mode = localStorage.getItem(MODE_KEY);
    if (mode === "dark" || mode === "light") root.setAttribute("data-theme", mode);
    var pal = localStorage.getItem(PAL_KEY);
    if (PALETTES.indexOf(pal) >= 0) root.setAttribute("data-palette", pal);
  } catch (e) {}

  document.addEventListener("DOMContentLoaded", function () {
    var btn = document.querySelector("[data-theme-toggle]");
    function current() {
      var t = root.getAttribute("data-theme");
      if (t) return t;
      return window.matchMedia && window.matchMedia("(prefers-color-scheme: dark)").matches ? "dark" : "light";
    }
    function label() { if (btn) btn.textContent = current() === "dark" ? "Light mode" : "Dark mode"; }
    if (btn) {
      label();
      btn.addEventListener("click", function () {
        var next = current() === "dark" ? "light" : "dark";
        root.setAttribute("data-theme", next);
        try { localStorage.setItem(MODE_KEY, next); } catch (e) {}
        label();
      });
    }

    var sel = document.querySelector("[data-palette-select]");
    if (sel) {
      sel.value = root.getAttribute("data-palette") || "ocean";
      sel.addEventListener("change", function () {
        if (PALETTES.indexOf(sel.value) < 0) return;
        root.setAttribute("data-palette", sel.value);
        try { localStorage.setItem(PAL_KEY, sel.value); } catch (e) {}
      });
    }

    document.querySelectorAll("form[data-confirm]").forEach(function (f) {
      f.addEventListener("submit", function (e) {
        if (!window.confirm(f.getAttribute("data-confirm"))) e.preventDefault();
      });
    });
    document.querySelectorAll("select[data-autosubmit]").forEach(function (s) {
      s.addEventListener("change", function () { s.form.submit(); });
    });
  });
})();
