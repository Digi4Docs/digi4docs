const PublicScripts = {
    adjustTeacherSelect: function (selectedSubject, resetTeacherValue) {
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
        }
    }
};

document.addEventListener("DOMContentLoaded", function (event) {

    // update teacher select box after selecting a subject
    const subjectId = document.getElementById('subjectId');
    const teacherId = document.getElementById('teacherId');
    if (null != subjectId && null != teacherId) {
        subjectId.addEventListener("change", (event) => PublicScripts.adjustTeacherSelect(
            event.currentTarget.value, true));
        PublicScripts.adjustTeacherSelect(subjectId.value, false);
    }

    // init progress circles
    if ("undefined" !== typeof CircularProgressBar) {
        const circle = new CircularProgressBar('pie');
        circle.initial();
    }
});