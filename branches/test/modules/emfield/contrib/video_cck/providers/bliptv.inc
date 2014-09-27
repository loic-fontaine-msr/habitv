<?php
// $Id: bliptv.inc,v 1.6.2.3 2008/02/19 16:52:27 aaron Exp $
define('VIDEO_CCK_BLIPTV_MAIN_URL', 'http://blip.tv/');
define('VIDEO_CCK_BLIPTV_DEFAULT_RSS_TYPE', 'flv');

function video_cck_bliptv_info() {
  $name = t('Blip.tv');
  $features = array(
    array(t('Autoplay'), t('Yes'), ''),
    array(t('RSS attachment'), t('Yes'), t('When displaying in an RSS feed, these videos will be attached to the feed. (The feed reader or aggregator must support enclosure tags.)')),
    array(t('Thumbnails'), t('Yes'), ''),
  );
  return array(
    'provider' => 'bliptv',
    'name' => $name,
    'url' => VIDEO_CCK_BLIPTV_MAIN_URL,
    'settings_description' => t('These settings specifically affect videos displayed from !provider.', array('!provider' => l($name, VIDEO_CCK_BLIPTV_MAIN_URL, array('target' => '_blank')))),
    'supported_features' => $features,
  );
}

function video_cck_bliptv_settings() {
  $tags = array('none' => t('No video'), 'flv' => t('Flash Video'), 'override' => t('Override tag'), 'web' => t('Web'), 'Web High Resolution' => t('Web High Resolution'), 'Portable (iPod)' => t('Portable (iPod)'), 'Portable (other)' => t('Portable (other)'), 'Television' => t('Television'), 'HDTV' => t('HDTV'), 'Cell Phone' => t('Cell Phone'), 'Source' => t('Source'));
  $formats = array('none' => t('No video'), 'flv' => t('Flash video (flv)'), 'mov' => t('Quicktime Movie (mov)'), 'mp3' => t('MP3'), 'm4a' => t('m4a'), 'mpg' => t('MPEG'), 'mp4' => t('mp4'), 'm4v' => t('m4v'), 'wmv' => t('Windows Media (wmv)'));

  $form = array();
  $form['video_formats'] = array(
    '#type' => 'fieldset',
    '#title' => t('Video formats'),
    '#description' => t("Blip.TV allows users to upload videos of various formats, sizes, and qualities. These options will allow the administrator to choose the default video formats to display. Blip.TV uses 'tags' to determine this, such as 'Web' for a standard web resolution, and 'HDTV' for a high definition. If you select a tag, then the video format provided by Blip.TV for that tag will be displayed. If you select 'Override tag', then you may specify that the first available video of the given format will be displayed instead, which may or may not have your desired resolution. If a tag or format is not available for a specific video, the module will display the Flash video format instead, which is always present."),
    '#collapsible' => true,
    '#collapsed' => false,
  );
  $form['video_formats']['rss'] = array(
    '#type' => 'fieldset',
    '#title' => t('RSS'),
    '#description' => t("This will determine the preferred video format for RSS feeds."),
    '#collapsible' => true,
    '#collapsed' => true,
  );

  $form['video_formats']['rss']['video_cck_bliptv_rss_tag'] = array(
    '#type' => 'select',
    '#title' => t('RSS full page tag'),
    '#description' => t("When RSS feeds are displayed, and this tag is available for a video, then the clip for that tag will be displayed as an enclosure tag in the feed. Select 'Override tag' if you wish to control the format displayed. Select 'No video' if you do not wish a video to be displayed in RSS feeds."),
    '#options' => $tags,
    '#default_value' => variable_get('video_cck_bliptv_rss_tag', array(VIDEO_CCK_BLIPTV_DEFAULT_RSS_TYPE => VIDEO_CCK_BLIPTV_DEFAULT_RSS_TYPE)),
  );
  $form['video_formats']['rss']['video_cck_bliptv_rss_format'] = array(
    '#type' => 'select',
    '#title' => t('RSS full page format override'),
    '#description' => t("When RSS feeds are displayed, and the 'Override tag' option is selected above, then this format will be displayed as an enclosure tag in the feed."),
    '#options' => $formats,
    '#default_value' => variable_get('video_cck_bliptv_rss_format', array(VIDEO_CCK_BLIPTV_DEFAULT_RSS_TYPE => VIDEO_CCK_BLIPTV_DEFAULT_RSS_TYPE)),
  );
  return $form;
}

