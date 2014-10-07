var queryInputText;
var searchButton;

var selectedFacets = [];

var instances = {};
function getInstance(identity) {
  if (!identity) {
    console.log("null!");
  }
  return instances[identity.toString()];
}
function setInstance(instance) {
  instances[instance.identity.toString()] = instance;
}


$(function onLoad() {




  var queryInput = $('#query');
  queryInput.watermark("Skriv din fråga och tryck på enter!", "watermark2");
  queryInput.focus();

  searchButton = document.getElementById("search");
  queryInputText = document.getElementById("query");


  queryInputText.onkeypress = function (e) {
    var key = e.which || e.keyCode;
    if (key == 13 || key == 32) {
      search({text: queryInputText.value});
    }
  };


});


function search() {

  $('#response_length').text('Söker…');
  $('#search_results').empty();

  var searchTimer = new Date().getTime();

  var request = {
    reference: {
      timestamp: new Date().getTime()
    },
    sortOrder: 'score',
    explain: false,
    offset: 0,
    length: 20,
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

    if (selectedFacets.length === 0) {
      request.cached = "empty";
    }

    request.sortOrder = 'timestamp';
    request.query.clauses.push({
      occur: 'should',
      query: {
        type: 'match all documents'
      }
    });

  } else {
    request.query.clauses.push({
      occur: 'must',
      query: {
        type: 'boolean analyzed text should',
        fields: ['text', 'text singularform', 'text_särskivning'],
        value: textQuery
      }
    });
  }


  S_search(
      request,
      function success(response) {

        var actualTimeSpent = new Date().getTime() - response.reference;

        var sekunder = (response.timers.total / 1000).toString();
        sekunder = sekunder.substring(0, Math.min(sekunder.length, 5));
        sekunder = sekunder.replace(".", ",");

        var searchResultsDiv = $('#search_results');

        $('#facets').empty();
        searchResultsDiv.empty();


        if (response.success === false) {
          var div = $("div");
          div.text("PC LOAD LETTER");
          div.appendTo(searchResultsDiv);
          return;
        }

        $('#response_length').text(response.length + ' träffar på ' + sekunder + ' sekunder');

        if (response.length === 0) {
          var div = $("div");
          div.text("Hittade inget!</POC>");
          div.appendTo(searchResultsDiv);
          return;
        }


        for (var facetIndex = 0; facetIndex < response.facets.length; facetIndex++) {
          var facet = response.facets[facetIndex];

          var html = "<div class='facet'>";

          html += "<span class='facet_name link'>" + facet.name + "</span> " + facet.values.length + "st|" + formatPercent(facet.matches, response.length, false);
          html += "<div class='facet_values' style='padding-bottom: 1.5em; display: none;'>";
          for (var facetValueIndex = 0; facetValueIndex < facet.values.length; facetValueIndex++) {
            var facetValue = facet.values[facetValueIndex];
            html += "<div class='facet_value' style='padding-left: 1em; padding-bottom: 0.25em;'>"
                + "<span class='facet_value_percent'>" + formatPercent(facetValue.matches, response.length, true) + "</span>"
                + "<span class='facet_value_name link'>" + facetValue.name + "</span>"
                + "</div>";
          }
          html += "</div>"; // values

          html += "</div>"; // facet

          appendFacetElement(html, facet);
        }

        // set instances
        instances = {};
        for (var index = 0; index < response.instances.length; index++) {
          setInstance(response.instances[index]);
        }
        // couple instances
        for (var identity in instances) {
          var instance = instances[identity];
          if (typeof instance.ärende === 'number') {
            instance.ärende = getInstance(instance.ärende);
          }
          if (typeof instance.åtgärd === 'number') {
            instance.åtgärd = getInstance(instance.åtgärd);
          }
          if (typeof instance.diarium === 'number') {
            instance.diarium = getInstance(instance.diarium);
          }
          if (typeof instance.enhet === 'number') {
            instance.enhet = getInstance(instance.enhet);
          }
        }


        function renderGroups() {
          for (var index = 0; index < response.groups.length; index++) {

            var group = response.groups[index];
            group.root = getInstance(group.root);

            for (var itemIndex = 0; itemIndex < group.items.length; itemIndex++) {
              group.items[itemIndex].instance = getInstance(group.items[itemIndex].instance);

            }


            var groupElement = $("<div class='group_result'></div>");
            groupElement.appendTo(searchResultsDiv);

            var html = "<div class='group_first_item'>";

            html += "<div>";
            html += "<span class='diarienummer'>" + group.items[0].instance.diarienummer + "</span>";
            html += "<span class='padding'></span>";
            html += "<span class='timestamp'>" + (group.items[0].timestamp ? $.format.date(group.items[0].timestamp, 'yyyy-MM-dd') : "????-??-??") + "</span>";
            html += "<span class='padding'></span>";
            html += "<span class='diarium'>" + group.items[0].instance.diarium.namn + "</span>";
            if (group.items[0].instance.enhet) {
              html += "/<span class='group_first_item_enhet'>" + group.items[0].instance.enhet.namn + "</span>";
            }
            html += "</div>";

            html += "<div>";

            html += "</div>";

            $(html).appendTo(groupElement);

            var rowDiv = $('<div/>');
            rowDiv.appendTo(groupElement);

            $("<span class='group_first_item_type'>" + getTypeText(group.items[0]) + ":&nbsp;</span>").appendTo(rowDiv);
            var groupTitle = $("<span class='title group_first_item_title link'>");
            makeLink(groupTitle);
            groupTitle.appendTo(rowDiv);
            if (group.items[0].type === "Arende") {
              groupTitle.text(group.items[0].instance.mening);
            } else if (group.items[0].type === "Atgard") {
              groupTitle.text(group.items[0].instance.text);
            } else if (group.items[0].type === "Dokument") {
              groupTitle.text(group.items[0].instance.text);
            } else {
              groupTitle.text(getTypeText(group.items[0]) + " utan titel.");
            }

            var ignoredInstances = [];

            if (group.items[0].type === "Arende") {
              ignoredInstances.push(group.items[0].instance);
            }


            //
            //
            // group items

            var groupItemsElement = $('<div class="group_items"/>');
            groupItemsElement.appendTo(groupElement);

            for (var itemIndex = 1; itemIndex < group.items.length; itemIndex++) {
              var item = group.items[itemIndex];
              if ($.inArray(item.instance, ignoredInstances) == -1) {

                var groupItemElement = $('<div class="group_item"/>');
                groupItemElement.appendTo(groupItemsElement);

//                html += "<span class='score'>" + item.normalizedScore + "</span>";
                $("<span class='timestamp'>" + (item.timestamp ? $.format.date(item.timestamp, 'yyyy-MM-dd') : "????-??-??") + "</span>").appendTo(groupItemElement);
                $("<span class='padding'></span>").appendTo(groupItemElement);
                $("<span class='indexable_type'>").appendTo(groupItemElement);
                var groupItemType = $("<span class='facet_value_name link'/>");
                groupItemType.appendTo(groupItemElement);
                groupItemType.text(getTypeText(item));
                $("<span>:&nbsp;</span>").appendTo(groupItemElement);
                makeLink(groupItemType);

                var groupItemTitle = $("<span class='title'/>");
                groupItemTitle.appendTo(groupItemElement);

                if (item.type === "Arende") {
                  groupItemTitle.text(item.instance.mening);
                } else if (item.type === "Atgard") {
                  groupItemTitle.text(item.instance.text);
                } else if (item.type === "Dokument") {
                  groupItemTitle.text(item.instance.text);
                } else {
                  groupItemTitle.text(getTypeText(item) + " utan titel");
                }
              }


            }
          }

        }

        renderGroups();

        var sekunder = ((new Date().getTime() - searchTimer) / 1000).toString();
        sekunder = sekunder.substring(0, Math.min(sekunder.length, 5));
        sekunder = sekunder.replace(".", ",");
        $('#search_timer').text(sekunder);


      });

}

