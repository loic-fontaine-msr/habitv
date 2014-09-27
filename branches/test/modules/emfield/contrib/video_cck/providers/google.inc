<?php
// $Id: google.inc,v 1.5.2.3 2008/02/19 16:52:27 aaron Exp $

define('VIDEO_CCK_GOOGLE_MAIN_URL', 'http://video.google.com/');
define('VIDEO_CCK_GOOGLE_XML', 'http://video.google.com/videofeed');
define('VIDEO_CCK_GOOGLE_DOMAIN_DEFAULT', 'com');

function video_cck_google_info() {
  $name = t('Google');
  $features = array(
    array(t('Autoplay'), t('Yes'), ''),
    array(t('RSS Attachment'), t('No'), ''),
    array(t('Thumbnails'), t('Yes'), ''),
  );
  return array(
    'provider' => 'google',
    'name' => $name,
    'url' => VIDEO_CCK_GOOGLE_MAIN_URL,
    'settings_description' => t('These settings specifically affect videos displayed from !google.', array('!google' => l($name, VIDEO_CCK_GOOGLE_MAIN_URL, array('target' => '_blank')))),
    'supported_features' => $features,
  );
}

function video_cck_google_settings() {
  $form = array();
  $form['video_cck_google_domain'] = array(
    '#type' => 'textfield',
    '#title' => t('Google video domain'),
    '#default_value' => variable_get('video_cck_google_domain', VIDEO_CCK_GOOGLE_DOMAIN_DEFAULT),
    '#description' => t('Google hosts their videos on various servers throughout the world. By default, videos from Google will be displayed from the United States, at their google.<b><em>com</em></b> servers. If you wish to display the video from another Google server, please enter the domain here, without the initial dot. For instance, you might enter <b><em>it</em></b> for the Italian Google servers at google.it, or <b><em>ca</em></b> for their Canadian servers.'),
  );
  return $form;
}

function video_cck_google_extract($embed) {
  return array(
    '@http://video\.google\.[a-z]{2,4}/videoplay\?docid=([^\&]*)\&@i',
    '@http://video\.google\.[a-z]{2,4}/videoplay\?docid=(.*)@i',
    '@http://video\.google\.[a-z]{2,4}/googleplayer\.swf\?docId=(-?\d*)@i',
  );
}

function video_cck_google_request($embed, $cacheable = TRUE) {
  $args = array('docid' => $embed);
  return module_invoke('emfield', 'request_xml', 'google', VIDEO_CCK_GOOGLE_XML, $args, $cacheable);
}

function video_cck_google_video_link($video_code) {
  return 'http://video.google.' . variable_get('video_cck_google_domain', VIDEO_CCK_GOOGLE_DOMAIN_DEFAULT) . '/videoplay?docid=' . $video_code;
}

function theme_video_cck_google_flash($embed, $width, $height, $autoplay) {
  if ($embed) {
    $autoplay = $autoplay ? '&autoPlay=true' : '';
    // this will be executed by not Internet Explorer browsers
    $output = '<!--[if !IE]> <-->
  <object type="application/x-shockwave-flash" width="'. $width .'" height="'. $height .'"
  data="http://video.google.' . variable_get('video_cck_google_domain', VIDEO_CCK_GOOGLE_DOMAIN_DEFAULT) . '/googleplayer.swf?docId='. check_plain($embed) . $autoplay . '">
  <!--> <![endif]-->' . "\n";

    // this will be executed by Internet Explorer
    $output .= '<!--[if IE]>
  <object type="application/x-shockwave-flash" width="'. $width .'" height="'. $height .'"
  classid="clsid:D27CDB6E-AE6D-11cf-96B8-444553540000"
  codebase="http://download.macromedia.com/pub/shockwave/cabs/flash/swflash.cab#version=6,0,40,0">
  <![endif]-->' . "\n";

    // params will be passed to both IE or not IE browsers
    $output .= '<param name="movie" value="http://video.google.' . variable_get('video_cck_google_domain', VIDEO_CCK_GOOGLE_DOMAIN_DEFAULT) . '/googleplayer.swf?docId=' . check_plain($embed) . $autoplay . '" />' . "\n";
    // following a list of params simply copied from old embed tag params. I don't know if this are really needed.
    $output .= '<param name="quality" value="best" />
    <param name="bgcolor" value="#ffffff" />
    <param name="allowScriptAccess" value="sameDomain" />
    <param name="scale" value="noScale" />
    <param name="wmode" value="transparent" />
    <param name="salign" value="TL" />
    <param name="FlashVars" value="playerMode=embedded" />
    <p>'. t('Your browser is not able to display this multimedia content.') .'</p>
  </object>';
  }
  return $output;
}

function video_cck_google_thumbnail($field, $item, $formatter, $node, $width, $height) {
  $xml = video_cck_google_request($item['value']);

  // we *should* be able to use media:thumbnail
  // but unfortunately, that is stripped out from the request hook
  // so instead, we'll parse it from the description, where it's repeated.
  // TODO: look into how to fix this...
  $desc = $xml['ITEM']['DESCRIPTION'][0];
  $regex = '@<img src="([^"]*)"@';
  if (preg_match($regex, $desc, $matches)) {
    return $matches[1];
  }
  return NULL;
}

function video_cck_google_video($embed, $width, $height, $field, $item, $autoplay) {
  $output = theme('video_cck_google_flash', $embed, $width, $height, $autoplay);
  return $output;
}

function video_cck_google_preview($embed, $width, $height, $field, $item, $autoplay) {
  $output = theme('video_cck_google_flash', $embed, $width, $height, $autoplay);
  return $output;
}
