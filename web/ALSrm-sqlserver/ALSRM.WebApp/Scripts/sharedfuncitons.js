var API_URI = "http://localhost:15113";

var counter = 0;

//window.addEventListener("load", initMessages);
var path = window.location.pathname;
var view = path.split("/").pop();


// For language
$(function() {

    $(".dropdown-menu")
        .on("click",
            "li a",
            function() {
                if (this.id === "langElem") {
                    $("#languageBtn:first-child").text($(this).text());
                    $("#languageBtn:first-child").val($(this).text());
                }
            });
});

function changeLanguage(abb) {
    setCookie("Language", abb, 1);
    location.reload();
}

$(document)
    .ready(function(e) {
        $("#countries").msDropdown();
    });

// Translate DataTable
$(document)
    .ready(function() {
        if (getCookie("Language") === "pt") {
            $("#examtable")
                .DataTable({
                    "language": {
                        "url": "/Content/dataTable.Portuguese.lang.json"
                        //"url": "https://cdn.datatables.net/plug-ins/1.10.12/i18n/Portuguese.json"
                    }
                });
        }
    });

$(document)
    .ready(function() {
        $("#examtable").DataTable();
    });

/* For message success or inssucess
function initMessages() {
    var msg;
    if ((msg = getCookie("successmsg")) !== "") {
        var sm = document.getElementById("successmsg");
        sm.style.display = "inline-block";
        sm.innerHTML = msg;
        deleteCookie("successmsg");
    } else {
        if ((msg = getCookie("dangermsg")) !== "") {
            var dm = document.getElementById("dangermsg");
            dm.style.display = "inline-block";
            dm.innerHTML = msg;
            deleteCookie("dangermsg");
        }
    }
};*/
//function to disable muscle for exam ecg
function changeExamType(val) {
    if (val === "ECG") {
        document.getElementById("musclediv").style.display = "none";
    } else {
        document.getElementById("musclediv").style.display = "inline";
    }
}

// For Create Exam
function cloneRow(tableId, rowToCloneId) {
    counter++;
    var table = document.getElementById(tableId);
    var row = document.getElementById(rowToCloneId);
    var clone = row.cloneNode(true);
    clone.id = rowToCloneId.replace("0", counter);
    for (var i = 0; i < clone.childNodes.length; i++) {
        var c;
        if ((c = clone.childNodes[i]).id === "removebtn-0") {
            c.id = "removebtn-" + counter;
            c.style.display = "table-cell";
        }
    }
    table.appendChild(clone);
}

function deleteRow(rowToDeleteId) {
    counter--;
    document.getElementById(rowToDeleteId).remove();
}

