document.addEventListener("DOMContentLoaded", function (event) {
    // remove form field validation when changing the invalid entry
    let elements = document.querySelectorAll("input, select, textarea");
    for (const element of elements) {
        element.addEventListener("change", (event) => {
            let inputs = document.getElementsByName(event.currentTarget.name);
            for (const input of inputs) {
                if (input.classList.contains("is-invalid")) {
                    input.classList.remove("is-invalid");
                }
            }
        });
    }

    // setup initial dates in forms
    let dateInputsLastYear = document.querySelectorAll("input[type='date'][data-last-year='true']");
    for (const dateInput of dateInputsLastYear) {
        if ("" === dateInput.value) {
            dateInput.valueAsDate = new Date(new Date().setFullYear(new Date().getFullYear() - 1));
        }
    }
    let dateInputsNow = document.querySelectorAll("input[type='date'][data-now='true']");
    for (const dateInput of dateInputsNow) {
        if ("" === dateInput.value) {
            dateInput.valueAsDate = new Date();
        }
    }

    // setup data tables
    if ("undefined" != typeof window.jQuery) {
        try {
            $('table.datatables tfoot th').each(function () {
                var title = $(this).text();
                if ("" !== title) {
                    $(this).html('<input type="text" class="form-control" placeholder="Durchsuchen" />');
                }
            });

            $('table.datatables').DataTable({
                "dom": "<'row'<'col-sm-12 mb-2'tr>>" +
                    "<'row'<'col-sm-12 col-md-5'l><'col-sm-12 col-md-7'p>>",
                stateSave: true,
                language: {
                    paginate: {
                        first: "Erste",
                        last: "Letzte",
                        previous: "Vorherige",
                        next: "Nächste"
                    },
                    "lengthMenu": "Zeige _MENU_ Einträge pro Seite",
                    "zeroRecords": "Keine Einträge gefunden",
                    "info": "Seite _PAGE_ von _PAGES_",
                    "infoEmpty": "Keine Einträge gefunden",
                    "infoFiltered": "(gefiltert von _MAX_ total Gesamteinträgen)"
                },
                initComplete: function () {
                    var tableRow = $($(this).find('tfoot tr'));
                    $($(this).find('thead')).append(tableRow);

                    // Restore existing values
                    var state = this.api().state.loaded();
                    if (state) {
                        this.api().columns().eq(0).each(function (colIdx) {
                            var that = this;
                            var colSearch = state.columns[colIdx].search;

                            if (colSearch.search) {
                                $('input', that.column(colIdx).footer()).val(colSearch.search);
                            }
                        });

                        this.api().draw();
                    }

                    // Apply the search
                    this.api().columns().every(function () {
                        var that = this;

                        $('input', this.footer()).on('keyup change clear', function () {
                            if (that.search() !== this.value) {
                                that
                                    .search(this.value)
                                    .draw();
                            }
                        });
                    });

                    // reset
                    var that = this;
                    $('.datatables-filter-reset').click(function () {
                        that.api().state.clear();
                        window.location.reload();
                    });
                }
            });

        } catch (e) {
            console.log("Can not initiate datatables", e);
        }
    }

    // ensure forms will not be resubmitted on page reload
    if (window.history.replaceState) {
        window.history.replaceState(null, null, window.location.href);
    }

    // add check all functionality to lists
    let toogleCheckboxes = document.getElementById("toogleCheckboxes");
    if (null !== toogleCheckboxes) {
        toogleCheckboxes.addEventListener("click", (event) => {
            let checkboxes = event.currentTarget.closest("table").querySelectorAll("input[type='checkbox']");
            for (const checkbox of checkboxes) {
                checkbox.checked = !checkbox.checked;
            }
        });
    }

    // load template when working on task reviews

    let templateSelect = document.getElementById("textTemplates");
    if (null !== templateSelect) {
        templateSelect.addEventListener("change", (event) => {
            try {
                const text = event.currentTarget.options[event.currentTarget.options.selectedIndex].getAttribute('data-text');
                if (null !== text) {
                    document.getElementById("comment").value = text;
                }
                event.currentTarget.options.selectedIndex = 0;
            } catch (e) {
                console.log(e);
            }
        });
    }

    let templateSelectAdd = document.getElementById("textTemplatesAdd");
    if (null !== templateSelectAdd) {
        templateSelectAdd.addEventListener("change", (event) => {
            try {
                const text = event.currentTarget.options[event.currentTarget.options.selectedIndex].getAttribute('data-text');
                if (null !== text) {
                    document.getElementById("comment").value =  document.getElementById("comment").value + text;
                }
                event.currentTarget.options.selectedIndex = 0;
            } catch (e) {
                console.log(e);
            }
        });
    }

    // warning for unsaved changes in forms
    let unsaved = false;
    const inputs = document.querySelectorAll("form.changeable input:not([type='password']),form.changeable select,form.changeable textarea");
    for (const input of inputs) {
        input.addEventListener("change", () => {
            unsaved = true;
        });
    }

    const submits = document.querySelectorAll("input[type='submit'], button[type='submit']");
    for (const submit of submits) {
        submit.addEventListener("click", () => {
            unsaved = false;
        })
    }

    window.onbeforeunload = function () {
        if (unsaved) {
            return "Achtung: Du hast nicht alle Änderungen gespeichert. Wenn du die Seite verlässt, gehen diese möglicherweise verloren.";
        }
    };

    // add PWA
    if ('serviceWorker' in navigator) {
        navigator.serviceWorker.register('/service-worker.js')
            .then(function (registration) {
                console.log('Successfully registration of service worker, scope is:', registration.scope);
            })
            .catch(function (error) {
                console.log('Service worker registration failed, error:', error);
            });
    }

    // show loading animation on button submit
    $('button[type="submit"].btn-success, button[type="submit"].btn-primary').click(function () {
        $(this).prepend('<span class="spinner-border spinner-border-sm" role="status" aria-hidden="true"></span>');
        $(this).find('i.bi').each(function () {
            $(this).remove();
        });
    })
});

