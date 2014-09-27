<?php
/* ex: set tabstop=4 expandtab shiftwidth=4 softtabstop=4: */
/*
Acidfree image Albums for Drupal
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

/**
 * @file
 * The is the class implementation of the image.  All the acidfree elements
 * must be defined in this same manner, which makes creating a new class
 * relatively simple -- copy one of the existing classes and modify the 
 * relevant functions.
 */

//require_once('image_manip.inc');

function class_image_register() {
    $class = new stdClass();
    if (!module_exists('image')) {
        return $class;
    }
    $class->class = 'image';
    $class->name = t('image');
    $class->addme = t('Add an image');
    $class->max_size = 2*(1 << 20);
    /* i.e. reports image/pjpeg as mime type for jpegs.  grrr. */
    $class->mime_ext = Array('image/jpeg'=>'jpg', 'image/pjpeg'=>'jpg', 'image/png'=>'png', 'image/gif' => 'gif');
    $class->form_alter = '_class_image_form_alter';
    $class->nodeapi = '_class_image_nodeapi';
    $class->access = 'create images';
    $class->import = 'acidfree_import_image';
    return $class;
}

/* this function is called by _acidfree_node_from_file with
 * the corresponding $node structure filled out as well as
 * that function could do.  The $file structure is also passed
 * which is what the $node was built from.
 */
function acidfree_import_image($node, $file) {
    $node = image_create_node_from($file->filepath, $node->title, '', $node->taxonomy);
    return $node;
}

function theme_acidfree_print_thumb_image(&$node, $parent=null) {
    $vid = acidfree_get_vocab_id();
    if (count($node->taxonomy[$vid]) > 1 && $parent) {
        $p = "pid={$parent->tid}";
    }
    $info = image_get_info(file_create_path($node->images['thumbnail']));

    $image = _acidfree_image_display($node, 'thumbnail', array('width' => $info['width'], 'height' => $info['height']));
    
    $h = $info['height'] + variable_get('acidfree_extra_length',12);
    $w = $info['width'] + variable_get('acidfree_extra_length',12);
    
    $path = "node/{$node->nid}";
    $overlay = l('', $path, array('title' => $node->title), $p, NULL, true, true);
    $image = l($image, $path, array('title' => $node->title), $p, NULL, true, true);
    $title = l($node->title, $path, array('title' => $node->title), $p, NULL, true, true);

    $imagediv = '<div class="acidfree-cell"><div class="acidfree-item acidfree-image">';
    $imagediv .= "<div class='acidfree-thumbnail' style='width: {$w}px; height: {$h}px;'>";
    $imagediv .= $image . '<div class="acidfree-overlay">' . $overlay . '</div></div>';
    $imagediv .= "<p>$title</p></div></div>";
    return $imagediv;
}

function _class_image_rotate(&$node) {
    if ($node->rotate != 0) {
        $large_path = file_create_path($node->images[IMAGE_ORIGINAL]);
        acidfree_rotate_image($large_path, $node->rotate);
        $filesize = filesize($large_path);
        db_query("UPDATE {files} SET filesize=%d WHERE filename='%s' AND nid=%d",
            $filesize, IMAGE_ORIGINAL, $node->nid);
        $node->rebuild_images = true;
        unset($node->rotate);
        image_update($node);
    }
}

function _class_image_form_alter($form_id, &$form) {
    $vid = acidfree_get_vocab_id();
    if (($arg = _path_match('node', 'add', 'image', '%d'))) {
        $album = acidfree_get_node_by_id($arg[0]);
        $form['taxonomy'][$vid]['#default_value'] = array($album->tid);
    }
    _acidfree_filter_taxonomy($form['taxonomy'][$vid]);
    $js_attrs = array('onchange' => 'set_title(this.value);');
    $form['image']['#attributes'] = is_array($form['image']['#attributes']) ?
        array_merge($js_attrs, $form['image']['#attributes']) : $js_attrs;
    $form['body']['#rows'] = 5;
    $rotate_options = Array(
            '270' => t('Counterclockwise'),
            '0' => t('No rotation'),
            '90' => t('Clockwise'),
            'auto'=>t('Use exif orientation tag'),
        );
    $form['rotate'] = array(
            '#type' => 'select',
            '#title' => t('Rotate image'),
            '#default_value' => '0',
            '#options' => $rotate_options,
            '#acidfree_form' => true,
            '#attributes' => array('class' => 'rotate'),
        );
    if (($arg = _path_match('album', '%d', 'contents'))) {
        unset($form['image']);
        unset($form['format']);
    }
}

function theme_acidfree_view_image($image) {
    // add some other divs for theming purposes
    $image = '<div class="acidfree-item acidfree-image">'.$image.'</div>';
    return $image;
}

function _class_image_view_alter(&$node) {
    if (isset($node->content)) {
        //dump_msg($node->content);
        $request = ($_GET['size']) ? $_GET['size'] : 'preview';
        $image = $node->content['image']['#value'];
        $size = _acidfree_get_sizes(IMAGE_ORIGINAL);
        // if we are not viewing the original, link to the original
        if (user_access('view original images') && ($node->images[$request] != $node->images[IMAGE_ORIGINAL]) && $size['link']) {
            if ($size['link'] == IMAGE_LINK_NEW) {
              $image = l($image, "image/view/{$node->nid}/_original", array('target' => '_blank'), null, null, null, true);
            }
            else {
              $image = l($image, "node/{$node->nid}", array(), 'size=_original', null, null, true);
            }
        }
        $node->content['image']['#value'] = theme('acidfree_view_image', $image);
    }
}
?>
