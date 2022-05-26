const PublicScripts = {
    adjustTeacherSelect: function (subjectId, teacherId, selectedSubject, resetTeacherValue) {
        // reset value
        if (resetTeacherValue) {
            teacherId.value = "";
        }

        Array.from(teacherId.options).forEach(function (optionElement) {
            if (optionElement.classList.contains("d-none")) {
                optionElement.classList.remove("d-none");
            }
        });

        if ("" !== selectedSubject) {
            teacherId.disabled = subjectId.disabled || false;
            Array.from(teacherId.options).forEach(function (optionElement) {
                selectedSubject = parseInt(selectedSubject);
                const teacherSubjectsString = optionElement.dataset.subjects;
                if ("undefined" !== typeof teacherSubjectsString) {
                    const teacherSubjects = JSON.parse(teacherSubjectsString);
                    if (!teacherSubjects.includes(selectedSubject)) {
                        optionElement.classList.add("d-none");
                    }
                }
            });
        } else {
            teacherId.disabled = true;
        }
    },

    print: function () {
        this.setupEmptyTask();
        this.setupPageBreaks();
        this.setupTitleImage();
        this.setupTaskImages();
        this.setupLastPage();
        window.print();
    },

    setupEmptyTask: function () {
        this.togglePrintClass('.print-empty-task', !document.getElementById('cbExcludeOpen').checked, 'd-print-none');
    },


    setupPageBreaks: function () {
        this.togglePrintClass('.print-page-break', !document.getElementById('cbPageBreaks').checked, 'page-break-before');
        this.togglePrintClass('.print-first-page-break', !document.getElementById('cbFirstPageBreak').checked, 'page-break-before');
        this.togglePrintClass('.print-last-page-break', !document.getElementById('cbLastPageBreak').checked, 'page-break-before');
    },

    setupLastPage: function () {
        this.togglePrintClass('.print-last-page', !document.getElementById('cbNoLastPage').checked, 'd-print-none');
    },

    setupTaskImages: function () {
        this.togglePrintClass('.print-image', document.getElementById('cbImages').checked, 'd-print-none');
    },

    setupTitleImage: function () {
        this.togglePrintClass('.print-title-image', document.getElementById('cbTitleImage').checked, 'd-print-none');
    },

    togglePrintClass(querySelector, enablePrint, cssClass) {
        let elements = document.querySelectorAll('.certificate ' + querySelector);
        for (const element of elements) {
            if (enablePrint) {
                if (element.classList.contains(cssClass)) {
                    element.classList.remove(cssClass);
                }
            } else {
                if (!element.classList.contains(cssClass)) {
                    element.classList.add(cssClass);
                }
            }
        }
    }
};

document.addEventListener("DOMContentLoaded", function (event) {

    // update teacher select box after selecting a subject
    const subjectId = document.getElementById('subjectId');
    const teacherId = document.getElementById('teacherId');
    if (null != subjectId && null != teacherId) {
        subjectId.addEventListener("change", (event) => PublicScripts.adjustTeacherSelect(
            subjectId, teacherId, event.currentTarget.value, true));
        PublicScripts.adjustTeacherSelect(subjectId, teacherId, subjectId.value, false);
    }

    // init progress circles
    if ("undefined" !== typeof CircularProgressBar) {
        const circle = new CircularProgressBar('pie');
        circle.initial();
    }
});