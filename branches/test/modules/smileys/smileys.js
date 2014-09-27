// $Id: smileys.js,v 1.1.2.4 2007/04/29 19:53:45 Gurpartap Exp $

/* Filename: smileys.js
 * jQuery Smileys Code for Drupal smileys module.
 * License: GPL (Read LICENSE.txt for more information).
 * Copyright, authors.
*/

Drupal.smileysAutoAttach = function() {
  timer = undefined;
  doIt = false;
  $('#showSmileysWindow').oneclick(function() {
    var basePath = Drupal.settings.smileys.basePath;
    $('<div id="smileysWindow"></div>').appendTo('body').load(basePath + 'live/smileys', Drupal.smileysAttach).hide();
    $(window).scroll(smileysWindow).resize(smileysWindow);
    smileysWindow(1);
  }).click(function() {
    $('#smileysWindow').toggle("slow");
    doIt = doItAlter(doIt);
  });
  Drupal.smileysAttach();
}

Drupal.smileysAttach = function() {
  $('#closeSmileys').click(function() {
    $('#smileysWindow').hide("slow");
    doIt = doItAlter(doIt);
  });
  $('img.smiley-class', this).click(function(){
    var smiley = ' ' + this.alt + ' ';
    $('textarea#edit-body, textarea#edit-comment').each(function() {
      if (document.selection) {
        this.focus();
        document.selection.createRange().text = smiley;
      }
       else if (this.selectionStart || this.selectionStart == '0') {
        var cursorPos = this.selectionEnd + smiley.length;
        this.value = this.value.substring(0, this.selectionStart) + smiley + this.value.substring(this.selectionEnd);
        this.selectionStart = this.selectionEnd = cursorPos;
      }
      else {
        this.value = this.value + smiley;
      }
      this.focus();
    });
  });
}

function smileysWindow(one) {
  one = one == 1 ? 1 : 0;
  if (doIt == false && one == 0) {
    return false;
  }
  if (timer) {
    clearTimeout(timer);
    timer = undefined;
  }
  timer = setTimeout(function() {
    var width = 0, height = 0;
    if (typeof(window.innerWidth) == 'number' ) {
      width = window.innerWidth;
      height = window.innerHeight;
    } //Non-IE
    else if (document.documentElement && (document.documentElement.clientWidth || document.documentElement.clientHeight)) {
      width = document.documentElement.clientWidth;
      height = document.documentElement.clientHeight;
    } //IE 6+ in 'standards compliant mode'
    else if (document.body && (document.body.clientWidth || document.body.clientHeight)) {
      width = document.body.clientWidth;
      height = document.body.clientHeight;
    } //IE 4 compatible
    var left = 0, top = 0;
    if (typeof(window.pageYOffset) == 'number') {
      top = window.pageYOffset;
      left = window.pageXOffset;
    } //Netscape compliant
    else if (document.body && (document.body.scrollLeft || document.body.scrollTop)) {
      top = document.body.scrollTop;
      left = document.body.scrollLeft;
    } //DOM compliant
    else if(document.documentElement && (document.documentElement.scrollLeft || document.documentElement.scrollTop)) {
      top = document.documentElement.scrollTop;
      left = document.documentElement.scrollLeft;
    } //IE6 standards compliant mode
    $('#smileysWindow').animate({'top': top + (height / 2) - (275 / 2), 'left': left + (width / 2) - (290 / 2)}, "slow");
  }, 500);
}

function doItAlter(doIt) {
  return (doIt == true) ? false : true
}

if (Drupal.jsEnabled) {
  $(document).ready(Drupal.smileysAutoAttach);
}
