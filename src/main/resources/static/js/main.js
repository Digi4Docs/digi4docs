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
    $('table.datatables tfoot th').each(function () {
        var title = $(this).text();
        if ("" !== title) {
            $(this).html('<input type="text" class="form-control" placeholder="Durchsuchen" />');
        }
    });

    $('table.datatables').DataTable({
        "dom": "<'row'<'col-sm-12 mb-2'tr>>" +
            "<'row'<'col-sm-12 col-md-5'l><'col-sm-12 col-md-7'p>>",
        //stateSave: true,
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
        }
    });

    // ensure forms will not be resubmitted on page reload
    if (window.history.replaceState) {
        window.history.replaceState(null, null, window.location.href);
    }
});