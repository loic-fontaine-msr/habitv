<?php
/* $Id: image_manip.inc,v 1.9.2.1 2008/03/06 15:06:41 vhmauery Exp $ */

/*
Acidfree Photo Albums for Drupal
Copyright (C) 2005 Vernon Mauery

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 2 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA
*/

/** @file
 * this file contains functions to abstract the image functions to make it
 * easier for us to make the calls the way we want.
 */

/**
 * create a temporary file in the drupal temp dir
 */
function _image_manip_mktmpfile($name, $ext=false) {
  $tmp = file_directory_temp();
  $file = tempnam($tmp, $name);
  if ($ext) {
  	rename($file, "{$file}.{$ext}");
	return "{$file}.{$ext}";
  }
  return $file;
}

/**
 * resize an image using the image library of choice
 *
 * @param $large
 *   path to a full size image to be resized
 * @param $small
 *   path to a smaller image that will be created
 *   if $small == $large, the $large image will be replaced
 * @param $size
 *   largest dimension for the new image
 *
 * @return
 *   false if image was not resized (not image file or size is smaller than image)
 *   true if image was resized as requested
 */
function acidfree_resize_image($large, $small, $x, $y=0) {
    if ($large == $small) {
        $small = _image_manip_mktmpfile('imagemanip', _acidfree_ext_from_file($small));
        $tmp = true;
    }
    if ($y==0) {
        $y = $x;
    }
    if (image_scale($large, $small, $x, $y)) {
        if ($tmp) {
            file_move($small, $large, FILE_EXISTS_REPLACE);
        }
        return true;
    } else {
        if ($tmp) {
            file_delete($small);
        }
        return false;
    }
}

/**
 * rotate an image the best we can
 *
 * @param $image
 *   path to the image to be rotated
 * @param $angle
 *   angle to rotate the image.  If this is 90, 180, or 270,
 *   we will first attempt lossless rotation.  If that fails,
 *   we will use lossy rotation.  If this is any other angle,
 *   we will skip straight to lossy rotation using convert.
 *
 * @return
 *   true for success, false for failure
 */
function acidfree_rotate_image($image, $angle) {
    $ext = _acidfree_ext_from_file($image);
    if (($angle == 90 || $angle ==180 || $angle == 270 || $angle == 'auto') && $ext == 'jpg') {
        // try to do lossless rotation first
        $exiftran_path = variable_get('acidfree_path_to_exiftran', '/usr/bin/exiftran');
        if (is_executable($exiftran_path)) {
            $command = "{$exiftran_path} -i -{$angle{0}} $image";
            system($command, $ret);
            return true;
        } else if ($angle == 'auto') {
            drupal_set_message('you must have exiftran installed to use automatic rotation', 'error');
            return false;
        }
        $jpegtran_path = variable_get('acidfree_path_to_jpegtran', '/usr/bin/jpegtran');
        if (is_executable($jpegtran_path)) {
            $outfile = _image_manip_mktmpfile('imagemanip', _acidfree_ext_from_file($image));
            $command = "{$jpegtran_path} -copy all -rotate $angle -outfile $outfile $image";
            system($command, $ret);
            if (!$ret) {
                file_move($outfile, $image, FILE_EXISTS_REPLACE);
                return true;
            }
            file_delete($outfile);
            return false;
        }
    } 
    if (image_get_toolkit() == 'gd') {
        $angle -= 180;
    }
    $outfile = _image_manip_mktmpfile('imagemanip', _acidfree_ext_from_file($image));
    if (image_rotate($image, $outfile, $angle)) {
        file_move($outfile, $image, FILE_EXISTS_REPLACE);
    } else {
        drupal_set_message(t('image rotate failed for %image', array('%image' => $image)));
        file_delete($outfile);
        return false;
    }
    return true;
}

/**
 * composite two images using imagemagick's composite command
 *
 * @param $tail
 *   path to a smaller image that will be pasted on top of the donkey
 * @param $donkey
 *   path to a full size image that is the base image
 * @param $outfile
 *   path to the composite of $donkey and $tail
 *   if $donkey == $outfile, $donkey will be replaced with the composite image
 * @param $dx
 *   x offset of where to place the $tail on the $donkey
 * @param $dy
 *   y offset of where to place the $tail on the $donkey
 * @param $options
 *   any composite specific options you wish to pass (man composite(1))
 *
 * @return
 *   nothing
 */
function acidfree_compose_image($tail, $donkey, $outfile, $dx=0, $dy=0, $options='') {
    if ($outfile == $donkey) {
        $outfile = _image_manip_mktmpfile('imagemanip', _acidfree_ext_from_file($small));
        $temp = true;
    } else {
        $temp = false;
    }
    image_composite($donkey, $tail, $outfile, $dx, $dy);
    if ($temp) {
        file_move($outfile, $donkey, FILE_EXISTS_REPLACE);
    }
}

function _acidfree_ext_from_file($filename) {
    $parts = pathinfo($filename);
    return strtolower($parts['extension']);
}

?>
