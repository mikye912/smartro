var ctrn_width = "40px";
var obj = document.getElementById("search_box").offsetWidth; 
var setw = obj-58 + "px";
document.getElementById("temp").value=setw;
document.getElementById("search_box_title").style.width = setw;
document.getElementById("search_box_control").style.width = ctrn_width;