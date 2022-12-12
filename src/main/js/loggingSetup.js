import log4javascript from 'log4javascript/log4javascript';

// Allows Scala.js to summon log4javascript from the global scope.
// Also adds some extension methods.

log4javascript.createJsonLayout = function () {
    return new log4javascript.JsonLayout();
};

log4javascript.createBrowserConsoleAppender = function () {
    return new log4javascript.BrowserConsoleAppender();
};

log4javascript.createPopupAppender = function () {
    return new log4javascript.PopupAppender();
};

log4javascript.createAjaxAppender = function (url) {
    return new log4javascript.AjaxAppender(url);
};

window.log4javascript = log4javascript;