function createExam() {
    var exam = {
        ExamType: "ECG",
        ExamState: "pending",
        ExamInitialDate: "",
        ExamEndDate: "",
        MuscleAbb: "",
        UserId: "",
        ExamSteps: new Array()
    };
    var timestamp;
    var isSafari = Object.prototype.toString.call(window.HTMLElement).indexOf('Constructor') > 0;
    var e = document.getElementById("ExamType");
    exam.ExamType = e.options[e.selectedIndex].value;

    e = document.getElementById("ExamState");
    exam.ExamState = e.options[e.selectedIndex].value;
    
    e = document.getElementById("ExamInitialDate");
    if (!isSafari) {
        timestamp = Date.parse(e.value);
        if (isNaN(timestamp)) {
            if (getCookie("Language") === "pt")
                alert("Data Inicial incorreta, tente novamente.");
            else
                alert("Initial Date incorrect, try again");
            return;
        }
    }
    exam.ExamInitialDate = e.value;

    e = document.getElementById("ExamEndDate");
    if (!isSafari) {
        timestamp = Date.parse(e.value);
        if (isNaN(timestamp)) {
            if (getCookie("Language") === "pt")
                alert("Data Final incorreta, tente novamente.");
            else
                alert("End Date incorrect, try again");
            return;
        }
    }
    exam.ExamEndDate = e.value;

    if (exam.ExamType === "ECG")
        exam.MuscleAbb = null;
    else {
        e = document.getElementById("MuscleAbb");
        exam.MuscleAbb = e.options[e.selectedIndex].value;
    }

    e = document.getElementById("UserId");
    exam.UserId = e.options[e.selectedIndex].value;


    e = document.getElementById("examsteptable");
    var counter = 0;
    // counter to ensure that examSteps number is correct
    for (var i = 0; i < e.childNodes.length; i++) {
        if (typeof e.childNodes[i].id !== "undefined")
            counter++;
    }

    for (var i = 0; i < counter; i++) {

        var examStep = {};
        e = document.getElementById("examsteprow-" + i).childNodes[1].childNodes[0];
        examStep.Description = e.options[e.selectedIndex].value;
        e = document.getElementById("examsteprow-" + i).childNodes[3].childNodes[0];
        examStep.State = e.options[e.selectedIndex].value;
        e = document.getElementById("examsteprow-" + i).childNodes[5].childNodes[0];
        if (isNaN(e.value) || e.value <= 0) {
            if (getCookie("Language") === "pt")
                alert("Tempo incorreto, tente novamente.");
            else
                alert("Time incorrect, try again.");
            return;
        }
        examStep.Time = e.value;
        exam.ExamSteps.push(examStep);
    }

    //console.log(exam);
    //console.log(JSON.stringify(exam));

    var xhttp = new XMLHttpRequest();


    xhttp.onreadystatechange = function() {
        if (xhttp.readyState === 4 && xhttp.status === 200) {
            if (xhttp.responseText <= 0) {
                if (getCookie("Language") === "pt")
                    alert("Ocorreu um erro, tente novamente.");
                else
                    alert("An error occurrs, try again.");
            } else {
                var successMessage;
                if (getCookie("Language") === "pt")
                    alert("O exame foi criado com sucesso!");
                else
                    alert("The exam was successfully created!");

                window.location.assign("/Exams/" + xhttp.responseText + "/Details");

            }
        }

    };
    xhttp.open("POST", "/Exams/Create");
    xhttp.setRequestHeader("Content-type", "application/json");
    xhttp.send(JSON.stringify(exam));

}

function setCookie(cname, cvalue, exdays) {
    var d = new Date();
    d.setTime(d.getTime() + (exdays * 24 * 60 * 60 * 1000));
    var expires = "expires=" + d.toUTCString();
    document.cookie = cname + "=" + cvalue + "; " + expires;
}

function getCookie(cname) {
    var name = cname + "=";
    var ca = document.cookie.split(";");
    for (var i = 0; i < ca.length; i++) {
        var c = ca[i];
        while (c.charAt(0) === " ") {
            c = c.substring(1);
        }
        if (c.indexOf(name) === 0) {
            return c.substring(name.length, c.length);
        }
    }
    return "";
}

function deleteCookie(cname) {
    document.cookie = cname + "=;expires=Wed; 01 Jan 1970";
}

function cancelExam(examId) {

    var xhttp = new XMLHttpRequest();


    xhttp.onreadystatechange = function() {
        if (xhttp.readyState === 4 && xhttp.status === 200) {
            if (xhttp.responseText <= 0) {
                if (getCookie("Language") === "pt")
                    alert("Ocorreu um erro, tente novamente.");
                else
                    alert("An error occurrs, try again.");
            } else {
                if (getCookie("Language") === "pt")
                    alert("O exame foi cancelado com sucesso!");
                else
                    alert("The exam was successfully cancelled!");
                window.location.reload();

            }
        }

    };
    xhttp.open("POST", "/Exams/" + examId + "/Cancel");
    xhttp.setRequestHeader("Content-type", "application/json");
    xhttp.send();
}

function cancelExamStep(examId, stepnum) {

    var xhttp = new XMLHttpRequest();


    xhttp.onreadystatechange = function() {
        if (xhttp.readyState === 4 && xhttp.status === 200) {
            if (xhttp.responseText <= 0) {
                if (getCookie("Language") === "pt")
                    alert("Ocorreu um erro, tente novamente.");
                else
                    alert("An error occurrs, try again.");
            } else {
                if (getCookie("Language") === "pt")
                    alert("A etapa do exame foi cancelada com sucesso!");
                else
                    alert("The exam step was successfully cancelled!");
                window.location.reload();

            }
        }

    };
    xhttp.open("POST", "/Exams/" + examId + "/ExamSteps/" + stepnum + "/Cancel");
    xhttp.setRequestHeader("Content-type", "application/json");
    xhttp.send();
}