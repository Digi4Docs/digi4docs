document.addEventListener("DOMContentLoaded", function (event) {
    new IconPicker('.icon-picker', {
        theme: 'bootstrap-5',
        closeOnSelect: true,
        iconSource: ["FontAwesome Solid 6", "FontAwesome Regular 6"],
    });

    var pickerButton = document.querySelector('.color-picker');
    var picker = new Picker(pickerButton);
    picker.onChange = function(color) {
        const colorInput = document.getElementById('color');
        colorInput.value = color.hex;
    };

    document.querySelector('.color-picker-delete').addEventListener("click", function (event) {
        const colorInput = document.getElementById('color');
        colorInput.value = "";
    });
});