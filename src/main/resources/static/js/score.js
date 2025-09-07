let currentFilter = '';

function updateDisplay() {
  const el = document.getElementById('current_filter');
  if (el) el.textContent = currentFilter; // guard prevents the error
}

function applyFilter(resetToFirst = true) {
  const select = document.getElementById('patrol');
  if (!select) return;

  const options = Array.from(select.options);

  options.forEach(opt => {
    if (opt.value === '') { // placeholder option
      opt.hidden = false;
      opt.disabled = false;
      return;
    }
    const match = currentFilter === '' || opt.value.toString().startsWith(currentFilter);
    opt.hidden = !match;
    opt.disabled = !match;
  });

  if (resetToFirst) {
    const first = options.find(o => !o.disabled && !o.hidden && o.value !== '');
    if (first) select.value = first.value;
  }
}

function filterPatrol(digit) {
  currentFilter += String(digit);
  updateDisplay();
  applyFilter();
}

function clearFilter() {
  currentFilter = '';
  updateDisplay();
  applyFilter();
}

function backspaceFilter() {
  if (!currentFilter) return;
  currentFilter = currentFilter.slice(0, -1);
  updateDisplay();
  applyFilter();
}

document.addEventListener('DOMContentLoaded', () => {
  updateDisplay();
  applyFilter(false);
});
