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


  searchButton.onclick = function () {
     search();
  };

  function search(){
    S_search({
          sortOrder: 'score',
          explain: false,
          offset: 0,
          length: 10,
          query: {
            type: 'boolean analyzed text should',
            field: 'text',
            value: $('#query').val()
          }
        },
        function success(response){

          $('#search_results').html("");

          $('#searchResults_length').text(response.length);

          var sekunder = (response.timers.total/1000).toString();
          sekunder = sekunder.substring(0, Math.min(sekunder.length, 5));
          $('#searchResults_timers_total').text(sekunder);

          for (var index = 0; index < response.items.length; index++) {
            var item = response.items[index];

            var html = "<div id='search_result_" + item.index + "'>";


            if (item.type === "Arende") {
              html += item.instance.mening;

            } else if (item.type === "Atgard") {
              html += item.instance.text;

            } else if (item.type === "Dokument") {
              html += item.instance.text;

            } else {
              html += "Ingen titel";
            }


            if (item.explaination !== undefined) {
              html += "<br/>";
              html += item.explanation;
            }

            html += "</div>";

            html += "<div style='height: 10;'></div>";

            $(html).appendTo('#search_results');
          }
        });

  }
}


