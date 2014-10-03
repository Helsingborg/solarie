var S_version = "0.0.1";

var S_getApiUrl = function getApiURL() {
  return "/api/" + S_version + "/";
};

var S_apiUrlBuilder = function apiUrlBuilder(suffix) {
  return S_getApiUrl() + suffix;
};

var S_guid = function guid() {

  function s4() {
    return Math.floor((1 + Math.random()) * 0x10000)
        .toString(16)
        .substring(1);
  }

  return s4() + s4() + '-' + s4() + '-' + s4() + '-' +
      s4() + '-' + s4() + s4() + s4();

};

var S_session = S_guid();


var S_Diarium = function Diarium(identity) {
  this.identity = identity === undefined ? null : parseInt(identity);
  this.namn = null;
  this.senasteSynkronisering = null;
  this.jdbcURL = null;
};


/**
 * todo copy documentation from servlet
 *
 * @param request Object
 * @param success function(Object response, String textStatus, jqXHR jqXHR)
 */
var S_search = function search(request, success) {
  request.session = S_session;
  request.reference = S_guid();

  $.post(
      S_apiUrlBuilder("search"),
      JSON.stringify(request),
      function post_success(data, textStatus, jqXHR) {
        success(data, textStatus, jqXHR);
      });
};

/**
 * todo copy documentation from servlet
 *
 * @param diarium S.Diarium
 * @param success function(Object response, String textStatus, jqXHR jqXHR)
 */
var S_synchronizeDiarium = function synchronizeDiarium(diarium, success) {
  $.post(
      S_apiUrlBuilder("diarium/synchronize"),
      JSON.stringify({
        reference: S_guid(),
        session: S_session,
        identity: diarium.identity
      }),
      function post_success(data, textStatus, jqXHR) {
        success(data, textStatus, jqXHR);
      });
};

