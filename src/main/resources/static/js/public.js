document.addEventListener("DOMContentLoaded", function (event) {
    const subjectId = document.getElementById('subjectId');
    const teacherId = document.getElementById('teacherId');
    if (null != subjectId && null != teacherId) {
        subjectId.addEventListener("change", (event) => {
            let selectedSubject = event.currentTarget.value;

            // reset value
            teacherId.value = "";

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

        });
    }
});