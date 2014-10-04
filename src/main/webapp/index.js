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

  function search() {

    var searchTimer = new Date().getTime();

    var request = {
      sortOrder: 'score',
      explain: false,
      offset: 0,
      length: 10
    }

    var textQuery = $('#query').val().trim();
    if ("" === textQuery) {
      request.query = {
        type: 'match all documents'
      };
    } else {
      request.query = {
        type: 'boolean analyzed text should',
        field: 'text',
        value: textQuery
      };
    }


    S_search(
        request,
        function success(response) {


          $('#search_results').html("");

          $('#reponse_length').text(response.length);

          var sekunder = (response.timers.total / 1000).toString();
          sekunder = sekunder.substring(0, Math.min(sekunder.length, 5));
          sekunder = sekunder.replace(".", ",");
          $('#response_timers_total').text(sekunder);

          $('#response_information').css('display', 'block');

          for (var facetIndex = 0; facetIndex < response.facets.length; facetIndex++) {
            var facet = response.facets[facetIndex];


            var html = "<div style='padding-bottom: 1em;'";
            html +=" onmouseover='this.getElementsByTagName(\"div\")[0].style.display=\"block\";'";
            html +=" onmouseout='this.getElementsByTagName(\"div\")[0].style.display=\"none\";'";
            html+= ">";
            html += "<strong>" + facet.name + "</strong>&nbsp;(" + facet.matches + ")";
            html += "<div style='display: none;'>";
            for (var facetValueIndex = 0; facetValueIndex < facet.values.length; facetValueIndex++) {

              var facetValue = facet.values[facetValueIndex];

              html += "<div style='padding-left: 1em;'>";
              html += facetValue.name;
              html += "&nbsp;(";
              html += facetValue.matches;
              html += ")</div>";

            }

//            var html = "<div>";
//            html += "<strong>" + facet.name + "</strong>&nbsp;(" + facet.matches + ")";
//            html += "<div id='facet_values_"+ facetIndex +"' style='display: none;'>";
//            for (var facetValueIndex = 0; facetValueIndex < facet.values.length; facetValueIndex++) {
//
//              var facetValue = facet.values[facetValueIndex];
//
//              html += "<div>";
//              html += facetValue.name;
//              html += "&nbsp;(";
//              html += facetValue.matches;
//              html += ")</div>";
//
//            }


            html += "</div>";


//            facetElement.mouseout(function () {
//              console.log("mouseout");
//              facetElement.find('#facet_values_' + facetIndex).css("display", "none");
//            });
//            facetElement.mouseover(function () {
//              console.log("mouseover");
//              facetElement.find('#facet_values_' + facetIndex).css("display", "block");
//            });


            var facetElement = $(html);

            facetElement.appendTo($("#facets"));
          }

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

          var sekunder = ((new Date().getTime() - searchTimer) / 1000).toString();
          sekunder = sekunder.substring(0, Math.min(sekunder.length, 5));
          sekunder = sekunder.replace(".", ",");
          $('#search_timer').text(sekunder);


        });

  }
}