// show and hide badges on home page
function showBadges(classIdentifier) {
    const initiallyHiddenElements = document.querySelectorAll('.' + classIdentifier + '-hidden');
    for (const elem of initiallyHiddenElements) {
        fadeIn(elem);
    }
    const hideIcon = document.querySelector('.' + classIdentifier + '-hide')
    if (hideIcon.classList.contains('d-none')) {
        hideIcon.classList.remove('d-none');
    }

    const initiallyShownElements = document.querySelectorAll('.' + classIdentifier + '-shown');
    for (const elem of initiallyShownElements) {
        fadeOut(elem);
    }
    const showIcon = document.querySelector('.' + classIdentifier + '-show')
    if (!showIcon.classList.contains('d-none')) {
        showIcon.classList.add('d-none');
    }
}

function hideBadges(classIdentifier) {
    const initiallyHiddenElements = document.querySelectorAll('.' + classIdentifier + '-hidden');
    for (const elem of initiallyHiddenElements) {
        fadeOut(elem);
    }
    const hideIcon = document.querySelector('.' + classIdentifier + '-hide')
    if (!hideIcon.classList.contains('d-none')) {
        hideIcon.classList.add('d-none');
    }

    const initiallyShownElements = document.querySelectorAll('.' + classIdentifier + '-shown');
    for (const elem of initiallyShownElements) {
        fadeIn(elem);
    }
    const showIcon = document.querySelector('.' + classIdentifier + '-show')
    if (showIcon.classList.contains('d-none')) {
        showIcon.classList.remove('d-none');
    }
}

function fadeOut(element) {
    element.style.opacity = 1;
    (function fade() {
        if ((element.style.opacity -= .1) < 0) {
            if (!element.classList.contains('d-none')) {
                element.classList.add('d-none');
            }
        } else {
            requestAnimationFrame(fade);
        }
    })();
};

function fadeIn(element) {
    element.style.opacity = 0;
    element.style.display = "block";
    if (element.classList.contains('d-none')) {
        element.classList.remove('d-none');
    }

    (function fade() {
        var val = parseFloat(element.style.opacity);
        if (!((val += .1) > 1)) {
            element.style.opacity = val;
            requestAnimationFrame(fade);
        }
    })();
};