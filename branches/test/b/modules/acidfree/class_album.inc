<?php
/* $Id: class_album.inc,v 1.41.2.23 2008/11/01 23:19:11 vhmauery Exp $ */
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
 * The is the class implementation of the album.  All the acidfree elements
 * must be defined in this same manner, which makes creating a new class
 * relatively simple -- copy one of the existing classes and modify the 
 * relevant functions.
 */

function class_album_register() {
    $class = new stdClass();
    $class->class = 'album';
    $class->name = t('album');
    $class->addme = t('Add an album');
    $class->mime_ext = Array();
    $class->form_alter = '_class_album_form_alter';
    $class->nodeapi = '_class_album_nodeapi';
    $class->access = 'create acidfree albums';
    $class->import = 'acidfree_import_album';
    return $class;
}

/* this function is called by _acidfree_node_from_file with
 * the corresponding $node structure filled out as well as
 * that function could do.  The $file structure is also passed
 * which is what the $node was built from.
 */
function acidfree_import_album($node, $file) {
    return acidfree_create_album($node->title, $node->taxonomy);
}

function acidfree_create_album($title, $taxonomy, $body='', $uid=null, $weight=null, $order=null, $view=null, $share=null) {
    $node = new stdClass();
    $node->type = 'acidfree';
    _acidfree_set_node_defaults($node);
    $node->created = $node->updated = time();
    $node->title = $title;
    $node->taxonomy = $taxonomy;
    $node->body = $body;
    if (isset($uid)) {
        $user = user_load(array('uid'=>$uid));
        if ($user) {
            $node->uid = $uid;
            $node->name = $user->name;
        }
    }
    if (isset($weight)) {
        $node->weight = $weight;
    }
    if (isset($order)) {
        $node->order_by = $order;
    }
    if (isset($view)) {
        $node->view = $view;
    }
    if (isset($share)) {
        $node->share = $share;
    }
    node_invoke_nodeapi($node, 'prepare');
    $node = node_submit($node);
    node_save($node);
    return $node;
}

function _acidfree_get_album_thumb(&$node) {
    if ($node->thumb) {
        $thumb = acidfree_get_node_by_id($node->thumb);
    } else {
        $child = _acidfree_get_children($node->tid, 1, -1, false, 'RAND()');

        if (!$child) {
            $child = _acidfree_get_children($node->tid, 1, -1, true, 'RAND()');
            if (!$child) {
                return '';
            }
        }
        $thumb = acidfree_get_node_by_id($child[0]);
    }
    if ($thumb->type == 'video') {
        if (isset($thumb->iid)) {
            $thumb = acidfree_get_node_by_id($thumb->iid);
            if (!$thumb) {
                $thumb = _class_video_fake_thumb();
            }
        } else {
            $thumb = _class_video_fake_thumb();
        }
    } else if ($thumb->type == 'acidfree') {
        return _acidfree_get_album_thumb($thumb);
    }
    return $thumb;
}

function _album_content_fields() {
    return variable_get('acidfree_content_fields', array(
            'title' => '2',
            'body' => '2',
            'taxonomy' => '1',
            'thumb' => '1',
            'rotate' => '1',
        )
    );
}

function _album_contents_field_number($field_name) {
    $fields = _album_content_fields();
    return $fields[$field_name];
}

