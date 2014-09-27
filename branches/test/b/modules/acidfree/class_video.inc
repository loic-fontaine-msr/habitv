<?php
/* $Id: class_video.inc,v 1.33.2.9 2008/10/31 14:28:55 vhmauery Exp $ */

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

/**
 * @file
 * The is the class implementation of the photo.  All the acidfree elements
 * must be defined in this same manner, which makes creating a new class
 * relatively simple -- copy one of the existing classes and modify the 
 * relevant functions.
 */

function class_video_register() {
    $class = new stdClass();
    if (!module_exists('video')) {
        return $class;
    }
    $class->class = 'video';
    $class->name = t('video');
    $class->addme = t('Add a video');
    $class->max_size = 4*(1 << 20);
    $class->mime_ext = Array(
            'video/x-msvideo'=>'avi',
            'video/mpeg'=>'mpg',
            'video/mp4'=>'mp4',
            'video/quicktime'=>'mov',
            'audio/x-pn-realaudio'=>'rm',
            'audio/x-realaudio'=>'rm',
            'application/vnd.rn-realmedia'=>'rm',
            'video/x-ms-wmv'=>'wmv',
            'video/x-ms-asf'=>'asf',
            'video/3gpp'=>'3gp',
            'video/x-flv'=>'flv',
            'application/x-flash-video'=>'flv',
            'flv-application/octet-stream'=>'flv',
        );
    $class->form_alter = '_class_video_form_alter';
    $class->nodeapi = '_class_video_nodeapi';
    $class->access = 'create video';
    $class->import = 'acidfree_import_video';
    return $class;
}

/* this function is called by _acidfree_node_from_file with
 * the corresponding $node structure filled out as well as
 * that function could do.  The $file structure is also passed
 * which is what the $node was built from.
 */
function acidfree_import_video($node, $file) {
    $node = video_upload_create_node_from($file, $node->title, '', $node->taxonomy);
    return $node;
}

function theme_acidfree_print_thumb_video(&$node, $parent=null) {
    $vid = acidfree_get_vocab_id();
    if (count($node->taxonomy[$vid]) > 1 && $parent) {
        $p = "pid={$parent->tid}";
    }
    $image_node = acidfree_get_node_by_id($node->iid);
    if ($image_node) {
        $info = image_get_info(file_create_path($image_node->images['thumbnail']));
    } else {
        $image_node = _class_video_fake_thumb();
        $info = $image_node->info;
    }

    $image = _acidfree_image_display($image_node, 'thumbnail', array('width' => $info['width'], 'height' => $info['height']));
    
    $h = $info['height'] + variable_get('acidfree_extra_length',12);
    $w = $info['width'] + variable_get('acidfree_extra_length',12);
    
    $overlay = l('', "node/{$node->nid}", array('title' => $node->title), $p, NULL, true, true);
    $image = l($image, "node/{$node->nid}", array('title' => $node->title), $p, NULL, true, true);
    $title = l($node->title, "node/{$node->nid}", array('title' => $node->title), $p, NULL, true, true);

    $videodiv = '<div class="acidfree-cell"><div class="acidfree-item acidfree-video">';
    $videodiv .= "<div class='acidfree-thumbnail' style='width: {$w}px; height: {$h}px;'>";
    $videodiv .= $image . '<div class="acidfree-overlay">' . $overlay . '</div></div>';
    $videodiv .= "<p>$title</p></div></div>";
    return $videodiv;
}

function _class_video_form_alter($form_id, &$form) {
    $form['taxonomy']['#weight'] = -19;
    $vid = acidfree_get_vocab_id();
    if (($arg = _path_match('node', 'add', 'video', '%d'))) {
        $album = acidfree_get_node_by_id($arg[0]);
        $form['taxonomy'][$vid]['#default_value'] = array($album->tid);
    }
    else if (($arg = _path_match('node', 'add', 'video', '%*s', '%d'))) {
        $album = acidfree_get_node_by_id($arg[0]);
        $form['taxonomy'][$vid]['#default_value'] = array($album->tid);
    }
    _acidfree_filter_taxonomy($form['taxonomy'][$vid]);
    unset($form['log']);
    if (_path_match('album', '%d', 'contents')) {
        $form['video']['#collapsible'] = true;
        $form['video']['#collapsed'] = true;
    }
    $form['body_filter']['body']['#rows'] = 5;
    $attributes = array('onchange' => 'set_title(this.value);');
    if (isset($form['video']['video_upload_file'])) {
        $form['video']['video_upload_file']['#attributes'] = 
            is_array($form['video']['video_upload_file']['#attributes']) ? 
            array_merge($attributes, $form['video']['video_upload_file']['#attributes']):$attributes;
    } else if(isset($form['video']['video_upload']['video_upload_file'])) {
        $form['video']['video_upload']['video_upload_file']['#attributes'] = 
            is_array($form['video']['video_upload']['video_upload_file']['#attributes']) ? 
            array_merge($attributes, $form['video']['video_upload']['video_upload_file']['#attributes']):$attributes;
    } else {
        //dump_msg($form);
    }
    if (($arg = _path_match('album', '%d', 'contents'))) {
        $form['body'] = $form['body_filter']['body'];
        unset($form['body_filter']);
        unset($form['video_upload_file']);
        unset($form['preview']);
        unset($form['image']);
    }

}

