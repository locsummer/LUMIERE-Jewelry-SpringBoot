document.addEventListener('DOMContentLoaded', function () {
  document.querySelectorAll('[data-confirm]').forEach(function (button) {
    button.addEventListener('click', function (event) {
      if (!window.confirm(button.getAttribute('data-confirm'))) event.preventDefault();
    });
  });
  window.setTimeout(function () {
    document.querySelectorAll('.flash-wrap .alert, .admin-flash .alert').forEach(function (alert) {
      if (window.jQuery) window.jQuery(alert).alert('close');
    });
  }, 4500);
});
