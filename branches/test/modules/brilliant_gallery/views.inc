<?php
// $Id: views.inc,v 1.2 2007/12/09 12:53:10 tjfulopp Exp $

/**
 * Implementation of hook_views_tables()
 */
function brilliant_gallery_views_tables() {
  $tables['brilliant_gallery'] = array(
    'name' => 'brilliant_gallery',
/*
    'join' => array(
    'left' => array(
      'table' => 'node',
      'field' => 'nid',
    ),
    'right' => array(
      'field' => 'nid',
    ),
    ),
*/
    'fields' => array(
      'node' => array(
        'name' => t('Brilliant Gallery: A random image'),
        'handler' => array(
          'brilliant_gallery_views_handler_image_img' => t('Random image'),
          #'brilliant_gallery_views_handler_image_img_link' => t('bgImage with link'),
        ),
/*
        'option' => array(
          '#type' => 'select',
          '#options' => 'brilliant_gallery_views_handler_filter_image_size',
        ),
*/
        'notafield' => true,
        'sortable' => false,
        'help' => t('Display one random image from the respective gallery.'),
      ),
    ),
  );
  return $tables;
}

/**
 * Views handler for displaying the image.
 */
function brilliant_gallery_views_handler_image_img($fieldinfo, $fielddata, $value, $data) {
/*
  $node = node_load($data->nid);
  return image_display($node, $fielddata['options']);
*/
/*
  $tmp = '.1. ' . implode( ' // ', $fieldinfo );
  $tmp .= '<br>.2. ' . implode( ' // ', $fielddata );
  $tmp .= '<br>.3. ' . implode( ' // ', $value );
  $tmp .= '<br>.4. ' . implode( ' // ', $data );
  $tmp .= '<br>.5. ' . $data->field_gallery_value;
  #field_gallery_value
*/
  $tmp = $data->nid;
  $query = "SELECT SQL_CACHE `field_gallery_value` FROM `content_type_page` WHERE `nid` = '". $tmp ."' ORDER BY `vid` DESC LIMIT 1";
  $string = db_result( db_query( $query ), 0 );
  $string = str_replace( array( '[bg|', ']' ), '', $string );
  if ( strpos( $string, '|' ) !== false ) {
       $string = substr( $string, 0, (strpos( $string, '|' )) );
     }
  if ( $string == '' ) return;
  # Now get a list of images and choose one of them.
    $absolpath = realpath(file_directory_path() . '/' . variable_get('brilliant_gallery_folder', '') . '/' . $string);
    #$string .= file_directory_path() . '/' . $string;
    # Load Directory Into Array  
      $poct = -1;
      $retval = array();
      $handle = opendir($absolpath);
      while ( $file = readdir($handle) ) {
              $poct += 1;
              $testending = strtolower( substr( $file, -4, 4 ) );
              if (  strtolower( $testending ) <> '.jpg' and strtolower( $testending ) <> 'jpeg' and strtolower( $testending ) <> '.gif' and strtolower( $testending ) <> '.png' ) { continue; }
              $retval[$poct] = $file;
            }
      closedir( $handle );
      #print_r( $retval );
  $randimg = mt_rand( 0, count( $retval ) );
  $result = $absolpath . '/' . $retval[$randimg];
  $temp = getimagesize( $result );
  #$imagewidth = variable_get('brilliant_gallery_maximagewidth', 150);
  $imgh = 100; # Hard-coded height for this purpose.
  $imgw = round( ($temp[0]/$temp[1])*$imgh );
  # Get this module's path:
    # Patching a possible problem with i18n
      $langcode = '';
      if ( function_exists('i18n_get_lang') ) { $langcode = i18n_get_lang(); }
    $modulepath = url(drupal_get_path('module', 'brilliant_gallery'), NULL, NULL, TRUE);
    # url() ads i18n codes to the URL ... we need to remove them here...
      if ( $langcode <> '' ) {
           $modulepath = str_replace( '/' . $langcode . '/', '/', $modulepath );
         }
    # Non-clean URLs need removing ?q=
      $modulepath = str_replace( "?q=", "",  $modulepath );
  $result = '<a href="' . $modulepath .'/image.php?imgp=' . base64_encode( $absolpath . '/' . $retval[$randimg] ) . '&imgw=' . $imgw*6 . '&imgh=' . $imgh*6 . '"';
  $setname = mt_rand( 1, 9999999 );
  $overbrowser = variable_get('brilliant_gallery_overbrowser', 'thickbox');
  switch( $overbrowser ) {
    case 'thickbox':
      $result .= ' class="thickbox"';
      $result .= ' rel="img_' . $setname . '"';
      #$attributes['class'] = $link_class;
      #$attributes['rel'] = 'img_' . ($node->nid? $node->nid: time()); // 'insert' has no $node->nid
      break;
    case 'lightbox':
      $result .= ' rel="lightbox[' . $setname . ']"';
      #$attributes['rel'] = 'lightbox[' . ($node->nid? $node->nid: time()) . ']'; // 'insert' has no $node->nid
      break;
    case 'greyboxr':
      $result .= ' class="greybox"';
      #$result .= ' rel="gb_imageset[' . $setname . ']"';
      break;
    default:
      break;
  }
  
  if ( $showcaption <> '' ) {
       $result .= ' title="' . $caption . '"';
     }
  $result .= '>';
  $result .= '<img style="display: block;border:0;align:right" src="' . $modulepath .'/image.php?imgp=' . base64_encode( $absolpath . '/' . $retval[$randimg] ) . '&imgw=' . $imgw . '&imgh=' . $imgh . '" />'; # width="' . $imgw . '"
  $result .= '</a>';
  return $result;
}


/**
 * Views - Generate a list of all the valid sizes that are available
 */
/*
function brilliant_gallery_views_handler_filter_image_size($op) {
  foreach (_image_get_sizes() as $key => $size) {
    $a[$key] = $size['label'];
  }
  return $a;
}
*/