function video_cck_bliptv_data($field, $item) {
  $data = array();

  // we added new data fields to keep from having to reload the api/rss
  // because the data is only stored on update/insert, however, we have to know which data type we're using
  // this will just be an integer, increased when we make a change to data
  $data['video_cck_bliptv_data_version'] = 1;

  // use the page id, since we'll have that in most cases (except in embed pastes, which gets parsed during extraction)
  // we use this to get an rss feed w/ all the info for the video. interesting reading ;)
  // some of our data we'll get from the api, like available files
  $rss = video_cck_bliptv_request($item['value'], TRUE, 'rss');
  $api = video_cck_bliptv_request($item['value'], TRUE, 'api');
/*print 'rss: ';
print_r($rss);
print "\n\n<br />\napi: ";
print_r($api);*/
  // get our thumbnail url
  $data['thumbnail']['url'] = $rss['ITEM']['MEDIA:THUMBNAIL'][1]['URL'];

  // this gets sent to the player
  foreach ((array)$api['MEDIA']['LINK'] as $x => $link) {
    $x--;
    $video_type = '';
    switch ($link['TYPE']) {
      case 'video/x-flv':
        $video_type = 'flv';
        break;
      case 'video/x-m4v':
        $video_type = 'm4v';
        break;
      case 'video/quicktime':
        $video_type = 'mov';
        break;
    }
    if ($video_type) {
      $video_info = array();
      $video_info['url'] = $link['HREF'];
      $video_info['width'] = $api['MEDIA']['WIDTH'][$x];
      $video_info['height'] = $api['MEDIA']['HEIGHT'][$x];
      $video_info['duration'] = $api['MEDIA']['DURATION'][$x];
      $video_info['size'] = $api['MEDIA']['SIZE'][$x];
      $video_info['mime'] = $link['TYPE'];

      // we only store the last video of a particular type, for instance if two roles use .mov
      $data[$video_type] = $video_info;

      // however, we store the whole thing under role, so the information is still saved
      // but our arrays may be out of synch...
      $y = $x + 1;
      if ($api['MEDIA']['ROLE'][$y]) {
        $data[$video_type]['role'] = $api['MEDIA']['ROLE'][$y];
        $data[$api['MEDIA']['ROLE'][$y]] = $video_info;
        $data[$api['MEDIA']['ROLE'][$y]]['role'] = $api['MEDIA']['ROLE'][$y];
      }
    }
  }
  if (!$data['flv']) {
    $data['flv'] = array();
  }
  if (!$data['flv']['url']) {
    $data['flv']['url'] = $rss['ITEM']['ENCLOSURE'][1]['URL'];
  }
  $data['title'] = $api['en']['TITLE'][0] ? $api['en']['TITLE'][0] : $rss['2.0']['CHANNEL']['TITLE'][0];
  $data['description'] = $api['en']['DESCRIPTION'][0] ? $api['en']['DESCRIPTION'][0] : $rss['ITEM']['BLIP:PUREDESCRIPTION'][0];
  $data['blip_user']['uid'] = $api['CREATEDBY']['UID'][0] ? $api['CREATEDBY']['UID'][0] : $rss['ITEM']['BLIP:USERID'][0];
  $data['blip_user']['username'] = $api['CREATEDBY']['LOGIN'][0] ? $api['CREATEDBY']['LOGIN'][0] : $rss['ITEM']['BLIP:USER'][0];
  $data['blip_user']['url'] = $api['CREATEDBY']['LINKS']['LINK'][1] ? $api['CREATEDBY']['LINKS']['LINK'][1] : 'http://blip.tv/users/view/' . $data['blip_user']['username'];

  foreach ((array)$api['en']['LINKS']['LINK'] as $link) {
    if ($link['TYPE'] == 'text/html') {
      $data['post_id'] = $link['HREF'];
      break;
    }
  }
  if (!$data['post_id']) {
    // this is the code actually used by the player, even though it's different than for the page
    $data['post_id'] = $rss['ITEM']['BLIP:POSTS_ID'][0];
  }

  return $data;
}

