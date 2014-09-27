<?php
/* $Id: image.imagick.inc,v 1.5 2006/07/03 14:04:23 vhmauery Exp $ */

/**
 * Return information about the imagick toolkit
 */
function image_imagick_info() {
  return array('name' => 'imagick', 'title' => 'Imagick (libmagick6) Toolkit.');
  
}

function image_imagick_check_settings() {
  if ($check = get_extension_funcs('imagick')) {
    if (in_array('imagick_readimage', $check)) {
      return true;
    }
  }
  return false;
}
/**
 * Validate and return toolkit specific settings
 */
function image_imagick_settings() {
    if (!image_imagick_check_settings()) {
        form_set_error('image_toolkit', t("The built-in imagick image toolkit requires that the imagick module for PHP be installed and configured properly. For more information see %url.", array('%url' => '<a href="http://pecl.php.net/package/imagick</a>')));
    }
    $imagick_settings['imagick_imagejpeg_quality'] = array(
            '#type'=>'textfield',
            '#title'=>t('Imagick JPEG image quality (1-100)'),
            '#default_value'=>variable_get('imagick_imagejpeg_quality', 75),
        );
    return $imagick_settings;
}

/**
 * Common helper functions
 */
function _image_imagick_report($handle, $function) {
    $reason = imagick_failedreason($handle);
    $description = imagick_faileddescription($handle);
    drupal_set_message(t('%function failed: %reason: %description',
        array('%function' => $function, '%reason' => $reason, '%description' => $description)));
}
function _image_imagick_read($file) {
    $handle = imagick_readimage($file);
    if (!$handle) {
        drupal_set_message(t('imagick_readimage failed to open file %file', array('%file' => $file)));
        return false;
    } elseif (imagick_iserror($handle)) {
        _image_imagick_report($handle, 'imagick_readimage');
        return false;
    }
    return $handle;
}
function _image_imagick_write($handle, $file) {
    if (!$handle) {
        drupal_set_message(t('imagick_readimage failed to open file %file', array('%file' => $file)));
        return false;
    } else {
        if (!imagick_set_image_quality($handle, variable_get('imagick_imagejpeg_quality', 75))) {
            _image_imagick_report($handle, 'imagick_set_image_quality');
            return false;
        }
        if (!imagick_writeimage($handle, $file)) {
            _image_imagick_report($handle, 'imagick_writeimage');    
            return false;
        }
    }
    return true;
}

/**
 * Resize an image to the given width and height
 */
function image_imagick_resize($source, $dest, $width, $height) {
    if (!($handle = _image_imagick_read($source)))
        return false;
    if (!imagick_scale($handle, $width, $height, "!")) {
        _image_imagick_report($handle, 'imagick_scale');    
        return false;
    }
    return _image_imagick_write($handle, $dest);
}

/**
 * Rotate an image
 */
function image_imagick_rotate($source, $dest, $degrees) {
    if (!($handle = _image_imagick_read($source)))
        return false;
    if (!imagick_rotate($handle, $degrees)) {
        _image_imagick_report($handle, 'imagick_rotate');    
        return false;
    }
    return _image_imagick_write($handle, $dest);
}

/**
 * Crop an image to the specified dimensions
 */
function image_imagick_crop($source, $dest, $x, $y, $width, $height) {
    if (!($handle = _image_imagick_read($source)))
        return false;
    if (!imagick_crop($handle, $x, $y, $width, $height)) {
        _image_imagick_report($handle, 'imagick_crop');    
        return false;
    }
    return _image_imagick_write($handle, $dest);
}

/**
 * Composite one image on another
 */
function image_imagick_composite($donkey, $tail, $dest, $x, $y) {
    if (!($handle = _image_imagick_read($donkey)))
        return false;
    if (!($comp_handle = _image_imagick_read($tail)))
        return false;
    imagick_composite($handle, IMAGICK_COMPOSITE_OP_OVER,
            $comp_handle, 200, 200);
    return _image_imagick_write($handle, $dest);
}

?>
