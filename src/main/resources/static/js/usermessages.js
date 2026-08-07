// Message bar - opens a message in a dialog and acknowledges it without leaving the page,
// so that half filled in forms are kept intact.
document.addEventListener('DOMContentLoaded', () => {

    document.querySelectorAll('.msgbar-link').forEach(link => {
        link.addEventListener('click', event => {
            event.preventDefault();
            const dialog = document.getElementById(link.dataset.dialog);
            if (dialog) {
                dialog.showModal();
            }
        });
    });

    document.querySelectorAll('.msgdialog .msgform').forEach(form => {
        form.addEventListener('submit', event => {
            event.preventDefault();

            const dialog = form.closest('.msgdialog');
            const button = form.querySelector('button[type="submit"]');
            if (button) {
                button.disabled = true;
            }

            fetch(form.action, {
                method: 'POST',
                body: new FormData(form),
                headers: {
                    'X-Requested-With': 'XMLHttpRequest'
                }
            })
            .then(response => {
                if (!response.ok) throw new Error('Meddelandet kunde inte kvitteras');
                removeMessage(dialog);
            })
            .catch(err => {
                if (button) {
                    button.disabled = false;
                }
                alert(err.message);
            });
        });
    });
});

function removeMessage(dialog) {

    const link = document.querySelector('.msgbar-link[data-dialog="' + dialog.id + '"]');
    if (link) {
        link.remove();
    }

    dialog.close();
    dialog.remove();

    const bar = document.querySelector('.msgbar');
    if (bar && bar.querySelectorAll('.msgbar-link').length === 0) {
        bar.remove();
    }
}