function _class_video_fake_thumb() {
    static $resized_thumb = false;
    $art_path = drupal_get_path('module', 'acidfree') . '/art/';
    if (!$resized_thumb) {
        $resized_thumb = true;
        $root = _nid_from_tid(acidfree_get_root());
        $out = db_fetch_object(db_query('SELECT * FROM {files} WHERE nid=%d AND filename=\'acidfree_nothumb.png\'', $root));
        if ($out) {
            $statinfo = @stat($out->filepath);
        }
        if (!$statinfo || $statinfo['mtime'] < variable_get('image_updated', 0)) {
            $infile = escapeshellarg($art_path . 'acidfree_nothumb.svg');
            $outfile = escapeshellarg($out->filepath);
            $size = _acidfree_get_sizes('thumbnail');
            $size = "{$size['width']}x{$size['height']}";
            exec("convert $infile -resize $size $outfile");
            $out->filesize = @filesize($out->filepath);
            if ($out->fid) {
                db_query("UPDATE {files} SET filesize=%d WHERE fid=%d",
                    $out->filesize, $out->fid);
            } else {
                $out->fid = db_next_id('{files}_fid');
                db_query("INSERT INTO {files} (fid,nid,filename,filepath,filemime,filesize) VALUES (%d,%d,'%s','%s','%s',%d)", $out->fid, $root, 'acidfree_nothumb.png', $out->filepath, 'image/png', $out->filesize);
            }
        }
    } else {
        $out = new stdClass();
        $out->filepath = file_create_path('acidfree_nothumb.png');
    }
    $img = new stdClass();
    $img->title = t('No thumbnail available');
    $img->path = $out->filepath;
    if (!file_exists($img->path)) {
        $img->path = $art_path . "nothumb.png";
    }
    $img->url = base_path() . $img->path;
    $size = _acidfree_get_sizes('thumbnail');
    $info = image_get_info($img->path);
    $ratio = 1;
    if ($info && $info['height'] && $info['width']) {
      $ratio = $info['height'] / $info['width'];
    }
    $size = _acidfree_get_sizes('thumbnail');
    $info['width'] = $size['width'];
    $info['height'] = $size['height'] * $ratio;
    $img->info = $info;
    return $img;
}


function theme_acidfree_view_video($node) {
    if (is_array($node->tempimage['fids']) && $node->tempimage['fids']['_original']) {
        // take care of previews on unsaved nodes
        $image_node = _video_image_temp_image_load(array_values($node->tempimage['fids']));
    } else {
        $image_node = acidfree_get_node_by_id($node->iid);
    }
    $size = 'preview';
    if (!$image_node->images[$size])
        $size = '_original';

    $info = image_get_info(file_create_path($image_node->images[$size]));

    $image = _acidfree_image_display($image_node, $size, array('width' => $info['width'], 'height' => $info['height']));
    
    $h = $info['height'] + variable_get('acidfree_extra_length',12);
    $w = $info['width'] + variable_get('acidfree_extra_length',12);
    
    $path = "node/{$node->nid}/play";
    $overlay = l('', $path, array('title' => $node->title), $p, NULL, true, true);
    $image = l($image, $path, array('title' => $node->title), $p, NULL, true, true);
    $title = l($node->title, $path, array('title' => $node->title), $p, NULL, true, true);

    $videodiv = '<div class="acidfree-item acidfree-video">';
    $videodiv .= "<div class='acidfree-preview' style='width: {$w}px; height: {$h}px;'>";
    $videodiv .= $image . '<div class="acidfree-overlay">' . $overlay . '</div></div>';
    $videodiv .= "<p>$title</p></div>";
    return $videodiv;
}

function _class_video_view_alter(&$node) {
    // dump_msg($node->content);
    if (isset($node->content['video_image_thumbnail']) && !variable_get('video_playinbody',0)) {
        $new_image = theme('acidfree_view_video', $node);
        $node->content['video_image_thumbnail']['#value'] = $new_image;
    }
}
?>
