<?php
/* $Id: image.php,v 1.9 2007/12/09 14:12:54 tjfulopp Exp $ */

if ( strpos( base64_decode( $_GET['imgp'] ), ":" ) !== false ) { # Fixing a possible code injection problem.
     header("HTTP/1.0 404 Not Found");
     exit();
   }

drupalize();
function drupalize() {
 while (!@stat('./includes/bootstrap.inc')) {
   chdir ('..');
 }
 require_once './includes/bootstrap.inc';
 drupal_bootstrap(DRUPAL_BOOTSTRAP_FULL);
#drupal_cron_run();
}

$bgcachexpire = 3600*24*7; # Cache expiration time.

#chdir ('../../../../');
#require_once './includes/bootstrap.inc';
#require_once '../../../../../includes/bootstrap.inc';
#drupal_bootstrap(DRUPAL_BOOTSTRAP_FULL);

#if ( $_SERVER['SERVER_ADDR'] == '64.13.192.90' ) {
if ( variable_get('brilliant_gallery_cache', 'd') == 'f' ) {
     #echo '.....................' . $_SERVER['SERVER_ADDR'];
     $my_data = resizeimage_wrapper_filecache();
   } else {
     $my_data = resizeimage_wrapper_dbcache();
   }

header($my_data[0]);
echo $my_data[1];

function resizeimage_wrapper_filecache() {
  global $bgcachexpire;
  $bgcacheid = 'bg_' . md5($_GET['imgp'] . $_GET['imgw'] . $_GET['imgh']);
  #echo $bgcacheid;
  #echo '. 0.... ';
  # Tested that both relative (eg sites/all/files/cache) and absolute (eg /home/data/tmp) tmp path settings work OK here.
  $cachedfile = file_directory_temp() . '/' . $bgcacheid;
  #$cachedfile = realpath(file_directory_temp() . '/' . $bgcacheid);
  #echo file_directory_temp()  . '/' . $bgcacheid;
  #echo " .... ";
  #echo $cachedfile;
  $lastchanged = (file_exists($cachedfile) ? filemtime( $cachedfile ) : false); # See http://drupal.org/node/194923
  if ( $lastchanged === false or ( time() - $lastchanged > ( $bgcachexpire ) ) ) {
       #echo '. 1.... ';
       # Cache file does not exist or is too old.
       $my_data = resizeimage ( $_GET['imgp'], $_GET['imgw'], $_GET['imgh'] );
       # Now put $my_data to cache!
       $fh = fopen( $cachedfile, "w+" );
        fwrite( $fh, $my_data );
        fclose( $fh ); 
       $my_data = unserialize( base64_decode( $my_data ) );
     } else {
       #echo '. 2.... ';
       # Cache file exists.
       $my_data = unserialize( base64_decode( file_get_contents( $cachedfile ) ) );
     }
  return $my_data;
}

function resizeimage_wrapper_dbcache($reset = FALSE) {
  global $bgcachexpire;
  global $user;
   $userId = $user->uid;
  $bgcacheid = 'bg_' . md5($_GET['imgp'] . $_GET['imgw'] . $_GET['imgh']);
  #echo $bgcacheid;
  static $my_data;
  #echo '0.... ';
  if (!isset($my_data) || $reset) {
     if (!$reset && ($cache = cache_get($bgcacheid)) && !empty($cache->data)) {
      #$my_data = $cache->data;
      $my_data = unserialize(base64_decode( $cache->data ));
       #echo '-1.... ' . $my_data;
    }
    else {
      // Do your expensive calculations here, and populate $my_data
      // with the correct stuff..
      $my_data = resizeimage ( $_GET['imgp'], $_GET['imgw'], $_GET['imgh'] );
       #echo ' -2.... ' . $my_data;
      cache_set($bgcacheid, 'cache', $my_data, time() + $bgcachexpire);
      $my_data = unserialize(base64_decode( $my_data ));
    }
  }
  return $my_data;
}

function resizeimage( $imgp, $imgw, $imgh ) {
         $imagepath = base64_decode( $imgp );
         #echo '.... ' . base64_decode( $imgp );
         #flush();die(' stop!');
         $suffix = strtolower(substr($imagepath, -4)); # Thanks to Michał Albrecht!
         if ( $suffix == ".gif" ) {
														#Header("Content-type: image/gif");
              $head = "Content-type: image/gif";
              $img = imagecreatefromgif( $imagepath );
            } else if ( $suffix == ".jpg" or $suffix == "jpeg" ) { # Thanks to Michał Albrecht!
														#Header("Content-type: image/jpeg");
              $head = "Content-type: image/jpeg";
              $img = imagecreatefromjpeg( $imagepath );
            } else if ( $suffix == ".png" ) {
														#Header("Content-type: image/png");
              $head = "Content-type: image/png";
              $img = imagecreatefrompng( $imagepath );
            }
         # Resize the image
           $src_h = ImageSY( $img );
           $src_w = ImageSX( $img );
           $dst_img = imagecreatetruecolor( $imgw, $imgh );
           imagecopyresampled( $dst_img, $img, 0, 0, 0, 0, $imgw, $imgh, $src_w, $src_h );
           $img = $dst_img;
         imageinterlace ( $img, 1 );
         imagecolortransparent ( $img );
         ob_start();
           if ( $suffix == ".gif" ) {
                Imagegif($img);
              } else if ( $suffix == ".jpg" or $suffix == ".jpeg" ) {
                Imagejpeg($img,'',80);
              } else if ( $suffix == ".png" ) {
                Imagepng($img);
              }
         $result = ob_get_clean();
         #ImageDestroy($img);
         $result = mysql_escape_string(base64_encode(serialize(array($head, $result))));
         return $result;
       }

?>
