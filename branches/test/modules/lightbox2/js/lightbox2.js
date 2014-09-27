if (Drupal.jsEnabled) {
  $(document).ready(function () {

    // handle lightbox2_settings_form
    lightbox2_lite_handler();
    image_node_handler();
    $("input[@name=lightbox2_lite]").bind("click", lightbox2_lite_handler);
    $("input[@name=lightbox2_use_alt_layout]").bind("click", alt_layout_handler);
    $("input[@name=lightbox2_image_node]").bind("click", image_node_handler);
    $("input[@name=lightbox2_flickr]").bind("click", image_node_handler);
  });
}


function lightbox2_lite_handler(event) {
  // enable / disable the image node options
  if ($("input[@name=lightbox2_lite]:checked").val() == 1) {
    $("input[@name=lightbox2_use_alt_layout]").attr("disabled", "disabled");
    $("input[@name=lightbox2_force_show_nav]").attr("disabled", "disabled");
    $("input[@name=lightbox2_disable_zoom]").attr("disabled", "disabled");
    $("input[@name=lightbox2_image_node]").attr("disabled", "disabled");
    $("input[@name=lightbox2_node_link_text]").attr("disabled", "disabled");
    $("input[@name=lightbox2_image_group]").attr("disabled", "disabled");
    $("input[@name=lightbox2_flickr]").attr("disabled", "disabled");
    $("input[@name=lightbox2_disable_nested_galleries]").attr("disabled", "disabled");
    $("input[@name=lightbox2_image_count_str]").attr("disabled", "disabled");
  }
  else {
    $("input[@name=lightbox2_use_alt_layout]").removeAttr("disabled");
    $("input[@name=lightbox2_force_show_nav]").removeAttr("disabled");
    $("input[@name=lightbox2_disable_zoom]").removeAttr("disabled");
    $("input[@name=lightbox2_image_node]").removeAttr("disabled");
    $("input[@name=lightbox2_flickr]").removeAttr("disabled");
    $("input[@name=lightbox2_node_link_text]").removeAttr("disabled");
    $("input[@name=lightbox2_image_group]").removeAttr("disabled");
    $("input[@name=lightbox2_disable_nested_galleries]").removeAttr("disabled");
    $("input[@name=lightbox2_image_count_str]").removeAttr("disabled");
    image_node_handler();
    alt_layout_handler();
  }
}

function alt_layout_handler(event) {
  if ($("input[@name=lightbox2_lite]:checked").val() != 1) {
    if ($("input[@name=lightbox2_use_alt_layout]:checked").val() == 1) {
      $("input[@name=lightbox2_force_show_nav]").attr("disabled", "disabled");
    }
    else {
      $("input[@name=lightbox2_force_show_nav]").removeAttr("disabled");
    }
  }
}
function image_node_handler(event) {
  // image node and flickr stuff
  if ($("input[@name=lightbox2_lite]:checked").val() != 1) {
    if ($("input[@name=lightbox2_image_node]:checked").val() == 1 || $("input[@name=lightbox2_flickr]:checked").val() == 1) {
      $("input[@name=lightbox2_node_link_text]").removeAttr("disabled");
      $("input[@name=lightbox2_image_group]").removeAttr("disabled");
    }
    else {
      $("input[@name=lightbox2_node_link_text]").attr("disabled", "disabled");
      $("input[@name=lightbox2_image_group]").attr("disabled", "disabled");
    }

    // image node only stuff
    if ($("input[@name=lightbox2_image_node]:checked").val() == 1) {
      $("input[@name=lightbox2_disable_nested_galleries]").removeAttr("disabled");
    }
    else {
      $("input[@name=lightbox2_disable_nested_galleries]").attr("disabled", "disabled");
    }
  }
}
