<?php
// $Id: views_acidfree.inc,v 1.4.2.2 2007/06/19 14:36:44 vhmauery Exp $
/* ex: set syntax=php tabstop=4 expandtab shiftwidth=4 softtabstop=4: */

/**
 * This include file implements views functionality on behalf of acidfree.module
 */

function acidfree_views_default_views() {
	$acidfree_vocab_id = variable_get('acidfree_vocab_id', 0);
	$node_count = variable_get('acidfree_num_thumbs', 15);
    /* album grid view */
    $view = new stdClass();
    $view->name = 'album_grid_view';
    $view->disabled = false;
    $view->page = true;
    $view->page_title = t('Album grid');
    $view->url = 'node/$nid';
    $view->description = t('Provides a grid view of items in an album');
    $view->access = array (
            );
    $view->view_args_php = '';
	if ($node_count > 0) {
		$view->use_pager = true;
		$view->nodes_per_page = $node_count;
	}
    $view->sort = array (
            array (
                'tablename' => 'node',
                'field' => 'sticky',
                'sortorder' => 'DESC',
                'options' => '',
                ),
            array (
                'tablename' => 'node',
                'field' => 'nid',
                'sortorder' => 'DESC',
                'options' => '',
                ),
            );
    $view->argument = array (
            array (
                'type' => 'taxid',
                'argdefault' => '1',
                'title' => '',
                'options' => '',
                'wildcard' => '',
                'wildcard_substitution' => '',
                ),
            );
    $view->field = array (
            );
    $view->filter = array (
            array (
                'tablename' => 'term_data',
                'field' => 'vid',
                'operator' => 'OR',
                'options' => '',
                'value' => array($acidfree_vocab_id),
                ),
            array (
                'tablename' => 'node',
                'field' => 'distinct',
                'operator' => '=',
                'options' => '',
                'value' => array (
                    ),
                ),
            array (
                'tablename' => 'node',
                'field' => 'status',
                'operator' => '=',
                'options' => '',
                'value' => '1',
                ),
            );
    $view->exposed_filter = array (
            array (
                'tablename' => 'term_data',
                'field' => 'vid',
                'label' => '',
                'optional' => 0,
                'is_default' => 0,
                'operator' => 0,
                'single' => 0,
                ),
            array (
                'tablename' => 'node',
                'field' => 'distinct',
                'label' => '',
                'optional' => 0,
                'is_default' => 0,
                'operator' => 0,
                'single' => 0,
                ),
            array (
                'tablename' => 'node',
                'field' => 'status',
                'label' => '',
                'optional' => 0,
                'is_default' => 0,
                'operator' => 0,
                'single' => 0,
                ),
            );
    $view->requires = array(node, term_data);
    $views[$view->name] = $view;

    /* album list view */
    $view = new stdClass();
    $view->name = 'album_list_view';
    $view->disabled = false;
    $view->page = true;
    $view->page_title = t('Album list');
    $view->url = 'node/$nid';
    $view->description = 'Provides a list view of items in an album';
	if (isset($node_count)) {
		$view->use_pager = true;
		$view->nodes_per_page = $node_count;
	}
    $view->access = array (
            );
    $view->view_args_php = '';
    $view->sort = array (
            array (
                'tablename' => 'node',
                'field' => 'sticky',
                'sortorder' => 'DESC',
                'options' => '',
                ),
            array (
                'tablename' => 'node',
                'field' => 'nid',
                'sortorder' => 'DESC',
                'options' => '',
                ),
            );
    $view->argument = array (
            array (
                'type' => 'taxid',
                'argdefault' => '1',
                'title' => '',
                'options' => '',
                'wildcard' => '',
                'wildcard_substitution' => '',
                ),
            );
    $view->field = array (
            );
    $view->filter = array (
            array (
                'tablename' => 'term_data',
                'field' => 'vid',
                'operator' => 'OR',
                'options' => '',
                'value' => array($acidfree_vocab_id),
                ),
            array (
                'tablename' => 'node',
                'field' => 'distinct',
                'operator' => '=',
                'options' => '',
                'value' => array (
                    ),
                ),
            array (
                'tablename' => 'node',
                'field' => 'status',
                'operator' => '=',
                'options' => '',
                'value' => '1',
                ),
            );
    $view->exposed_filter = array (
            array (
                'tablename' => 'term_data',
                'field' => 'vid',
                'label' => '',
                'optional' => 0,
                'is_default' => 0,
                'operator' => 0,
                'single' => 0,
                ),
            array (
                'tablename' => 'node',
                'field' => 'distinct',
                'label' => '',
                'optional' => 0,
                'is_default' => 0,
                'operator' => 0,
                'single' => 0,
                ),
            array (
                'tablename' => 'node',
                'field' => 'status',
                'label' => '',
                'optional' => 0,
                'is_default' => 0,
                'operator' => 0,
                'single' => 0,
                ),
            );
    $view->requires = array(node, term_data);
    $views[$view->name] = $view;
    return $views;
}
