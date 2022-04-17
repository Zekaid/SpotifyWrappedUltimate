function start(){
    window.location.pathname = "/login"
    document.getElementById("log").style.display = "none"
    document.getElementById("title").style.display = "none"
}

function out(){
    window.location.pathname = "/logout"
}

function run(){
    document.getElementById("btn1").style.display = "none"
    document.getElementById("btn2").style.display = "inline"
    document.getElementById("btn3").style.display = "none"
    fetch("/api/top/tracks").then(r => r.json()).
    then(data => append(data))
}

function run2(){
    document.getElementById("btn1").style.display = "none"
    document.getElementById("btn2").style.display = "inline"
    document.getElementById("btn3").style.display = "none"
    fetch("/api/top/artists").then(r => r.json()).
    then(data => append2(data))
}

function hide(){
    document.getElementById("demo").innerHTML = ""
    document.getElementById("btn2").style.display = "none"
    document.getElementById("btn1").style.display = "inline"
    document.getElementById("btn3").style.display = "inline"
}

function append (data){
    let mainContainer = document.getElementById("demo");
    for (let i = 0; i < data.length; i++) {
        let div = document.createElement("div");
        div.innerHTML = data[i].artist + ": " + data[i].track;
        mainContainer.appendChild(div);
    }
}

function append2 (data){
    let mainContainer = document.getElementById("demo");
    for (let i = 0; i < data.length; i++) {
        let div = document.createElement("div");
        div.innerHTML = data[i].artist;
        mainContainer.appendChild(div);
    }
}