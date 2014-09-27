<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="<?php print $language ?>" lang="<?php print $language ?>">
<head>
    <title><?php print $head_title ?></title>
    <?php print $head ?>
    <?php print $styles ?>
    <?php print $scripts ?>
	<!--[if lt IE 7]>
	<link rel="stylesheet" href="ie-gif.css" type="text/css" />
	<![endif]-->
</head>
<body>
<div id="page">
	<div id="wrapper">
	<div id="header">
		<a href="<?php print check_url($base_path); ?>"><?php if ($logo) { print '<img src="'. check_url($logo) .'" alt="'. $site_title .'" id="logo" />'; } ?></a>	
		<h2><a href="<?php print check_url($base_path); ?>"><?php print check_plain($site_name); ?></a></h2>
		<div class="slogan"><?php print check_plain($site_slogan); ?></div>	
		<?php if ($search_box): ?><?php print $search_box ?><?php endif; ?>	
    </div><!-- /header -->
    <div id="left-col">
        <div id="nav">
        <ul>
			<?php if (is_array($primary_links)) : ?>
			<?php foreach($primary_links AS $links){ echo '<li class="page_item">'. l($links['title'], $links['href']). '</li>'; }
			endif; ?>
        </ul>
		</div><!-- /nav -->
		<div id="content">
			<?php if ($mission != ""): ?>
			<div class="mission"><?php print $mission ?></div>
			<?php endif; ?>
			<?php print $header; ?>			
			<?php if ($title != ""): ?>
			<h1><?php print $title ?></h1>
			<?php endif; ?>			
			<?php if ($tabs != ""): ?>
			<?php print $tabs ?>
			<?php endif; ?>						
			<?php if ($help != ""): ?>
			<p id="help"><?php print $help ?></p>
			<?php endif; ?>
			<?php if ($messages != ""): ?>
			<div id="message"><?php print $messages ?></div>
			<?php endif; ?>
			<?php print($content) ?>	
		</div><!--/content -->
		<div id="footer"><?php print $footer_message ?> | Theme &amp; Icons by <a href="http://www.ndesign-studio.com">N.Design Studio</a>, mod. <a href="http://www.salle.ru/">salle</a></div>
    </div><!--/left-col -->		
	<div id="sidebar" class="dbx-group" >
	  <?php if ($sidebar_left): ?>
          <?php print $sidebar_left ?>
      <?php endif; ?>	  
	  <?php if ($sidebar_right): ?>
          <?php print $sidebar_right ?>
      <?php endif; ?> 
	</div><!--/sidebar -->
    <hr class="hidden" />
</div><!--/wrapper -->
</div><!--/page -->
</body>
</html>