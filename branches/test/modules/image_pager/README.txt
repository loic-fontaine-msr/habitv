Image Pager module

PREREQUISITES

If you are using Drupal 4.7, Image Pager requires the jquery47
module.  Image Pager has no extra dependencies with Drupal 5.

INSTALLATION

Install and activate the jquery47 and Image Pager modules like every
other Drupal module.

DESCRIPTION

Image Pager provides a block that displays a selected subset of a
page's images.  The images are shown one at a time; the user can
dynamically switch among them using previous/next links.  Each image's
alt and title text is shown as a caption.  A demonstration is
available at http://jaspan.com/dynamic-image-pager-module.

Image Pager is designed to work with existing node types and themes
without requiring changes to either.  When the Image Pager block is
displayed, it uses jQuery to scan the web page, identify the images to
be shown in the Pager, hide them so they are not shown in their
original location on the page, and redisplay them within the
Pager.

Image Pager's jQuery can also be used on its own without Drupal; see
below for details.

Selecting images

The set of images that Image Pager displays (and hides in their
original location) is determined by a CSS (1, 2, or 3) or XPath
selector.  The default selector is ".node .content img" which means
that all images in the content area of a node (or a node teaser) are
included.  If you only want Image Pager to be active for node pages
and not for teasers, you can see the Image Pager Block settings so it
only displays on "node/*" pages.

You can override the theme function image_pager_selector to return a
different selector as as string.  For example, in your template.php:

function phptemplate_image_pager_selector() {
  return 'img.show-in-pager';
}

This would cause all images anywhere on the page with the
"show-in-pager" class to be displayed in the pager.

Styling images and graceful degredation

You can style the images displayed in the Image Pager with CSS.  If
you are using the default Image Pager theme (see below), the selector
'.image-pager img' will work.  For example:

.image-pager img {
  border: 1px solid gray;
  padding: 3px;
  background-color: white;
}

Image Pager does not work if JavaScript is not available.  To keep
your pages useful and attractive, just make sure your images are
styled to look good in their original location.  For example:

img.show-in-pager {
  float: right;
  clear: right;
  border: 1px solid gray;
  margin: 0.5em 0 0.5em 1em;
  padding: 3px;
  background-color: white;
}

You can turn Image Pager off to simulate a no-JavaScript environment
by adding the query string "?image_pager=off" to the URL.

Changing the pager theme

You can change the layout of the Image Pager block itself by
overriding the theme function image_pager_block.  The default function
is:

function theme_image_pager_block() {
  return ('<div class="image-pager">'.
	  '<div class="pager">'.
	  '  <a class="prev">&laquo; Prev</a>'.
	  '  <span class="count"></span>'.
	  '  <a class="next">Next &raquo;</a>'.
	  '</div>'.
	  '<div class="image"><a><img /></a></div>'.
	  '<div class="caption"></div>'.
	  '<div class="credit"></div>'.
	  '<form action="/">'.
	  '  <input type="hidden" name="sel" value="'.
	  str_replace('+','%20',urlencode(theme('image_pager_selector'))).
	  '" />'.
	  '</form>'.
	  '</div>');
}

The Image Pager JavaScript looks for an object with the class
"image-pager" and then looks inside of it for objects with class
"prev", "next", "image", "catpion", and "credit".  If you omit one of
these objects, that part of the Image Pager will simply not be shown.
The "image" object should contain an <img> tag which is used to
display the paged images.

The little dance with the <form> inside the image-pager is how Image
Pager passes the output of theme('image_pager_selector') from the
server to the client-side JavaScript.  If anyone know a better way to
do this, let me know.

Image sizes

Image Pager does not manipulate the sizes of the images it finds to
display; that is your responsibility.  The Drupal image module allows
you to control the size of upload images.  If you are using CCK, I
suggest using the imagecache module.

If the "image" object inside "image-pager" HTML block contains an <a>
tag, the anchor's href will be a link to the current page with the
query "?image=<num>" appended.  This allows "click to enlarge"
functionality, especially if the images in the pager come from a CCK
imagefield, but you will need to write a custom node template to make
it work.  There are probably better ways to implement this feature.

Using Image Pager without Drupal

To use Image Pager without Drupal, download the Drupal module.  Load
image_pager.js into your web page and manually add an "image-pager"
block like the one output by the theme_image_pager_block() PHP
function shown above.

Sponsor

Event Publishing LLC
http://www.event-solutions.com

AUTHOR

Barry Jaspan
http://jaspan.com
firstname at lastname dot com
