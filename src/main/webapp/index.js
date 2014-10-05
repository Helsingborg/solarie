var queryInputText;
var searchButton;

var selectedFacets = [];

var instances = {};
function getInstance(identity) {
  return instances[identity.toString()];
}
function setInstance(instance) {
  instances[instance.identity.toString()] = instance;
}


$(function onLoad() {

  $('#query').focus();

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


});


function search() {

  var searchTimer = new Date().getTime();

  var request = {
    sortOrder: 'score',
    explain: false,
    offset: 0,
    length: 10,
    query: {
      type: 'boolean',
      clauses: [
      ]
    }

  };

  var query;

  if (selectedFacets.length > 0) {
    for (var i = 0; i < selectedFacets.length; i++) {
      request.query.clauses.push({
        occur: 'must',
        query: selectedFacets[i].query
      });
    }
  }

  var textQuery = $('#query').val().trim();
  if ("" === textQuery) {
    request.query.clauses.push({
      occur: 'should',
      query: {
        type: 'match all documents'
      }
    });
  } else {
    request.query.clauses.push({
      occur: 'should',
      query: {
        type: 'boolean analyzed text should',
        field: 'text',
        value: textQuery
      }
    });
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

        $('#facets').empty();
        for (var facetIndex = 0; facetIndex < response.facets.length; facetIndex++) {
          var facet = response.facets[facetIndex];

          var html = "<div>";
          html += "<span class='facet_name link'>" + facet.name + "</span>&nbsp;(" + facet.matches + ")";
          html += "<div style='padding-bottom: 1.5em; display: none;'>";
          for (var facetValueIndex = 0; facetValueIndex < facet.values.length; facetValueIndex++) {
            var facetValue = facet.values[facetValueIndex];
            html += "<div style='padding-left: 1em;'>";
            html += facetValue.name;
            html += "&nbsp;(";
            html += facetValue.matches;
            html += ")</div>";
          }
          html += "</div>";

          appendFacetElement(html, facet);

        }

        // set instances
        instances = {};
        for (var index = 0; index < response.instances.length; index++) {
          setInstance(response.instances[index]);
        }
        for (var identity in instances) {
          var instance = instances[identity];
          if (typeof instance.ärende === 'number') {
            instance.ärende = getInstance(instance.ärende);
          }
          if (typeof instance.åtgärd === 'number') {
            instance.åtgärd = getInstance(instance.åtgärd);
          }
        }



        for (var index = 0; index < response.items.length; index++) {

          // instance verkar inte sättas i item?

          response.items[index].instance = getInstance(response.items[index].instance);

          var item = response.items[index];

          var html = "<div class='search_result' id='search_result_" + item.index + "'>";

          html += "<div>";
          html += "<span class='search_result_diarienummer'>" + item.instance.diarienummer + "</span>";

          var typeText;
          if (item.type === "Arende") {
            typeText = "Ärende";
            html += "<span class='search_result_type'>" + typeText + "</span>";
          } else if (item.type === "Atgard") {
            typeText = "Åtgärd";
            html += "<span class='search_result_type'>" + typeText + "</span>";
            html += "<span class='search_result_atgard_arende'>Del av ärendet <span style='font-style: italic'>" + item.instance.ärende.mening + "</span></span>";
          } else if (item.type === "Dokument") {
            typeText = "Dokument";
            html += "<span class='search_result_type'>" + typeText + "</span>";
          } else {
            typeText = item.type;
            html += "<span class='search_result_type'>" + typeText + "</span>";
          }


          html += "</div>";


          html += "<div class='search_result_title'>";
          if (item.type === "Arende") {
            html += item.instance.mening;
          } else if (item.type === "Atgard") {
            html += item.instance.text;
          } else if (item.type === "Dokument") {
            html += item.instance.text;
          } else {
            html += typeText + " utan titel";
          }
          html += "</div>";


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

function appendFacetElement(html, facet) {
  var facetElement = $(html);
  facetElement.get(0).facet = facet;

  var facetNameElement = facetElement.find('span');
  var valuesElement = facetElement.find('div');

  facetElement.mouseout(function () {
    facetElement.css('cursor', 'default');
  });
  facetElement.mouseover(function () {
    facetElement.css('cursor', 'pointer');
  });

  facetNameElement.click(function () {
    if (valuesElement.is(":visible")) {
      facetNameElement.removeClass("visited_link");
      facetNameElement.addClass("link");
      valuesElement.hide(100);
    } else {
      facetNameElement.removeClass("link");
      facetNameElement.addClass("visited_link");
      valuesElement.show(0);
    }
  });

  valuesElement.children().each(function (index, element) {
    $(element).click(function () {
      var facet = facetElement.get(0).facet;
      var facetValue = facet.values[index]
      selectedFacets.push(facetValue);
      var activeFacet = $('<span style="padding-left: 2em;"/>');
      activeFacet.addClass('active_facet');
      activeFacet.addClass('link');
      activeFacet.text(facet.name + "/" + facetValue.name);
      activeFacet.appendTo($('#active_facets'));
      search();
    });
  });

  facetElement.appendTo($("#facets"));
  return facetElement;
}




