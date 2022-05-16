document.addEventListener("DOMContentLoaded", function (event) {
    tinymce.init({
        selector: 'textarea.wysiwyg-editor',
        height: 400,
        menubar: false,
        plugins: [
            'lists', 'image', 'media', 'table', 'link', 'code', 'iframe'
        ],
        toolbar: 'undo redo | fontsize | ' +
            'bold italic strikethrough forecolor backcolor | ' +
            'alignleft aligncenter alignright alignjustify | bullist numlist | ' +
            'link image media iframe |  code  | removeformat ',
        content_style: 'body { font-family:Helvetica,Arial,sans-serif; font-size:14px }'
    });
});

(function () {
    (function () {
        'use strict';
        tinymce.PluginManager.add('iframe', function (editor, url) {
            var openDialog = function () {
                return editor.windowManager.open({
                    title: 'Webseite einbinden',
                    body: {
                        type: 'panel',
                        items: [
                            {
                                type: 'input',
                                name: 'url',
                                inputMode: 'url',
                                label: 'Externe Webseite',
                                placeholder: 'https://www.example.com'
                            },
                            {
                                type: 'grid',
                                columns: 2,
                                items: [
                                    {
                                        type: 'input',
                                        name: 'width',
                                        inputMode: 'number',
                                        label: 'Breite',
                                        placeholder: '1200'
                                    },
                                    {
                                        type: 'input',
                                        name: 'height',
                                        inputMode: 'number',
                                        label: 'Höhe',
                                        placeholder: '800'
                                    }
                                ]
                            }
                        ]
                    },
                    buttons: [
                        {
                            type: 'cancel',
                            text: 'Abbrechen'
                        },
                        {
                            type: 'submit',
                            text: 'Speichern',
                            primary: true
                        }
                    ],
                    onSubmit: function (api) {
                        var data = api.getData();
                        var width = "" === data.width ? 1200 : data.width;
                        var height = "" === data.height ? 800 : data.height;

                        var htmlCode = '<iframe style="border:0;width: ' + width + 'px; height: ' + height + 'px;" scrolling="no" allowfullscreen="allowfullscreen" src="' + data.url + '"></iframe>';
                        editor.insertContent(htmlCode);
                        api.close();
                    }
                });
            };

            editor.ui.registry.addButton('iframe', {
                tooltip: 'Externe Webseite einbinden',
                icon: 'embed-page',
                onAction: function () {
                    openDialog();
                }
            });

            /* Return the metadata for the help plugin */
            return {
                getMetadata: function () {
                    return {
                        name: 'custom iframe plugin'
                    };
                }
            };
        });
    }());
})();