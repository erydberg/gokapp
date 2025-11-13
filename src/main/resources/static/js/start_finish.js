const csrfToken = document.querySelector('meta[name="_csrf"]')?.content || "";
const csrfHeader = document.querySelector('meta[name="_csrf_header"]')?.content || "X-CSRF-TOKEN";

const cssPath = '/css/'; // adjust if needed

// Map current status to next status + backend endpoint
const statusMap = {
    REGISTERED: { next: 'ACTIVE', url: '/startfinish/activate/' },
    ACTIVE: { next: 'FINISHED', url: '/startfinish/finished/' },
    FINISHED: { next: 'RESIGNED', url: '/startfinish/resigned/' },
    RESIGNED: { next: 'REGISTERED', url: '/startfinish/registered/' }
};

// Attach event listener after DOM is loaded
document.addEventListener('DOMContentLoaded', () => {
    document.querySelectorAll('.patrolstatus .patrol').forEach(img => {
        img.addEventListener('click', () => handleStatusClick(img));
    });
});

function handleStatusClick(img) {
    const patrolId = img.dataset.id;
    const currentStatus = img.dataset.status;
    const mapping = statusMap[currentStatus];

    if (!mapping) {
        alert('Okänt statusvärde: ' + currentStatus);
        return;
    }

    fetch(mapping.url + patrolId, {
        method: 'PUT',
        headers: {
                    [csrfHeader]: csrfToken,
                    'X-Requested-With': 'XMLHttpRequest'
                }
    })
    .then(response => {
        if (!response.ok) throw new Error('Något gick fel med uppdateringen');
        // update UI
        img.dataset.status = mapping.next;
        img.src = cssPath + mapping.next + '.png';
    })
    .catch(err => {
        alert(err.message);
    });
}