function _album_contents_available_fields() {
    // get a node
    $node = new stdClass();
    $node->created = time() - 100;
    $form_alter_mods = module_implements('form_alter');
    $keys = array();
    $types = array('acidfree', 'image', 'video');
    foreach ($types as $type) {
        $node->type = $type;
        $f = node_form($node, $param);
        unset($f['submit']);
        unset($f['preview']);
        unset($f['delete']);
        foreach ($form_alter_mods as $module) {
            $function = $module .'_form_alter';
            $form_id = $node->type.'_node_form';
            $function($form_id, $f);
        }
        // hack for video's body_filter
        if ($node->type == 'video') {
            $f['body'] = $f['body_filter']['body'];
            $f['filter'] = $f['body_filter']['filter'];
            unset($f['body_filter']['body']);
            unset($f['body_filter']['filter']);
        }
        $form[$type] = $f;
        $kids[$type] = element_children($f);
    }
    $kids['common'] = array_intersect(
            $kids['acidfree'],
            $kids['image'],
            $kids['video']
        );
    foreach ($kids['common'] as $k) {
        $form['common'][$k] = $form['acidfree'][$k];
    }
    foreach ($types as $type) {
        $kids[$type] = array_diff($kids[$type], $kids['common']);
    }

    // check all the fields
    $acidfree_content_fields = _album_content_fields();
    $acidfree_content_fields_all = variable_get('acidfree_content_fields_all', true);
    $fields = array(
            '#type' => 'item',
            '#tree' => true,
            '#theme' => 'acidfree_settings_fields',
        );
    $types[] = 'common';
    $unwanted = array('body_filter', 'thumb');
    foreach ($types as $type) {
        foreach ($kids[$type] as $k) {
            if (in_array($k, $unwanted)) {
                continue;
            }
            if ($form[$type][$k]['#type'] != 'hidden' && $form[$type][$k]['#type'] != 'value') {
                if (!isset($form[$type][$k]['#title'])) {
                    $title = drupal_ucfirst($k);
                } else {
                    $title = $form[$type][$k]['#title'];
                }
                $fields[$k] = array(
                        '#af_type' => $type,
                        '#field_name' => $title,
                        '#type' => 'select',
                        '#options' => array(
                                0 => t('not shown'),
                                1 => t('left column'),
                                2 => t('right column'),
                            ),
                        '#default_value' => $acidfree_content_fields[$k],
                    );
            }
        }
    }
    // return an array of form elements
    return $fields;
}

/*
 * Prune out items that are not to be shown
 * on the album contents form (for simplicity sake)
 */
function _album_contents_prune_item(&$form) {
    $fields = _album_content_fields();
    $show_all = variable_get('acidfree_content_fields_all', true);
    $skip = array('checked', 'thumb');
    foreach (element_children($form) as $k) {
        if (in_array($k, $skip)) {
            continue;
        }
        if (!$show_all && !$fields[$k]) {
            if ($form[$k]['#type'] != 'hidden' && $form[$k]['#type'] != 'value') {
                unset($form[$k]);
            }
        }
    }
}

/* recursively flatten an array tree, so that all
 * that is left is $k => $v for $v is a leaf
 */
function _flatten_array($a) {
    $b = array();
    foreach ($a as $k => $v) {
        if (is_array($v)) {
            $b = array_merge($b, _flatten_array($v));
        } else {
            $b[$k] = $v;
        }
    }
    return $b;
}

function acidfree_album_contents_submit($form_id, $form) {
    $op = isset($_POST['op']) ? $_POST['op'] : '';
    //dump($form, true);
    $tid = $form['album'];
    global $acidfree_types;
    $targets = array();
    $vid = acidfree_get_vocab_id();
    $parent = $form['parent'][$vid];
    foreach ($form['nodes'] as $fnode) {
        $n = acidfree_get_node_by_id($fnode['nid']);
        // troublesome keys -- they use an array on the form
        // and then don't know how to handle it when it is passed back
        $trouble_keys = array('author','options','path','og_nodeapi', 'node_privacy_byrole');
        // this takes care of class extras and title
        if ($fnode['checked'] == '1') {
            // move, copy, delete the node
            switch ($op) {
            case t('Delete'):
                if ($n->type != 'acidfree' && count($fnode['taxonomy'][$vid]) > 1) {
                    // delete from this album only
                    $key = array_search($tid, $fnode['taxonomy'][$vid]);
                    unset($fnode['taxonomy'][$vid][$key]);
                    drupal_set_message(t("removed '%item' from album",
                            array('%item' => $n->title)));
                } else {
                    node_delete($n->nid);
                    unset($n);
                }
                break;
            case t('Copy'):
                if ($n->type != 'acidfree') {
                    // add dest->nid to parent list
                    $fnode['taxonomy'][$vid][$parent] = $parent;
                }
                break;
            case t('Move'):
                // replace node->nid with dest->nid in parent list
                if ($n->type != 'acidfree') {
                    $key = array_search($tid, $fnode['taxonomy'][$vid]);
                    unset($fnode['taxonomy'][$vid][$key]);
                    $fnode['taxonomy'][$vid][$parent] = $parent;
                } else {
                    $parents = _acidfree_get_ancestors(_album_from_tid($parent));
                    $awry = ($parent == $n->tid);
                    foreach ($parents as $p) {
                        if ($p->nid == $n->nid) {
                            $awry = true;
                            break;
                        }
                    }
                    if (!$awry) {
                        $fnode['taxonomy'][$vid][$parent] = $parent;
                    } else {
                        drupal_set_message(t("Could not set '%item' to be own ancestor album",
                                array('%item' => $n->title)), 'error');
                    }
                }
                break;
            }
        }
        if (isset($n)) {
            _acidfree_pause_messages(true);
            drupal_execute("{$n->type}_node_form", $fnode, $n);
            _acidfree_pause_messages();
        }
    }
    // not sure why, but even though we pass the nodes around as references,
    // somewhere things go awry and acidfree_get_node_by_id returns the 
    // old copy that is not the one we saved.  so we just reload.
    drupal_set_message(t('Changes saved.'));
    $pager = _acidfree_make_pager_query();
    $nid = _nid_from_tid($tid);
    drupal_goto("node/{$nid}/contents", $pager);
}

