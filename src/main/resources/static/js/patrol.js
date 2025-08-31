document.addEventListener("DOMContentLoaded", function() {
    // Attach click event for all .paid-action elements inside .switchstatus container
    document.querySelectorAll('.switchstatus .paid-action').forEach(function(element) {
        element.addEventListener("click", function() {
            const status = this.dataset.status;
            const patrolId = this.dataset.id;

            if (status === "paid") {
                changeToNotPaid(this, patrolId);
            } else if (status === "notpaid") {
                changeToPaid(this, patrolId);
            }
        });
    });
});

function changeToPaid(element, id) {
    fetch(`/admin/patrol/setpaid/${id}`, {
        method: 'PUT'
    })
    .then(response => {
        if (!response.ok) throw new Error("Network response was not ok");
        element.dataset.status = 'paid';
        element.innerHTML = '<strong>Ja</strong>';
    })
    .catch(error => {
        alert("Det gick inte att uppdatera statusen");
        console.error(error);
    });
}

function changeToNotPaid(element, id) {
    fetch(`/admin/patrol/setnotpaid/${id}`, {
        method: 'PUT'
    })
    .then(response => {
        if (!response.ok) throw new Error("Network response was not ok");
        element.dataset.status = 'notpaid';
        element.innerHTML = '<strong>Nej</strong>';
    })
    .catch(error => {
        alert("Det gick inte att uppdatera statusen");
        console.error(error);
    });
}
