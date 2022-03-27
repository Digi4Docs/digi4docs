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

    printWithoutImages: function () {
        let elements = document.querySelectorAll('.certificate .print-on-demand');
        for (const element of elements) {
            if(!element.classList.contains("d-print-none")) {
                element.classList.add("d-print-none");
            }
        }

        window.print();
    },

    printWithImages: function () {
        let elements = document.querySelectorAll('.certificate .print-on-demand');
        for (const element of elements) {
            if(element.classList.contains("d-print-none")) {
                element.classList.remove("d-print-none");
            }
        }

        window.print();

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