function video_cck_bliptv_request($code, $cacheable = TRUE, $skin = 'rss') {
  $args = array();
  return module_invoke('emfield', 'request_xml', 'bliptv', "http://blip.tv/file/$code?skin=" . $skin, $args, $cacheable, false, $code . ':' . $skin);
}

function video_cck_bliptv_extract($embed) {
  // http://blip.tv/file/177952
  // http://blip.tv/file/177952/

  // http://blip.tv/file/177952?skin=rss

  // this gets all convoluted, because the file code is different than the video post id, which is different than the video filename,
  // and they don't all show up in the same places :(
  //<center>                              <script type="text/javascript" src="http://blip.tv/scripts/pokkariPlayer.js"></script><script type="text/javascript" src="http://blip.tv/syndication/write_player?skin=js&posts_id=182146&source=3&autoplay=true&file_type=flv&player_width=&player_height="></script><div id="blip_movie_content_182146"><a href="http://blip.tv/file/get/Make-WhatDoYouMake201.flv" onclick="play_blip_movie_182146(); return false;"><img src="http://blip.tv/file/get/Make-WhatDoYouMake201.flv.jpg" border="0" title="Click To Play" /></a><br /><a href="http://blip.tv/file/get/Make-WhatDoYouMake201.flv" onclick="play_blip_movie_182146(); return false;">Click To Play</a></div>                   </center>
  if ($embed && preg_match('@http://blip\.tv/syndication/write_player\?skin=js\&posts_id=([^\&]*)\&@i', $embed, $matches)) {
    $match = $matches[1];
    unset($matches);
    $result = drupal_http_request("http://blip.tv/syndication/write_player/?posts_id=$match&skin=js");
    if (preg_match('@setPermalinkUrl\(\"http://blip.tv/file/view/([^/\?\"]*)[/\?\"]@i', $result->data, $matches)) {
      $code = $matches[1];
    }
  }
  else if ($embed && preg_match('@blip\.tv/file/view/([^\?]*)[/\?]@i', $embed, $matches)) {
    $code = $matches[1];
  }
  else if ($embed && preg_match('@blip\.tv/file/view/(.*)@i', $embed, $matches)) {
    $code = $matches[1];
  }
  else if ($embed && preg_match('@blip\.tv/file/([^/\?]*)[/\?]@i', $embed, $matches)) {
    $code = $matches[1];
  }
  else if ($embed && preg_match('@blip\.tv/file/(.*)@i', $embed, $matches)) {
    $code = $matches[1];
  }
  if ($code) {
    return $code;
  }
  return array();
}

function video_cck_bliptv_video_link($video_code) {
  return 'http://blip.tv/file/' . $video_code;
}

function _video_cck_bliptv_get_video_info_from_preferred_tag($data, $var) {
  $preferred_tags = (array)variable_get($var, array(VIDEO_CCK_BLIPTV_DEFAULT_RSS_TYPE => VIDEO_CCK_BLIPTV_DEFAULT_RSS_TYPE));
// print_r($var);
  foreach($preferred_tags as $test_tag) {
    // if we override the tag with a type, then break so it can be handled
    if ($test_tag == 'override') {
      $video_info = 'override';
      break;
    }

    // if we have a clip of that format type, then return it
    if ($data[$test_tag]['url']) {
      $video_info = $data[$test_tag];
      break;
    }
  }

  return $video_info;
}