function makeLink(element) {
  element.mouseout(function () {
    element.css('cursor', 'default');
  });
  element.mouseover(function () {
    element.css('cursor', 'pointer');
  });

}

function appendFacetElement(html, facet) {
  var facetElement = $(html);
  facetElement.get(0).facet = facet;

  var facetNameElement = facetElement.find('.facet_name');
  makeLink(facetNameElement);

  var valuesElement = facetElement.find('.facet_values');

  facetElement.find('.facet_value_name').each(function (index, element) {
    makeLink($(element));
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

  function makeActiveFacetClickable(element, facetValue, facet) {
    $(element).click(function () {
      selectedFacets.push(facetValue);
      var activeFacet = $('<span/>');
      activeFacet.addClass('active_facet');
      activeFacet.addClass('link');
      activeFacet.click(function () {
        selectedFacets.splice(selectedFacets.indexOf(facet), 1);
        $(this).remove();
        search();
      });
      activeFacet.text(facet.name + ": " + facetValue.name);

      makeLink(activeFacet);

      activeFacet.appendTo($('#active_facets'));
      search();
    });
  }

  valuesElement.children().each(function (index, element) {
    var facet = facetElement.get(0).facet;
    var facetValue = facet.values[index];
    makeActiveFacetClickable(element, facetValue, facet);
  });

  facetElement.appendTo($("#facets"));
  return facetElement;
}

function getTypeText(searchResult) {
  if (searchResult.type === "Arende") {
    return "Ärende";
  } else if (searchResult.type === "Atgard") {

    if (searchResult.instance.inkom && searchResult.instance.utgick) {
      return "Inkommande och utgående åtgärd";
    } else if (searchResult.instance.inkom) {
      return "Inkommande åtgärd";
    } else if (searchResult.instance.utgick) {
      return "Utgående åtgärd";
    } else {
      return "Åtgärd";
    }
  } else if (searchResult.type === "Dokument") {
    return "Dokument";
  } else {
    return type;
  }
}

function formatPercent(part, total, leftPadding) {
  var html = '<span class="percent">';
  var paddingLeft = 0;
  if (part === total) {
    html += "100%";
  } else {

    var factor = part / total;
    var percent = (factor * 100).toString().replace(".", ",");

    if (factor >= 0.1) {
      if (leftPadding) {
        html += "&nbsp;&nbsp;";
      }
      html += percent.substring(0, 2) + "%";
      if (!leftPadding) {
        html += "&nbsp;&nbsp;";
      }

    } else {
      if (leftPadding) {
        html += "&nbsp;";
      }
      html += percent.substring(0, 3) + "%";
      if (!leftPadding) {
        html += "&nbsp;";
      }
    }
  }
  html += "</span>";


  return html;
}





