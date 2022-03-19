document.addEventListener("DOMContentLoaded", function (event) {
    tinymce.init({
        selector: 'textarea.wysiwyg-editor',
        height: 400,
        menubar: false,
        plugins: [
            'lists', 'image', 'media', 'table', 'link'
        ],
        toolbar: 'undo redo | fontsize | ' +
            'bold italic strikethrough forecolor backcolor | ' +
            'alignleft aligncenter alignright alignjustify | bullist numlist | ' +
            'link image media | removeformat ',
        content_style: 'body { font-family:Helvetica,Arial,sans-serif; font-size:14px }'
    });
});