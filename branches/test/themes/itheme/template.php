<?php
drupal_add_js(drupal_get_path('theme', 'itheme') . '/js/jquery.cookie.js', 'theme');
drupal_add_js('cookiePath = "' . base_path() . '";', 'inline');
drupal_add_js(drupal_get_path('theme', 'itheme') . '/js/itheme.ui.js', 'theme');
?>