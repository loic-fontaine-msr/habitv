<?php

/**
 * Catch the theme_username function and link to the usernode instead of linking to the userpage
 *
 * @param $object
 *   The user object to format, usually returned from user_load().
 * @return
 *   A string containing an HTML link to the usernode if the passed object
 *   suggests that this is a site user. Otherwise, only the username is returned.
 */

function phptemplate_username($object) {

  if ($object->uid && $object->name) {
    // Shorten the name when it is too long or it will break many tables.
    if (drupal_strlen($object->name) > 20) {
      $name = drupal_substr($object->name, 0, 15) .'...';
    }
    else {
      $name = $object->name;
    }

    if (user_access('access content') && module_exists('usernode')) {
      $nid = usernode_get_node_id($object);
      $output = l($name, 'node/'. $nid, array('title' => t('View user details.')));
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
      $output = l($object->name, $object->homepage);
    }
    else {
      $output = check_plain($object->name);
    }

    $output .= ' ('. t('not verified') .')';
  }
  else {
    $output = variable_get('anonymous', 'Anonymous');
  }

  return $output;
}

/**
 * Catch the theme_user_picture function and link to the usernode instead of linking to the userpage
 */

function phptemplate_user_picture(&$account) {
  if (variable_get('user_pictures', 0)) {
    if ($account->picture && file_exists($account->picture)) {
      $picture = file_create_url($account->picture);
    }
    else if (variable_get('user_picture_default', '')) {
      $picture = variable_get('user_picture_default', '');
    }

    if (isset($picture)) {
      $alt = t("@user's picture", array('@user' => $account->name ? $account->name : variable_get('anonymous', 'Anonymous')));
      $picture = theme('image', $picture, $alt, $alt, '', false);
      if (!empty($account->uid) && user_access('access content') && module_exists('usernode')) {
        $nid = usernode_get_node_id($account);
        $picture = l($picture, "node/$nid", array('title' => t('View user details.')), NULL, NULL, FALSE, TRUE);
      }

      return "<div class=\"picture\">$picture</div>";
    }
  }
}
