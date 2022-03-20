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

    // setup data tables
    let tables = document.querySelectorAll("table.simple-datatables");
    for(const table of tables) {
         new simpleDatatables.DataTable(table, {
             perPageSelect: [5, 10, 25, 50, 100],
             labels: {
                 placeholder: "Suchen...",
                 perPage: "{select} Einträge pro Seite",
                 noRows: "Keine Einträge gefunden",
                 info: "Zeige {start} bis {end} von {rows} Einträgen",
             }
         });
    }

    // ensure forms will not be resubmitted on page reload
    if ( window.history.replaceState ) {
        window.history.replaceState( null, null, window.location.href );
    }
});