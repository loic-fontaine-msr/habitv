// $id$
function postletFinished(){
  var imagexReturn = function (data) {
    var returnHtml = Drupal.parseJson(data);
    $('#imagexthumbs').hide();
    $('#imagexthumbs').html(returnHtml['html']);
    $('#imagexthumbs').slideDown('slow');
  }
  $.get('http://quartz.nhm.ac.uk/imagex/get', null, imagexReturn);
}
function imagexclick(nid){
  var selectedImages = $('#edit-selected-images').val();
  var indexOfNid = selectedImages.indexOf('|'+nid+'|');
  if(indexOfNid>-1){
    // nid is in list remove it
    selectedImages = selectedImages.replace('|'+nid+'|','|');
    $('#imagexthumb-'+nid).css({ border: "solid 2px grey"});
  }
  else {
    // nid NOT in list add it
    selectedImages += nid+'|';
  }
  $('#edit-selected-images').val(selectedImages);
  var selectedImagesArray = selectedImages.split("|");
  var numberSelected = selectedImagesArray.length;
  for(i=0;i<numberSelected;i++){
    $('#imagexthumb-'+selectedImagesArray[i]).css({ border: "solid 2px blue"});
  }
}