const csrfToken = document.querySelector('meta[name="_csrf"]')?.content || "";
const csrfHeader = document.querySelector('meta[name="_csrf_header"]')?.content || "X-CSRF-TOKEN";

document.addEventListener("DOMContentLoaded", () => {
    // Event delegation on the table container
    const table = document.querySelector("table");
    if (!table) return;

    table.addEventListener("click", (e) => {
        const target = e.target.closest(".paid-action");
        if (!target) return;

        const patrolId = target.dataset.id;
        const markAsPaid = target.dataset.status === "notpaid";

        togglePaid(target, patrolId, markAsPaid);
    });
});

function togglePaid(element, id, markAsPaid) {
    const url = `${path}admin/patrol/setpaid/${id}?status=${markAsPaid}`;

    fetch(url, {
        method: 'PUT',
        headers: {
            [csrfHeader]: csrfToken,
            'X-Requested-With': 'XMLHttpRequest'
        }
    })
    .then(response => {
        if (!response.ok) throw new Error('Network response not ok');
        // Update the UI
        element.dataset.status = markAsPaid ? 'paid' : 'notpaid';
        element.innerHTML = markAsPaid ? '<strong>Ja</strong>' : '<strong>Nej</strong>';
    })
    .catch(() => {
        alert("Det gick inte att uppdatera statusen");
    });
}