function theme_acidfree_album_contents($form) {
    if (count(element_children($form['nodes'])) == 0)
        return t('This album is empty')."<br/>";
    $output .= drupal_render($form['pager']);
    $output .= '<div class="container-inline" style="padding: 5px;">';
    foreach (element_children($form['actionstop']) as $key) {
        $output .= drupal_render($form['actionstop'][$key]) . '&nbsp;&nbsp;';
    }
    $output .= '</div>';

    $i=0;
    foreach (element_children($form['nodes']) as $child) {
        $row[] = Array('data' => drupal_render($form['nodes'][$child]['checked']));
        $node = $form['nodes'][$child]['#node'];
        $cell = theme("acidfree_print_thumb_{$node->type}", $node);
        $cell .= '<div class="block-bottom"></div>';
        foreach (array_keys(acidfree_form_elements($form['nodes'][$child])) as $key) {
            $cell .= drupal_render($form['nodes'][$child][$key]);
        }
        $row[] = Array( 'data' => $cell,
                'class' => 'album-cell');
        foreach (array(1, 2) as $col) {
            unset($cell);
            foreach (array_filter(element_children($form['nodes'][$child]),
                    create_function('$v', 
                        'return _album_contents_field_number($v) == '.$col.';'
                    )
                ) as $ck) {
                $cell .= drupal_render($form['nodes'][$child][$ck]);
            }
            $row[] = Array('data' => $cell, 'class' => 'album-cell');
        }
        if (variable_get('acidfree_content_fields_all', true)) {
            $rows[] = Array('data' => $row);
            unset($row);
            $extra['extras'] = array('#type' => 'fieldset', '#title' => t('Other fields'), '#collapsible' => true, '#collapsed' => true);
            $extra['extras']['fields'] = array('#value' => drupal_render($form['nodes'][$child]));
            $cell = drupal_render($extra['extras']);
            $row[] = Array('data' => $cell, 'class' => 'album-cell', 'colspan' => 4);
        } else {
            $cell = drupal_render($form['nodes'][$child]);
            $row[] = Array('data' => $cell, 'class' => 'album-cell');
        }
        $rows[] = Array('data' => $row);
        unset($row);
    }
    $output .= theme('table', NULL, $rows);
    $output .= '<div class="container-inline" style="padding: 5px;">';
    foreach (element_children($form['actionsbot']) as $key) {
        $output .= drupal_render($form['actionsbot'][$key]) . '&nbsp;&nbsp;';
    }
    $output .= '</div>';
//    $output .= '<pre>'; $output .= dump($form); $output .= '</pre>';
    return $output.drupal_render($form);
}

