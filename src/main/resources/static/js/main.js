document.addEventListener("DOMContentLoaded", function (event) {
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
});