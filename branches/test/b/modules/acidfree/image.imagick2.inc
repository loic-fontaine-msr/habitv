<?php
/**
 * Return information about the imagick2 toolkit
 */
function image_imagick2_info() {
  return array('name' => 'imagick2', 'title' => 'Imagick2 (libmagick6) Toolkit.');
  
}

function image_imagick2_check_settings() {
	if (class_exists('Imagick'))
		return true;
	return false;
}
/**
 * Validate and return toolkit specific settings
 */
function image_imagick2_settings() {
    if (!image_imagick2_check_settings()) {
        form_set_error('image_toolkit', t("The built-in imagick2 image toolkit requires that the Imagick2 module for PHP be installed and configured properly. For more information see %url.", array('%url' => '<a href="http://pecl.php.net/package/imagick</a>')));
    }
    $imagick2_settings['imagick2_imagejpeg_quality'] = array(
            '#type'=>'textfield',
            '#title'=>t('Imagick2 JPEG image quality (1-100)'),
            '#default_value'=>variable_get('imagick2_imagejpeg_quality', 75),
        );
    return $imagick2_settings;
}

/**
 * Common helper functions
 */
function _image_imagick2_report($function, $e) {
    drupal_set_message(t('%function failed: %description',
        array('%function' => $function, '%description' => $e->getMessage())));
}
function _image_imagick2_read($file) {
	try {
		$img = new Imagick();
		$img->readImage($file);
	} catch (exception $e) {
		_image_imagick2_report(__FUNCTION__, $e);
        return false;
    }
    return $img;
}
function _image_imagick2_write($img, $file) {
    try {
        $img->setCompressionQuality(variable_get('imagick2_imagejpeg_quality', 75));
        $img->writeImage($file);
		$img->destroy();
	} catch (exception $e) {
		_image_imagick2_report(__FUNCTION__, $e);
        return false;
    }
    return true;
}

/**
 * Resize an image to the given width and height
 */
function image_imagick2_resize($source, $dest, $width, $height) {
    if (!($img = _image_imagick2_read($source))) {
		echo "read failed\n";
        return false;
	}
	try {
		$img->resizeImage($width, $height, imagick::FILTER_QUADRATIC, 1);
	} catch (exception $e) {
		_image_imagick2_report(__FUNCTION__, $e);
        return false;
    }
	if (!_image_imagick2_write($img, $dest)) {
		echo "write failed\n";
		return false;
	}
	return true;
}

/**
 * Rotate an image
 */
function image_imagick2_rotate($source, $dest, $degrees) {
    if (!($img = _image_imagick2_read($source)))
        return false;
    try {
		$img->rotateImage(new ImagickPixel(), $degrees);
	} catch (exception $e) {
		_image_imagick2_report(__FUNCTION__, $e);
        return false;
    }
    return _image_imagick2_write($img, $dest);
}

/**
 * Crop an image to the specified dimensions
 */
function image_imagick2_crop($source, $dest, $x, $y, $width, $height) {
    if (!($img = _image_imagick2_read($source)))
        return false;
    try {
		$img->cropImage($x, $y, $width, $height);
	} catch (exception $e) {
		_image_imagick2_report(__FUNCTION__, $e);
        return false;
    }
    return _image_imagick2_write($img, $dest);
}

/**
 * Composite one image on another
 */
function image_imagick2_composite($donkey, $tail, $dest, $x, $y) {
    if (!($img = _image_imagick2_read($donkey)))
        return false;
    try {
    	$comp_img = _image_imagick2_read($tail);
		$img->compositeImage($comp_img, imagick::COMPOSITE_ATOP, $x, $y);
	} catch (exception $e) {
		_image_imagick2_report(__FUNCTION__, $e);
        return false;
    }
    return _image_imagick2_write($img, $dest);
}

?>
