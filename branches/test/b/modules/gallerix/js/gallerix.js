//Globals
var path;
var base_path;
var pid;
var default_image;
var get_directory;
var widgets;
var fetch;
var loader_image;

$(window).load(function(){

  path = document.location.href;
  base_path = path.split('#')[0]; //We always want to know the base node path, so we can tack on anchors.
  pid = path.split('#')[1]; //We want to know what pid the user provided, if any.
  default_image = Drupal.settings.gallerix['default'];
  get_directory = Drupal.settings.gallerix['get'];  
  loader_image = $('#gallerix-loader').css('background-image'); 
   
   
  fetch = function (data) {
    var image = Drupal.parseJson(data);
    pid = image['key'];
    document.location.href = base_path + '#' + pid;
    var thumbnails = image['thumbnails'];
    var thumbnail_count = image['thumbnail_count'];    

    $('#gallerix-message').html('');

      
    image_buffer = new Image(image['width'], image['height']);
    image_buffer.src= image['path'];
    

    
    var load_image = function () {
      var height = parseInt(image['height']) + 10;
      
      $('#gallerix-full').css('height', height + 'px');
      $('#gallerix-full').attr('src', image['path']);
      $('#gallerix-display').fadeTo(500, 1);
      $('#gallerix-loader').show().css('background-image', 'none');
      
      var thumbnail_buffer = new Image();
      thumbnail_buffer.src = thumbnails['gallerix-thumbnail-link-' + (thumbnail_count - 1)][1];
      //alert(thumbnail_buffer.src);
      
      //$(thumbnail_buffer).load(function () {
      //thumbnail_buffer.onload = function () {
        for (thumbnail in thumbnails) {
          $('#' + thumbnail).attr('href', thumbnails[thumbnail][0]).find('img').attr('src', thumbnails[thumbnail][1]).attr('title', thumbnails[thumbnail][2]);
        } 
      //}         
      //});       
    }
    
    
    
    //$(image_buffer).load(load_image);
    image_buffer.onload = load_image;

    
    
    if (Drupal.settings.gallerix['widgets_enabled']) {
      update_gallerix_widgets(image);
    }
    
    gallerix_attach_events(image);
  }
  
  //Load the first image. 
  gallerix_fetch_picture(get_directory + (pid ? pid : default_image));   
  
  gallerix_thumbnails();
  
 
});

function gallerix_attach_events() {
    var ajax_link = function (data) {
      eval(data);
    }
    
    $('.gallerix-ajax-link').click(function() {
      $.get(this.href, null, ajax_link);
      
      return false;
    });   

}

function gallerix_fetch_picture(location) {
  $('#gallerix-loader').css('background-image', loader_image);    
  $('#gallerix-display').fadeTo(500, 0.01);  
  $.get(location, null, fetch);
}



function gallerix_thumbnails() {

  $('.gallerix-picture-link').each(function() {
    $(this).click(function() {
      gallerix_fetch_picture(this.href);
      return false;
    });
    
    $(this).css('opacity', .6);
    
    
    
    var mouseover = function () {
      $(this).css('opacity', 1);
    }
    var mouseout = function () {
      $(this).css('opacity', .6);
    }       
    
    /*
    var mouseover = function () {
      var current_link_id = $(this).attr('id');
      
      $('.gallerix-picture-link').each(function() {
        if ($(this).attr('id') != current_link_id) {
          $(this).css('opacity', '.25');
        }
      });
    }
    
    var mouseout = function () {
      $('.gallerix-picture-link').each(function() {
        $(this).css('opacity', '1');
      });
    } 
    */  
    
    $(this).mouseover(mouseover);
    $(this).mouseout(mouseout);
    
    
    
  }); 
  
  
  
  
}
