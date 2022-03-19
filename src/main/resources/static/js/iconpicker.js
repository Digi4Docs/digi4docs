document.addEventListener("DOMContentLoaded", function (event) {
    new IconPicker('.icon-picker', {
        theme: 'bootstrap-5',
        closeOnSelect: true,
        iconSource: ["FontAwesome Solid 6", "FontAwesome Regular 6"],
    });
});