function acidfree_album_contents(&$term) {
    $vid = acidfree_get_vocab_id();
    $form = array();
    $term = taxonomy_get_term($term);
    drupal_set_title(t('%title Contents', array('%title' => $term->name)));
    drupal_set_breadcrumb($bc = acidfree_make_breadcrumbs($term));
    
    $nodes_per_page = min(15, variable_get('acidfree_num_thumbs', 15));
    if ($nodes_per_page <= 0) {
      $nodes_per_page = 15;
    }
    
    $pnode = _album_from_tid($term->tid);
    $pager_sort = $pnode->order_by;
    if (!$pager_sort || $pager_sort == '<default>') {
        $pager_sort = variable_get('acidfree_order', 'node.nid DESC');
    }
    $pager_sort = _acidfree_content_sort_clause($pnode);
    $nodes = _acidfree_get_children($term->tid, $nodes_per_page, ALBUM_PAGER, true, $pager_sort);
    if (count($nodes) == 0)
        return array('empty' => array('#value' => t("This album is empty")."<br/>"));
    $form['album'] = array(
            '#type' => 'hidden',
            '#value' => $term->tid,
        );
    $form['pager'] = array(
            '#value' => theme('pager', NULL, $nodes_per_page, ALBUM_PAGER),
        );
    $form['actionstop']['select'] = array(
            '#type' => 'select',
            '#default_value' => '0',
            '#options' => array('0'=>' -- ', 'all'=>t('All'), 'none'=>t('None'), 'invert'=>t('Invert')),
            '#attributes' => array('onchange' => 'select_nodes(this);'),
            '#prefix' => '<div class="form-item"><label for="edit-select">Select: </label></div>',
        );
    $form['actionstop']['parent'] = _acidfree_parent_select($term);
    $form['actionstop']['parent'][$vid]['#attributes'] = array('onchange' => 'update_parent_selects(this);');
    _acidfree_filter_taxonomy($form['actionstop']['parent'][$vid]);
    $form['actionstop']['parent'][$vid]['#title'] = t('Destination');
    $form['actionstop']['move'] = array(
            '#type' => 'submit',
            '#value' =>t('Move'),
        );
    $form['actionstop']['copy'] = array(
            '#type' => 'submit',
            '#value' =>t('Copy'),
        );
    $form['actionstop']['delete'] = array(
            '#type' => 'submit',
            '#value' =>t('Delete'),
        );
    $form['actionstop']['submit'] = array(
            '#type' => 'submit',
            '#value' =>t('Submit'),
        );
    $form['nodes']['#tree'] = true;
    $i = 0;
    $form_alter_mods = module_implements('form_alter');
    foreach ($nodes as $child) {
        $child = acidfree_get_node_by_id($child);
        $form['nodes'][$i] = node_form($child, $param);
        unset($form['nodes'][$i]['submit']);
        unset($form['nodes'][$i]['preview']);
        unset($form['nodes'][$i]['delete']);
        unset($form['nodes'][$i]['#after_build']);
        $form['nodes'][$i]['type'] = array('#value' => $child->type);
        $form['nodes'][$i]['checked'] = array(
                '#type' => 'checkbox',
                '#default_value' => 0,
                '#tree' => true,
            );
        $form['nodes'][$i]['nid'] = array(
                '#type' => 'hidden',
                '#value' => $child->nid,
                '#tree' => true,
            );
        $form['nodes'][$i]['#tree'] = true;
        $form['nodes'][$i]['#node'] = $child;
        foreach ($form_alter_mods as $module) {
            $function = $module .'_form_alter';
            $form_id = $child->type.'_node_form';
            $function($form_id, $form['nodes'][$i]);
        }
        // hack to un-force node.module to validate $form -- only $form['nodes'][$i]
        // needs to be validated.  /me curses album contents...
        unset($form['nodes'][$i]['#validate']['node_form_validate']);
        // hack to make video behave properly
        if ($child->type == 'video') {
            $form['nodes'][$i]['body'] = $form['nodes'][$i]['body_filter']['body'];
            $form['nodes'][$i]['filter'] = $form['nodes'][$i]['body_filter']['filter'];
            unset($form['nodes'][$i]['body_filter']['body']);
            unset($form['nodes'][$i]['body_filter']['filter']);
        }
        if (isset($form['nodes'][$i]['comment_settings'])) {
            $form['nodes'][$i]['comment_settings']['comment']['#parents'] = array('nodes', "$i", 'comment');
        }
        //dump_msg($form['nodes'][$i]);
        unset($form['nodes'][$i]['type']);
        _album_contents_prune_item($form['nodes'][$i]);
        $i++;
        continue;
    }
    $form['actionsbot'] = $form['actionstop'];
    return $form;
}

/**
 * This views generates the customized acidfree views based on the standard view
 *
 * @param $view_name
 *   The name or id of the view to load
 * @param $args
 *   The arguments from the end of the url. Usually passed from the menu system.
 *
 * @return
 *   The view page.
 */