/**
 *  providers may supply an enclosure for rss feeds. this expects something in a file format, so would be an object
 *  in the format of $file->filepath, $file->filesize, and $file->filemime. calls the providers hook video_cck_PROVIDER_rss
 */
function video_cck_bliptv_rss($item, $teaser = NULL) {
  if ($item['value']) {
    if ($item['data']['video_cck_bliptv_data_version'] >= 1) {
      $data = $item['data'];
    }
    else {
      $data = video_cck_bliptv_data(NULL, $item);
    }

    // get the preferred type for the rss feed
    $var = 'video_cck_bliptv_rss_tag';
    $video_info = _video_cck_bliptv_get_video_info_from_preferred_tag($data, $var);

    // grab the preferred filetype rather than tag, so .mov rather than 'web'
    if ($video_info == 'override') {
       $var = 'video_cck_bliptv_rss_format';
      $video_info = _video_cck_bliptv_get_video_info_from_preferred_tag($data, $var);
    }

    // default to flv if there's no available clip format
    if (is_null($video_info) || ($video_info == 'override') && $video_info != 'none') {
      $video_info = $data['flv'];
    }

    if (is_array($video_info)) {
      // Begin building file object.
      $file = new stdClass();
      // Create temporary name/path for newly uploaded files.
      $file->filepath = $video_info['url'];
      $file->filesize = $video_info['size'];
      $file->filemime = $video_info['mime'];

      return $file;
    }
  }
}

function theme_video_cck_bliptv_flash($embed, $width, $height, $field, $item, $autoplay, $flv, $thumbnail) {
  if ($embed) {
    $autoplay = $autoplay ? 'autoStart=true' : 'autoStart=false';
    $output .= '<object type="application/x-shockwave-flash" height="' . $height . '" width="' .$width . '" data="http://blip.tv/scripts/flash/blipplayer.swf?' . $autoplay . '&amp;file=' . $flv . '%3Fsource%3D3"  >
      <param name="movie" value="http://blip.tv/scripts/flash/blipplayer.swf?' . $autoplay . '&amp;file=' . $flv . '%3Fsource%3D3"  />
      <param name="allowScriptAcess" value="always" />
      <param name="allowFullScreen" value="true" />
      <param name="quality" value="high" />
      <param name="bgcolor" value="#FFFFFF" />
      <param name="scale" value="noScale" />
      <param name="salign" value="TL" />
      <param name="wmode" value="transparent" />
    </object>';
  }
  return $output;
}

function video_cck_bliptv_thumbnail($field, $item, $formatter, $node, $width, $height) {
  if ($item['data']['video_cck_bliptv_data_version'] >= 1) {
    $tn = $item['data']['thumbnail']['url'];
  }
  else {
    $tn = $item['data']['thumbnail'];
  }
  return $tn;
}

function video_cck_bliptv_video($embed, $width, $height, $field, $item, $autoplay) {
  if ($item['data']['video_cck_bliptv_data_version'] >= 1) {
    $flv = $item['data']['flv']['url'];
    $thumbnail = $item['data']['thumbnail']['url'];
  }
  else {
    $flv = $item['data']['flv'];
    $thumbnail = $item['data']['thumbnail'];
  }
  $post_id = $item['data']['post_id'];
  $output = theme('video_cck_bliptv_flash', $post_id, $width, $height, $field, $item, $autoplay, $flv, $thumbnail);
  return $output;
}

function video_cck_bliptv_preview($embed, $width, $height, $field, $item, $autoplay) {
  if ($item['data']['video_cck_bliptv_data_version'] >= 1) {
    $flv = $item['data']['flv']['url'];
    $thumbnail = $item['data']['thumbnail']['url'];
  }
  else {
    $flv = $item['data']['flv'];
    $thumbnail = $item['data']['thumbnail'];
  }
  $post_id = $item['data']['post_id'];
  $output = theme('video_cck_bliptv_flash', $post_id, $width, $height, $field, $item, $autoplay, $flv, $thumbnail);
  return $output;
}

