// Image Node Auto-Format with Auto Image Grouping
// Original version by Steve McKenzie
// Altered by Stella Power for jQuery version

if (Drupal.jsEnabled) {
  $(document).ready(function lightbox2_image_nodes() {

    var settings = Drupal.settings.lightbox2;

    // don't do it on the image assist popup selection screen
    var img_assist = document.getElementById("img_assist_thumbs");
    if (!img_assist) {

      // select the enabled image types
      var classes = settings.image_node_classes;
      $("a["+classes+"]").each(function(i) {

        if (!settings.disable_for_gallery_lists || (settings.disable_for_gallery_lists && !$(this).parents(".galleries").length)) {
          var child = $(this).children();

          // set the alt text
          var alt = $(child).attr("alt");
          if (!alt) {
            alt = "";
          }

          // set the image node link text
          var link_text = settings.node_link_text;

          // set the rel attribute
          var rel = "lightbox";
          if (settings.group_images) {
            rel = "lightbox[node_thumbnails]";
          }

          // set the href attribute
          var href = $(child).attr("src").replace(".thumbnail", "").replace(/(image\/view\/\d+)(\/\w*)/, "$1/_original");

          // handle flickr images
          if ($(child).attr("class").match("flickr-photo-img")) {
            href = $(child).attr("src").replace("_s", "").replace("_t", "").replace("_m", "").replace("_b", "");
            if (settings.group_images) {
              rel = "lightbox[flickr]";
            }
          }

          // modify the image url
          $(this).attr({rel: rel,
            title: alt + "<br /><a href=\"" + this.href + "\" id=\"node_link_text\">"+ link_text + "</a>",
            href: href
            });
        }

      });

    }
  });
}