function acidfree_album_view() {
    $args = func_get_args();
    $view_name = array_shift($args);
    $view = views_get_view($view_name);
    if (!$view) {
        //drupal_set_message("view $viewname not found");
        drupal_not_found();
        exit;
    }

    $album = _album_from_tid($args[0]);
    $album_sort = _acidfree_content_sort_array($album);
    for ($i=0; $i<count($album_sort); $i++) {
        $item = $album_sort[$i];
        $key = array_keys($item);
        $val = array_values($item);
        $view->sort[$i+1] = array(
                'vid' => $view->vid,
                'position' => 1+$i,
                'field' => 'node.'.$key[0],
                'sortorder' => $val[0],
                'options' => NULL,
                'tablename' => 'node',
                'id' => 'node.'.$key[0],
            );
    }

    $view->debug = true;

    if (($node_count = variable_get('acidfree_num_thumbs', 15)) <= 0) {
        $view->use_pager = false;
        unset($view->nodes_per_page);
    } else {
        $view->use_pager = true;
		$view->nodes_per_page = $node_count;
    }

    $output = views_build_view('page', $view, $args, $view->use_pager, $view->nodes_per_page);
    if ($output === FALSE) {
        drupal_not_found();
        exit;
    }
    
    $size = _acidfree_get_sizes('thumbnail');
    // 5px padding + 1px border on both sides of image +
    // 5px padding and 2px border on both sides of acidfree-item
    // gives 26. Height needs more because it contains text underneath.
    $style = '<style type="text/css" media="all">';
    $style .= '.acidfree-cell{width:' . ($size['width'] + 26) . 'px;}';
    $style .= '.acidfree .acidfree-cell{height:' . ($size['height'] + 45) . 'px;}';
    $style .= '</style>';
    drupal_set_html_head($style);

    return $output;
}

function theme_views_view_album_grid_view($view, $type, $nodes, $teasers = false, $links = true) {
    $term = taxonomy_get_term($view->args[0]);
    drupal_set_breadcrumb(acidfree_make_breadcrumbs($term));
    $offset = _acidfree_get_pager_offset_from_path(ALBUM_PAGER);
    if ($offset) {
        $offset = " - ".t('Page !page', array('!page' => "".($offset+1)));
    }
    drupal_set_title($term->name.$offset);
    if (count($nodes) == 0) {
        return "<p>".t('This album is empty')."</p>";
    }
    $output = "\n<p class=\"acidfree-center\">".check_markup($term->description)."</p>\n";
    $output .= theme('pager', NULL, 1, ALBUM_PAGER);
    $output .= '<div class="clear-block acidfree">' . "\n";
    foreach ($nodes as $node) {
        $node = node_load(array('nid' => $node->nid));
        $output .= theme("acidfree_print_thumb_{$node->type}", $node, $term)."\n";
    }
    $output .= "</div>\n";
    return $output;
}

function theme_views_view_album_list_view($view, $type, $nodes, $teasers = false, $links = true) {
    $term = taxonomy_get_term($view->args[0]);
    drupal_set_breadcrumb(acidfree_make_breadcrumbs($term));
    $offset = _acidfree_get_pager_offset_from_path(ALBUM_PAGER);
    if ($offset) {
        $offset = " - ".t('Page !page', array('!page' => "".($offset+1)));
    }
    drupal_set_title($term->name.$offset);
    if (count($nodes) == 0) {
        return "<p>".t('This album is empty')."</p>";
    }
    $output = "\n<p class=\"acidfree-center\">".check_markup($term->description)."</p>\n";
    $output .= theme('pager', NULL, 1, ALBUM_PAGER);
    $output .= "\n<div class='acidfree-list'>\n";
    
    $nodes_per_page = variable_get('acidfree_num_thumbs', 15);
    $i = _acidfree_get_pager_offset_from_path(ALBUM_PAGER) * $nodes_per_page;
    foreach ($nodes as $node) {
        $node = node_load(array('nid' => $node->nid));
        $row_class = "acidfree-list-".($i & 0x1 ? "even" : "odd");
        $output .= '<div class="clear-block acidfree-list-row ' . $row_class . '">';
        $output .= '<div class="acidfree-list-number">'.(++$i)."</div>";
        $output .= theme("acidfree_print_thumb_{$node->type}", $node, $term);
        $output .= '<div class="acidfree-list-body">'.check_markup($node->body)."</div>";
        $output .= "</div>\n";
    }
    $output .= "</div>";
    return $output;
}

