var queryInputText;
var searchButton;

function initialize() {

  searchButton = document.getElementById("search");
  queryInputText = document.getElementById("query");




  queryInputText.onkeypress = function (e) {
    var key = e.which || e.keyCode;
    if (key == 13) {
      search({text: queryInputText.value});
    }
  };


  searchButton.onclick = function() {
    search({text: queryInputText.value});
  };


}


function search(query) {

  console.log("[search] " + JSON.stringify(query));

}