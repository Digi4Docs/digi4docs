document.addEventListener("DOMContentLoaded", function (event) {
    new IconPicker('.icon-picker', {
        theme: 'bootstrap-5',
        closeOnSelect: true,
        iconSource: [
            "FontAwesome Solid 6",
            "FontAwesome Regular 6",
            {
                key: "ccicon",
                prefix: "ccicon-",
                url: '/cc-badges.json'
            }
        ],
        i18n: {
            'input:placeholder': 'Icon suchen',
            'text:title': 'Icon auswählen',
            'text:empty': 'Keine Ergebnisse gefunden',
            'btn:save': 'Speichern'
        }
    });

    var pickerButton = document.querySelector('.color-picker');
    const colorInput = document.getElementById('color');
    var picker = new Picker({
        parent: pickerButton,
        color: '' != colorInput.value ? colorInput.value : '#68B022',
        alpha: false,
        editorFormat: 'hex',
        onDone: function (color) {
            const colorInput = document.getElementById('color');
            colorInput.value = color.hex;
        }
    });

    document.querySelector('.color-picker-delete').addEventListener("click", function (event) {
        const colorInput = document.getElementById('color');
        colorInput.value = "";
    });
});