function theme_acidfree_print_thumb_acidfree(&$node) {
    $album_info = _album_get_info($node);
    $image_node = _acidfree_get_album_thumb($node);

    if (is_object($image_node) && !$image_node->images) {
        $info = $image_node->info;
        $image = _acidfree_image_display($image_node, 'thumbnail', array('id' => "acidfree-thumb{$node->nid}", 'title' => $album_info, 'width' => $info['width'], 'height' => $info['height']));
    }
    else if ($image_node != '') {
        $info = image_get_info(file_create_path($image_node->images['thumbnail']));
        $image = _acidfree_image_display($image_node, 'thumbnail', array('id' => "acidfree-thumb{$node->nid}", 'title' => $album_info, 'width' => $info['width'], 'height' => $info['height']));
    }
    else {
        $info = _acidfree_get_sizes('thumbnail');
        $info['height'] = $info['width'] * 3/4;
        $image = '<div class="acidfree-empty">'.t('Empty').'</div>';
    }

    $info['height'] += variable_get('acidfree_extra_length',12);
    $info['width'] += variable_get('acidfree_extra_length',12);

    $overlay = l('', "node/{$node->nid}", array('title' => $album_info), NULL, NULL, true, true);
    $image = l($image, "node/{$node->nid}", array('title' => $album_info), NULL, NULL, true, true);
    $title = l($node->title, "node/{$node->nid}", array('title' => $album_info), NULL, NULL, true, true);

    $thumbnail = '<div class="acidfree-thumbnail" style="width: ' . $info['width'] . 'px; height: ' . $info['height'] . 'px;">';
    $thumbnail .= $image . '<div class="acidfree-overlay">' . $overlay . '</div></div>';
    $folderdiv = '<div class="acidfree-item acidfree-folder">' . $thumbnail . "<p>$title</p></div>";
    return '<div class="acidfree-cell">' . $folderdiv . "</div>";
}

function _album_get_info(&$node) {
    $albums = count(taxonomy_get_tree(acidfree_get_vocab_id(), $node->tid));
    $children = taxonomy_term_count_nodes($node->tid);
    if ($children == 0) {
        return $node->title . ": " . t('Empty');
     }
    // album nodes count as children too, so get rid of those
    $children -= $albums;
    if ($children > 0) {
        $tag[] = "$children  " . ($children == 1 ? t('Item') : t('Items'));
    }
    if ($albums > 0) {
        $tag[] = "$albums  " . ($albums == 1 ? t('Albums') : t('Albums'));
    }
    // this entire string gets check_plain'd so no need to check the title
    return $node->title . ": " . implode(' & ', $tag);
}

function _album_from_tid($tid) {
    $nid = _nid_from_tid($tid);
    if (!$nid) {
        return null;
    }
    return acidfree_get_node_by_id($nid);
}

function _nid_from_tid($tid) {
    $nid = db_result(db_query("SELECT aid FROM {acidfree_album} WHERE tid=%d", $tid));
    return $nid;
}

function _class_album_alter_parent(&$form) {
    //dump_msg($form['parent']);
    $form['parent']['#title'] = t('Parent album');
    $form['parent']['#multiple'] = 0;
    $form['parent']['#size'] = 0;
    $form['parent']['#required'] = 1;
    if (($arg = _path_match('node', 'add', 'acidfree', '%d'))) {
        $album = acidfree_get_node_by_id($arg[0]);
        $form['taxonomy'][$vid]['#default_value'] = array($album->tid);
    }
    unset($form['parent']['#description']);
    if (is_array($form['parent']['#options'])) {
        foreach ($form['parent']['#options'] as $key => $value) {
            if ($value == '<'. t('root') .'>' && count($form['parent']['#default_value'])) {
                unset($form['parent']['#options'][$key]);
                continue;
            }
            if (is_array($value)) {
            }
            if (user_access('can upload to any album')) {
                continue;
            }
            $node = _album_from_tid($key);
            if ($node->share) {
                continue;
            }
            if (!node_access($node)) {
                unset($form['parent']['#options'][$key]);
                continue;
            }
        }
    }
}

function _class_album_form_alter($form_id, &$form) {
    $vid = acidfree_get_vocab_id();
    if (isset($form['taxonomy'])) {
        $form['taxonomy'][$vid] = $form['parent'][$vid];
    } else {
        // special case the root album
        // it uses a hidden field rather than a select box
        if (isset($form['parent']['#value'])) {
            $form['parent'] = array($vid=>$form['parent']);
        }
        $form['taxonomy'] = $form['parent'];
    }
    unset($form['parent']);
    _acidfree_filter_taxonomy($form['taxonomy'][$vid]);
}
?>
