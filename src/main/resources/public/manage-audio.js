// audio recorder
let recorder, audio_stream;
const recordButton = document.getElementById("recordButton");
recordButton.addEventListener("click", startRecording);

// stop recording
//const stopButton = document.getElementById("stopButton");
//stopButton.addEventListener("click", stopRecording);
//stopButton.disabled = true;

// set preview
const preview = document.getElementById("audio-playback");

// set download button event
const downloadAudio = document.getElementById("downloadButton");
downloadAudio.addEventListener("click", downloadRecording);

var reader = new FileReader();

function startRecording() {
    // button settings
    recordButton.disabled = true;
    recordButton.innerText = "Recording..."
    $("#recordButton").addClass("button-animate");

   // $("#stopButton").removeClass("inactive");
    //stopButton.disabled = false;


    if (!$("#audio-playback").hasClass("hidden")) {
        $("#audio-playback").addClass("hidden")
    };

    if (!$("#downloadContainer").hasClass("hidden")) {
        $("#downloadContainer").addClass("hidden")
    };

    navigator.mediaDevices.getUserMedia({ audio: true })
        .then(function (stream) {
            audio_stream = stream;
            recorder = new MediaRecorder(stream);
            var base64data;
            // when there is data, compile into object for preview src
            recorder.ondataavailable = function (e) {
                reader.readAsDataURL(e.data); 
                reader.onloadend = function() {
                    base64data = reader.result;                
                    console.log(base64data);
                }
                const url = URL.createObjectURL(e.data);
                preview.src = url;

                // set link href as blob url, replaced instantly if re-recorded
                //downloadAudio.href = url;
            };
            recorder.start();

            timeout_status = setTimeout(function () {
                console.log("5 min timeout");
                stopRecording();
            },6000);
        });
}

function stopRecording() {
    recorder.stop();
    audio_stream.getAudioTracks()[0].stop();

    // buttons reset
    recordButton.disabled = false;
    recordButton.innerText = "Redo Recording"
    $("#recordButton").removeClass("button-animate");

    //$("#stopButton").addClass("inactive");
   // stopButton.disabled = true;

    $("#audio-playback").removeClass("hidden");

    $("#downloadContainer").removeClass("hidden");
}

function downloadRecording(){
    var name = new Date();
    var res = name.toISOString().slice(0,10)
    downloadAudio.download = res + '.wav';
  //  downloadAudio.href = url;
    var xhttp = new XMLHttpRequest();
    xhttp.open('GET', '//http://localhost:8080/blob/uploadFile?id=Muse&type=downloadAudio.download ');
    xhttp.setRequestHeader('Content-Type', 'application/json');
    xhttp.addEventListener('load', onXHRLoad);
    xhttp.send();
}