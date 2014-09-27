<?php
function barlow_regions() {
  return array(
    'left' => t('left sidebar'),
    'right' => t('right sidebar'),
    'before_content' => t('before content'),
    'content' => t('content'),
    'header' => t('header'),
    'footer' => t('footer')
    );
}

function  _phptemplate_variables($hook, $vars) {
  switch($hook) {
    case 'page': {
      drupal_add_css($vars['directory'] . '/layout.css', 'theme');
      drupal_add_css($vars['directory'] . '/typography.css', 'theme');
      $vars['styles'] = drupal_get_css();
    }
    case 'node':
      if (count($vars['node']->taxonomy)) {
        $vars['terms'] = t('Tags: !tags', array('!tags' => $vars['terms']));
      }
      if ($vars['submitted']) {
        $vars['author'] = t('By: !user', array('!user' => theme('username', $vars['node'])));
      }
      break;
  }
  return $vars;
}

/**
 * Sets the body-tag class attribute.
 *
 * Adds 'sidebar-left', 'sidebar-right' or 'sidebars' classes as needed.
 */
function phptemplate_body_class($sidebar_left, $sidebar_right) {
  if ($sidebar_left != '' && $sidebar_right != '') {
    $class = 'sidebars';
  }
  else {
    if ($sidebar_left != '') {
      $class = 'sidebar-left';
    }
    if ($sidebar_right != '') {
      $class = 'sidebar-right';
    }
  }

  if (isset($class)) {
    print ' class="'. $class .'"';
  }
}

/**
 * Return a themed breadcrumb trail.
 *
 * @param $breadcrumb
 *   An array containing the breadcrumb links.
 * @return a string containing the breadcrumb output.
 */
function phptemplate_breadcrumb($breadcrumb) {
  if (!empty($breadcrumb)) {
    return '<div class="breadcrumb">'. implode(' → ', $breadcrumb) .'</div>';
  }
}

function phptemplate_username($object) {

  if ($object->uid && $object->name) {
    // Shorten the name when it is too long or it will break many tables.
    if (drupal_strlen($object->name) > 20) {
      $name = drupal_substr($object->name, 0, 15) .'...';
    }
    else {
      $name = $object->name;
    }

    if (user_access('access user profiles')) {
      $output = l($name, 'user/'. $object->uid, array('title' => t('View user profile.')));
    }
    else {
      $output = check_plain($name);
    }
  }
  else if ($object->name) {
    // Sometimes modules display content composed by people who are
    // not registered members of the site (e.g. mailing list or news
    // aggregator modules). This clause enables modules to display
    // the true author of the content.
    if ($object->homepage) {
      $output = l($object->name, $object->homepage, array('class' => 'anonymous', 'title' => t('not verified')));
    }
    else {
      $output = '<span class="anonymous" title="'. t('not verified') .'">'. check_plain($object->name) .'</span>';
    }
  }
  else {
    $output = variable_get('anonymous', t('Anonymous'));
  }

  return $output;
} 

/**
 * Theme an image node teaser
 */
function phptemplate_image_teaser($node) {
 return l(image_display($node, 'thumbnail'), 'node/'. $node->nid, array('class' => 'image'), NULL, NULL, TRUE, TRUE) . $node->teaser;
}
