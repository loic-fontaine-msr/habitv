/* $Id: image_pager.js,v 1.2.2.1 2007/02/08 21:01:16 bjaspan Exp $ */

var pp_pp;
var pp_prev;
var pp_next;
var pp_images = [];
var pp_idx = 0;

/*
  This little trick allows the same code to work with Drupal 4.7's
  jquery47 module, which renames the $ function to JQ, as well as with
  pure jQuery and Drupal 5.
*/
if (typeof JQ == "undefined") {
    function JQ(a,c) {
	return jQuery(a,c);
    }
}

function check_plain(s) {
    return s.replace(/&/g, '&amp;').replace(/\"/g, '&quot;').
	replace(/\'/g, '&#039').replace(/</g, '&lt;').replace(/>/g, '&gt;');
}

function pp_or(v) { return v ? v : ''; }

function pp_display(idx) {
    if (idx < 0) {
	idx = 0;
    } else if (idx >= pp_images.length) {
	idx = pp_images.length-1;
    }

    pp_pp.
	find('.image a').href("?image="+idx).end().
	find('.image img').src(pp_images[idx].src()).
	  attr('title', pp_or(pp_images[idx].attr('alt'))).end().
	find('.caption').
	  html(check_plain(pp_or(pp_images[idx].attr('alt')))).end().
	find('.credit').
	  html(check_plain(pp_or(pp_images[idx].attr('title')))).end().
	find('.count').html((idx+1)+' of '+pp_images.length).end();

    pp_prev.removeClass('disabled');
    pp_next.removeClass('disabled');
    if (idx == 0) {
	pp_prev.addClass('disabled');
    }
    if (idx == pp_images.length-1) {
	pp_next.addClass('disabled');
    }

    return idx;
}

JQ(function() {
    /* if the URL query contains image_pager=off, do nothing */
    var off = /[?&]image_pager=off/;
    if (window.location.search.search(off) != -1) {
	return;
    }
    
    pp_pp = JQ('.image-pager');

    JQ(decodeURIComponent(JQ(pp_pp).find('input[@name="sel"]').attr('value'))).
    each(function () {
	pp_images[pp_images.length] = JQ(this);
	JQ(this).hide();
    });
    
    if (pp_images.length == 0) {
	return;
    }

    pp_pp = JQ('.image-pager');
    pp_prev = JQ(pp_pp).find('.prev').
	click(function() { pp_idx=pp_display(pp_idx-1); });
    pp_next = JQ(pp_pp).find('.next').
	click(function() { pp_idx=pp_display(pp_idx+1); });

    pp_display(pp_idx);

    JQ('.block-image_pager').show();

    if (pp_images.length == 1) {
	JQ(pp_pp).find('.pager').hide();
    }
});
		
